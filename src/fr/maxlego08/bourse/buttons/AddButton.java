package fr.maxlego08.bourse.buttons;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.buttons.bourse.BourseButton;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public class AddButton extends ZButton {

	private final BoursePlugin plugin;
	private final int amount;

	/**
	 * @param plugin
	 * @param amount
	 */
	public AddButton(BoursePlugin plugin, int amount) {
		super();
		this.plugin = plugin;
		this.amount = amount;
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {

		BourseButton button = plugin.getButton(player);
		ItemStack itemStack = button.getItemStack().build(player);

		int newAmount = button.getAmount(player) + this.amount;
		if (newAmount > itemStack.getMaxStackSize()) {
			newAmount = itemStack.getMaxStackSize();
		}

		button.setAmount(inventory, player, newAmount);

	}

}
