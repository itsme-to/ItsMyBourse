package fr.maxlego08.bourse.buttons;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.buttons.bourse.BourseButton;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public class SetToMaxButton extends ZButton {

	private final BoursePlugin plugin;

	/**
	 * @param plugin
	 */
	public SetToMaxButton(Plugin plugin) {
		super();
		this.plugin = (BoursePlugin) plugin;
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {

		BourseButton button = this.plugin.getButton(player);
		ItemStack itemStack = button.getItemStack().build(player);
		button.setAmount(inventory, player, itemStack.getMaxStackSize());
	}

	@Override
	public ItemStack getCustomItemStack(Player player) {
		ItemStack itemStack = super.getCustomItemStack(player);

		ItemMeta itemMeta = itemStack.getItemMeta();

		String displayName = itemMeta.getDisplayName();

		displayName = displayName.replace("%maxStack%", String.valueOf(itemStack.getMaxStackSize()));
		displayName = displayName.replace("%maxstack%", String.valueOf(itemStack.getMaxStackSize()));

		itemMeta.setDisplayName(displayName);

		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}

}
