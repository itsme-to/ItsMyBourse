package fr.maxlego08.bourse.buttons;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.buttons.bourse.BourseButton;
import fr.maxlego08.bourse.save.Config;
import fr.maxlego08.menu.button.ZButton;

public class ShowItemButton extends ZButton {

	private final BoursePlugin plugin;
	private final List<String> lore;

	/**
	 * @param plugin
	 * @param lore
	 */
	public ShowItemButton(BoursePlugin plugin, List<String> lore) {
		super();
		this.plugin = plugin;
		this.lore = lore;
	}

	@Override
	public ItemStack getCustomItemStack(Player player) {
		BourseButton button = this.plugin.getButton(player);

		ItemStack itemStack = button.getItemStack().build(player);

		int amount = button.getAmount(player);
		itemStack.setAmount(amount);

		ItemMeta itemMeta = itemStack.getItemMeta();

		itemMeta.setLore(color(this.getLore().stream().map(line -> {
			
			line = line.replace("%sellPrice%", format(button.getSellPrice(player) * amount, Config.priceFormat));
			line = line.replace("%buyPrice%", format(button.getBuyPrice() * amount, Config.priceFormat));
			
			return line;
		}).collect(Collectors.toList())));

		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}

	public List<String> getLore() {
		return lore;
	}

}
