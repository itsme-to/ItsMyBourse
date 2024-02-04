package fr.maxlego08.bourse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.bourse.buttons.bourse.BourseButton;
import fr.maxlego08.bourse.save.Config;
import fr.maxlego08.bourse.zcore.enums.Message;
import fr.maxlego08.bourse.zcore.utils.ZUtils;
import fr.maxlego08.bourse.zcore.utils.storage.Persist;
import fr.maxlego08.bourse.zcore.utils.storage.Saveable;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;

public class BourseManager extends ZUtils implements Saveable {

	private transient final BoursePlugin plugin;
	private transient final Map<Player, String> playerCategories;

	private static Map<String, Double> prices = new HashMap<String, Double>();
	private static Map<String, List<SellAmount>> amounts = new HashMap<String, List<SellAmount>>();

	/**
	 * @param plugin
	 */
	public BourseManager(BoursePlugin plugin) {
		super();
		this.plugin = plugin;
		this.playerCategories = new HashMap<>();
	}

	@Override
	public void save(Persist persist) {
		persist.save(this, "storages");
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(this, BourseManager.class, "storages");

		this.changePrices();
	}

	private String toString(ItemStack itemStack) {
		return itemStack.getType().name();
	}

	/**
	 * Permet de retourner le prix
	 * 
	 * @param itemStack
	 * @param defaultValue
	 * @return value
	 */
	public double getPrice(ItemStack itemStack, double defaultValue) {
		return prices.getOrDefault(toString(itemStack), defaultValue);
	}

	/**
	 * Permet de modifier le prix
	 * 
	 * @param itemStack
	 * @param value
	 */
	public void setPrice(ItemStack itemStack, double value) {
		prices.put(toString(itemStack), value);
	}

	/**
	 * Permet de retourner le nombre d'item vendu en 24 heures
	 * 
	 * @param itemStack
	 * @return amount
	 */
	public long getAmount(ItemStack itemStack) {
		List<SellAmount> sells = amounts.getOrDefault(toString(itemStack), new ArrayList<SellAmount>());

		Iterator<SellAmount> iterator = sells.iterator();
		while (iterator.hasNext()) {
			SellAmount amount = iterator.next();
			if (amount.hasExpired()) {
				iterator.remove();
			}
		}

		return sells.stream().mapToLong(SellAmount::getAmount).sum();
	}

	/**
	 * Permet d'ajouter un nombre en vente
	 * 
	 * @param itemStack
	 * @param amount
	 */
	public void addAmount(ItemStack itemStack, long amount) {

		List<SellAmount> sells = amounts.getOrDefault(toString(itemStack), new ArrayList<SellAmount>());
		SellAmount sellAmount = new SellAmount(amount);
		sells.add(sellAmount);

		amounts.put(toString(itemStack), sells);
	}

	/**
	 * Permet d'ouvrir l'inventaire des bourses
	 * 
	 * @param player
	 */
	public void openBourse(Player player, String category) {

		InventoryManager inventoryManager = this.plugin.getIManager();
		Optional<Inventory> optional = inventoryManager.getInventory(this.plugin, category);
		if (!optional.isPresent()) {
			message(player, Message.INVENTORY_NOT_FOUND);
			return;
		}

		inventoryManager.openInventory(player, optional.get());
		this.playerCategories.put(player, category);
	}

	public void openBourse(Player player) {
		this.openBourse(player, this.playerCategories.getOrDefault(player, ""));
	}

	@SuppressWarnings("deprecation")
	public Optional<BourseButton> getButton(ItemStack itemStack) {
		InventoryManager inventoryManager = this.plugin.getIManager();
		return Config.categories.stream().map(category -> inventoryManager.getInventory(this.plugin, category))
				.filter(Optional::isPresent).map(Optional::get).map(i -> i.getButtons(BourseButton.class))
				.flatMap(List::stream).filter(button -> {
					ItemStack item = button.getItemStack().build(null);
					return item.isSimilar(itemStack) && (item.getData().getData() == itemStack.getData().getData());
				}).findFirst();
	}

	private void changePrices() {

		InventoryManager inventoryManager = this.plugin.getIManager();
		this.scheduleFix(this.plugin, Config.taskChangePricemilliSeconds, (task, canRun) -> {

			Config.categories.forEach(category -> {

				Optional<Inventory> optional = inventoryManager.getInventory(this.plugin, category);

				if (!optional.isPresent()) {
					return;
				}

				Inventory inventory = optional.get();
				List<BourseButton> buttons = inventory.getButtons(BourseButton.class);

				int a = buttons.size();

				if (a == 0) {
					return;
				}

				long c = buttons.stream().mapToLong(button -> this.getAmount(button.getItemStack().build(null))).sum();

				if (c == 0) {
					buttons.forEach(
							button -> this.setPrice(button.getItemStack().build(null), button.getInitialPrice()));
					return;
				}

				long M = 100 / a;

				buttons.forEach(button -> {

					long b = this.getAmount(button.getItemStack().build(null));
					double P = button.getInitialPrice();

					if (b <= Config.minItemChange) {
						this.setPrice(button.getItemStack().build(null), P);
						return;
					}

					double m = percent(b, c);

					if (m == 0) {
						this.setPrice(button.getItemStack().build(null), P);
						return;
					}

					double p = M / m * P;

					this.setPrice(button.getItemStack().build(null), p);

				});
			});
		});

	}

}
