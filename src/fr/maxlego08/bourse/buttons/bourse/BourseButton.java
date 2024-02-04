package fr.maxlego08.bourse.buttons.bourse;

import org.bukkit.entity.Player;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public interface BourseButton extends Button {

	double getBuyPrice();
	
	double getInitialPrice();
	
	void sell(Player player, int amount);
	
	void buy(Player player, int amount);
	
	int getAmount(Player player);

	void setAmount(InventoryDefault inventoryDefault, Player player, int maxStackSize);

	double getSellPrice(Player player);

}
