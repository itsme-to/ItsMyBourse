package fr.maxlego08.bourse.buttons;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.buttons.bourse.BourseButton;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public class RemoveButton extends ZButton {

	private final BoursePlugin plugin;
	private final int amount;

	/**
	 * @param plugin
	 * @param amount
	 */
	public RemoveButton(BoursePlugin plugin, int amount) {
		super();
		this.plugin = plugin;
		this.amount = amount;
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {

		BourseButton button = this.plugin.getButton(player);
		
		int newAmount = button.getAmount(player) - this.amount;
		if (newAmount < 1){
			newAmount = 1;
		}
		
		button.setAmount(inventory, player, newAmount);
		
	}

}
