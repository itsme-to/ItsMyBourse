package fr.maxlego08.bourse.buttons.bourse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.maxlego08.bourse.BourseManager;
import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.buttons.ShowItemButton;
import fr.maxlego08.bourse.save.Config;
import fr.maxlego08.bourse.zcore.enums.Message;
import fr.maxlego08.bourse.zcore.utils.ZUtils;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.button.ZPlaceholderButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public class ZBourseButton extends ZButton implements BourseButton {

	private final Map<Player, Integer> playerAmounts = new HashMap<Player, Integer>();

	private final double initialPrice;
	private final double buyPrice;

	private final BoursePlugin plugin;
	private final BourseManager manager;
	private final ZUtils utils = new ZUtils() {
	};

	/**
	 * @param initialPrice
	 * @param stage
	 * @param multiplyFactorStage
	 */
	public ZBourseButton(BoursePlugin plugin, double initialPrice, double buyPrice) {
		super();
		this.initialPrice = initialPrice;
		this.buyPrice = buyPrice;
		this.plugin = plugin;
		this.manager = plugin.getManager();
	}

	@Override
	public double getInitialPrice() {
		return this.initialPrice;
	}

	@Override
	public double getBuyPrice() {
		return this.buyPrice;
	}

	@Override
	public ItemStack getCustomItemStack(Player player) {
		ItemStack itemStack = super.getCustomItemStack(player);

		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(this.getLore(player));
		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}

	/**
	 * Permet de modifier le lore
	 * 
	 * @param player
	 * @return lore
	 */
	private List<String> getLore(Player player) {

		ItemStack itemStack = super.getCustomItemStack(player);

		ItemMeta itemMeta = itemStack.getItemMeta();

		List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<String>();

		Config.defaultLore.forEach(line -> {

			double price = this.manager.getPrice(itemStack, this.initialPrice);

			double percent = Math.abs(100 - this.percent(price, this.initialPrice));

			String fluctuation = price >= this.initialPrice
					? Config.upColor.replace("%percent%", format(percent, Config.priceFormat))
					: Config.downColor.replace("%percent%", format(percent, Config.priceFormat));

			double diffAsDouble = Math.abs(this.initialPrice - price);
			String diff = price >= this.initialPrice
					? Config.upNumberColor.replace("%value%", format(diffAsDouble, Config.priceFormat))
					: Config.downNumberColor.replace("%value%", format(diffAsDouble, Config.priceFormat));

			line = line.replace("%fluctuation%", fluctuation);
			line = line.replace("%diff%", diff);
			line = line.replace("%price%", this.format(price, Config.priceFormat));
			line = line.replace("%buyPrice%", this.format(this.buyPrice, Config.priceFormat));
			line = line.replace("%defaultPrice%", this.format(this.initialPrice, Config.priceFormat));
			line = line.replace("%itemAmount%", String.valueOf(this.count(player.getInventory(), itemStack)));
			line = line.replace("%count%", this.format(this.manager.getAmount(itemStack), '.'));

			lore.add(line);

		});

		return color(lore);
	}

	@Override
	public void onInventoryOpen(Player player, InventoryDefault inventory) {

		long ms = Config.taskChangePricemilliSeconds;

		this.scheduleFix(this.plugin, ms, ms, (task, canRun) -> {

			if (!canRun) {
				return;
			}

			if (inventory.isClose()) {

				task.cancel();

			} else {

				ItemStack itemStack = inventory.getSpigotInventory().getItem(this.getSlot());

				if (itemStack != null) {

					ItemMeta itemMeta = itemStack.getItemMeta();
					itemMeta.setLore(this.getLore(player));
					itemStack.setItemMeta(itemMeta);

				}

			}

		});

	}

	@Override
	public void onMiddleClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {

		this.sell(player, 0);

	}

	@Override
	public void onRightClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {

		InventoryManager inventoryManager = this.plugin.getIManager();
		Optional<fr.maxlego08.menu.api.Inventory> optional = inventoryManager.getInventory(this.plugin, "sell");
		if (!optional.isPresent()) {
			this.utils.message(player, Message.INVENTORY_NOT_FOUND);
			return;
		}

		this.playerAmounts.put(player, 1);
		this.plugin.setButton(player, this);
		inventoryManager.openInventory(player, optional.get(), 1, inventory.getInventory());

	}

	@Override
	public void onLeftClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {

		InventoryManager inventoryManager = this.plugin.getIManager();
		Optional<fr.maxlego08.menu.api.Inventory> optional = inventoryManager.getInventory(this.plugin, "buy");
		if (!optional.isPresent()) {
			this.utils.message(player, Message.INVENTORY_NOT_FOUND);
			return;
		}

		this.playerAmounts.put(player, 1);
		this.plugin.setButton(player, this);
		inventoryManager.openInventory(player, optional.get(), 1, inventory.getInventory());

	}

	/**
	 * Permet de vendre
	 * 
	 * @param player
	 * @param amount
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void sell(Player player, int amount) {

		ItemStack itemStack = super.getItemStack().build(player);
		int itemAmount = this.count(player.getInventory(), itemStack);

		if (itemAmount <= 0) {
			this.utils.message(player, Message.NOT_ITEMS);
			return;
		}

		if (itemAmount < amount) {
			this.utils.message(player, Message.NOT_ENOUGH_ITEMS);
			return;
		}

		itemAmount = amount == 0 ? itemAmount
				: itemAmount < amount ? amount : amount > itemAmount ? itemAmount : amount;
		int realAmount = itemAmount;

		double price = this.getSellPrice(player);
		double currentPrice = price * realAmount;

		int slot = 0;

		for (ItemStack is : player.getInventory().getContents()) {

			if (is != null && is.isSimilar(itemStack) && itemAmount > 0 && (is.getData().getData() == itemStack.getData().getData())) {

				int currentAmount = is.getAmount() - itemAmount;
				itemAmount -= is.getAmount();

				if (currentAmount <= 0) {
					if (slot == 40)
						player.getInventory().setItemInOffHand(null);
					else
						player.getInventory().removeItem(is);
				} else
					is.setAmount(currentAmount);
			}
			slot++;
		}

		player.updateInventory();
		this.plugin.getEconomy().depositPlayer(player, currentPrice);

		this.manager.addAmount(itemStack, realAmount);

		this.utils.message(player, Message.SELL_ITEM, "%amount%", String.valueOf(realAmount), "%item%",
				getItemName(itemStack), "%price%", format(currentPrice));
	}

	/**
	 * Permet de conter
	 * 
	 * @param inventory
	 * @param currentItemStack
	 * @return price
	 */
	@SuppressWarnings("deprecation")
	protected int count(Inventory inventory, ItemStack currentItemStack) {
		int count = 0;
		ItemStack[] var7;
		int var6 = (var7 = inventory.getContents()).length;

		for (int var5 = 0; var5 < var6; ++var5) {
			ItemStack itemStack = var7[var5];
			if (itemStack != null && itemStack.isSimilar(currentItemStack) && (currentItemStack.getData().getData() == itemStack.getData().getData())) {
				count += itemStack.getAmount();
			}
		}

		return count;
	}

	@Override
	public void buy(Player player, int amount) {

		double currentPrice = this.getBuyPrice() * amount;

		if (currentPrice < 0) {
			return;
		}

		if (!this.plugin.getEconomy().has(player, currentPrice)) {
			this.utils.message(player, Message.NOT_ENOUGH_MONEY);
			return;
		}

		if (hasInventoryFull(player)) {
			this.utils.message(player, Message.NOT_ENOUGH_PLACE);
			return;
		}

		this.plugin.getEconomy().withdrawPlayer(player, currentPrice);

		ItemStack itemStack = super.getItemStack().build(player);
		itemStack.setAmount(amount);

		give(player, papi(itemStack, player));

		this.utils.message(player, Message.BUY_ITEM, "%amount%", String.valueOf(amount), "%item%",
				getItemName(itemStack), "%price%", format(currentPrice));

	}

	@Override
	public void setAmount(InventoryDefault inventoryDefault, Player player, int amount) {
		this.playerAmounts.put(player, amount);

		fr.maxlego08.menu.api.Inventory inventory = inventoryDefault.getInventory();
		inventory.getButtons(ShowItemButton.class).forEach(button -> {

			int slot = button.getSlot();
			ItemStack itemStack = button.getCustomItemStack(player);
			inventoryDefault.getSpigotInventory().setItem(slot, itemStack);

		});

	}

	@Override
	public int getAmount(Player player) {
		return this.playerAmounts.getOrDefault(player, 1);
	}

	@Override
	public double getSellPrice(Player player) {
		return this.manager.getPrice(this.getItemStack().build(player), this.initialPrice);
	}

}
