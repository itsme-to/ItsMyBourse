package fr.maxlego08.bourse.sell;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.maxlego08.bourse.BourseManager;
import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.buttons.bourse.BourseButton;
import fr.maxlego08.bourse.listener.ListenerAdapter;
import fr.maxlego08.bourse.zcore.enums.Message;
import fr.maxlego08.bourse.zcore.logger.Logger;
import fr.maxlego08.bourse.zcore.utils.loader.ItemStackLoader;
import fr.maxlego08.bourse.zcore.utils.loader.Loader;
import fr.maxlego08.bourse.zcore.utils.nms.ItemStackCompound;
import fr.maxlego08.bourse.zcore.utils.nms.ItemStackCompound.EnumReflectionCompound;
import fr.maxlego08.bourse.zcore.utils.storage.Persist;
import fr.maxlego08.bourse.zcore.utils.storage.Saveable;

public class SellManager extends ListenerAdapter implements Saveable {

	private final Map<String, SellItem> items = new HashMap<String, SellItem>();
	private final BoursePlugin plugin;
	private ItemStackCompound compound;

	private final String KEY_BOOST = "sellstick:boost";
	private final String KEY_DURA = "sellstick:dura";
	private final String KEY_NAME = "sellstick:name";

	/**
	 * @param plugin
	 */
	public SellManager(BoursePlugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public void save(Persist persist) {

	}

	@Override
	public void load(Persist persist) {

		this.items.clear();
		this.compound = new ItemStackCompound(EnumReflectionCompound.V1_8_8);

		YamlConfiguration configuration = YamlConfiguration
				.loadConfiguration(new File(this.plugin.getDataFolder(), "sell.yml"));
		Loader<ItemStack> loader = new ItemStackLoader();

		ConfigurationSection configurationSection = configuration.getConfigurationSection("items.");
		for (String key : configurationSection.getKeys(false)) {

			String path = "items." + key + ".";
			ItemStack itemStack = loader.load(configuration, path + "item.");
			String name = configuration.getString(path + "name");
			double boost = configuration.getDouble(path + "boost");
			int durability = configuration.getInt(path + "durability");

			SellItem item = new SellItem(itemStack, boost, durability, name);
			this.items.put(name, item);
		}

		Logger.info("Chargement de " + this.items.size() + " sellstick");

	}

	/**
	 * Permet de récupérer un item
	 * 
	 * @param sender
	 * @param player
	 * @param name
	 */
	public void give(CommandSender sender, Player player, String name) {

		if (!this.items.containsKey(name)) {
			message(sender, Message.SELL_ITEM_ERROR_NOTFOUND, "%item%", name);
			return;
		}

		SellItem item = this.items.get(name);
		ItemStack itemStack = item.getItemStack().clone();

		itemStack = replaceDurability(itemStack, item, item.getDurability());
		give(player, itemStack);

		String itemName = name(itemStack);

		message(sender, Message.SELL_ITEM_SUCCESS_GIVE, "%item%", itemName, "%player%", player.getName());
		message(player, Message.SELL_ITEM_SUCCESS_SENDER, "%item%", itemName, "%player%", player.getName());

	}

	public List<String> names() {
		return new ArrayList<>(this.items.keySet());
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onInteract(PlayerInteractEvent event, Player player) {

		if (event.isCancelled()) {
			return;
		}

		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

			Block block = event.getClickedBlock();
			ItemStack itemStack = player.getInventory().getItemInMainHand();

			if (this.compound.isKey(itemStack, this.KEY_BOOST)) {

				event.setCancelled(true);

				Chest chest = (Chest) block.getState();
				Inventory inventory = chest.getInventory();

				double boost = this.compound.getDouble(itemStack, this.KEY_BOOST);

				boolean sellContent = this.sellContent(inventory, player, boost);
				if (sellContent) {

					int durability = this.compound.getInt(itemStack, this.KEY_DURA) - 1;

					if (durability < 0) {
						removeItemInHand(player);
					} else {

						SellItem item = this.items.get(this.compound.getString(itemStack, this.KEY_NAME));
						if (item == null) {
							removeItemInHand(player);
						} else {
							itemStack = this.replaceDurability(item.getItemStack().clone(), item, durability);
							player.getInventory().setItemInMainHand(itemStack);
						}

					}

					player.updateInventory();

				} else {

					message(player, Message.SELL_ITEM_ERROR_EMPTY);

				}

			}

		}

	}

	private boolean sellContent(Inventory inventory, Player player, double boost) {

		double currentPrice = 0;
		int items = 0;

		BourseManager manager = this.plugin.getManager();
		for (int slot = 0; slot != inventory.getContents().length; slot++) {

			ItemStack itemStack = inventory.getContents()[slot];
			if (itemStack != null) {
				Optional<BourseButton> optional = manager.getButton(itemStack);

				if (optional.isPresent()) {

					BourseButton bourseButton = optional.get();
					double sellPrice = bourseButton.getSellPrice(player);
					int amount = itemStack.getAmount();

					currentPrice += (sellPrice * amount);
					items += amount;

					manager.addAmount(itemStack, amount);
					inventory.setItem(slot, new ItemStack(Material.AIR));

				}

			}

		}

		if (items > 0) {

			this.plugin.getEconomy().depositPlayer(player, currentPrice * boost);
			message(player, Message.SELL_ITEM_ALL, "%amount%", String.valueOf(items), "%price%",
					format(currentPrice * boost));

			return true;
		}

		return false;
	}

	private ItemStack replaceDurability(ItemStack itemStack, SellItem item, int dura) {
		
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta.hasLore()) {
			itemMeta.setLore(itemMeta.getLore().stream()
					.map(e -> e.replace("%dura%", String.valueOf(dura))).collect(Collectors.toList()));
		}
		itemStack.setItemMeta(itemMeta);

		itemStack = this.compound.setString(itemStack, this.KEY_NAME, item.getName());
		itemStack = this.compound.setDouble(itemStack, this.KEY_BOOST, item.getBoost());
		itemStack = this.compound.setInt(itemStack, this.KEY_DURA, dura);
		return itemStack;
	}

}
