package fr.maxlego08.bourse.buttons;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.buttons.bourse.BourseButton;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public class SellConfirmButton extends ZButton {

	private final BoursePlugin plugin;

	/**
	 * @param plugin
	 */
	public SellConfirmButton(Plugin plugin) {
		super();
		this.plugin = (BoursePlugin) plugin;
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {

		BourseButton button = plugin.getButton(player);
		button.sell(player, button.getAmount(player));

	}

}
