package fr.maxlego08.bourse;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import fr.maxlego08.bourse.buttons.AddButton;
import fr.maxlego08.bourse.buttons.BuyConfirmButton;
import fr.maxlego08.bourse.buttons.RemoveButton;
import fr.maxlego08.bourse.buttons.SellConfirmButton;
import fr.maxlego08.bourse.buttons.SetToMaxButton;
import fr.maxlego08.bourse.buttons.SetToOneButton;
import fr.maxlego08.bourse.buttons.bourse.BourseButton;
import fr.maxlego08.bourse.buttons.bourse.BourseLoader;
import fr.maxlego08.bourse.buttons.loader.ActionLoader;
import fr.maxlego08.bourse.buttons.loader.ShowLoader;
import fr.maxlego08.bourse.command.CommandManager;
import fr.maxlego08.bourse.command.commands.CommandBourse;
import fr.maxlego08.bourse.inventory.ZInventoryManager;
import fr.maxlego08.bourse.listener.AdapterListener;
import fr.maxlego08.bourse.save.Config;
import fr.maxlego08.bourse.save.MessageLoader;
import fr.maxlego08.bourse.sell.SellManager;
import fr.maxlego08.bourse.zcore.ZPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.button.loader.PluginLoader;
import fr.maxlego08.menu.exceptions.InventoryException;
import net.milkbowl.vault.economy.Economy;

/**
 * System to create your plugins very simply Projet:
 * https://github.com/Maxlego08/TemplatePlugin
 * 
 * @author Maxlego08
 *
 */
public class BoursePlugin extends ZPlugin {

	private final BourseManager manager = new BourseManager(this);
	private InventoryManager inventoryManager;
	private ButtonManager buttonManager;
	private Economy economy;

	private final SellManager sellManager = new SellManager(this);

	private Map<Player, BourseButton> buttons = new HashMap<Player, BourseButton>();

	@Override
	public void onEnable() {

		this.preEnable();

		this.commandManager = new CommandManager(this);
		super.inventoryManager = new ZInventoryManager(this);

		this.inventoryManager = this.getProvider(InventoryManager.class);
		this.buttonManager = this.getProvider(ButtonManager.class);
		this.economy = this.getProvider(Economy.class);

		if (!new File(this.getDataFolder(), "sell.yml").exists()) {
			this.saveResource("sell.yml", false);
		}

		this.registerCommand("bourse", new CommandBourse(this));

		/* Add Listener */

		this.addListener(new AdapterListener(this));
		this.addListener(super.inventoryManager);
		this.addListener(this.sellManager);

		/* Add Saver */
		this.addSave(Config.getInstance());
		this.addSave(new MessageLoader(this));
		this.addSave(this.manager);
		// addSave(new CooldownBuilder());

		this.getSavers().forEach(saver -> saver.load(this.getPersist()));

		this.loadInventories();

		this.postEnable();
	}

	@Override
	public void onDisable() {

		this.preDisable();

		this.getSavers().forEach(saver -> saver.save(this.getPersist()));

		this.postDisable();

	}

	public ButtonManager getButtonManager() {
		return buttonManager;
	}

	public InventoryManager getIManager() {
		return inventoryManager;
	}

	public Economy getEconomy() {
		return economy;
	}

	public void loadInventories() {

		File folder = new File(this.getDataFolder(), "inventories");
		if (!folder.exists()) {
			folder.mkdirs();
		}

		this.buttonManager.unregisters(this);
		this.buttonManager.register(new BourseLoader(this));

		this.buttonManager.register(new ActionLoader(this, RemoveButton.class, "bourse_remove"));
		this.buttonManager.register(new ActionLoader(this, AddButton.class, "bourse_add"));

		this.buttonManager.register(new PluginLoader(this, SetToMaxButton.class, "bourse_set_to_max"));
		this.buttonManager.register(new PluginLoader(this, SetToOneButton.class, "bourse_set_to_one"));
		this.buttonManager.register(new PluginLoader(this, BuyConfirmButton.class, "bourse_buy_confirm"));
		this.buttonManager.register(new PluginLoader(this, SellConfirmButton.class, "bourse_sell_confirm"));

		this.buttonManager.register(new ShowLoader(this));

		this.inventoryManager.deleteInventories(this);

		Config.categories.forEach(category -> {

			File file = new File(this.getDataFolder(), "inventories/" + category + ".yml");
			if (!file.exists()) {
				this.saveResource("inventories/" + category + ".yml", false);
			}

			try {
				this.inventoryManager.loadInventory(this, file);
			} catch (InventoryException e) {
				e.printStackTrace();
			}

		});

		File fileBuy = new File(this.getDataFolder(), "inventories/buy.yml");
		if (!fileBuy.exists()) {
			this.saveResource("inventories/buy.yml", false);
		}

		File fillSell = new File(this.getDataFolder(), "inventories/sell.yml");
		if (!fillSell.exists()) {
			this.saveResource("inventories/sell.yml", false);
		}

		try {
			this.inventoryManager.loadInventory(this, fillSell);
			this.inventoryManager.loadInventory(this, fileBuy);
		} catch (InventoryException e) {
			e.printStackTrace();
		}

	}

	public BourseManager getManager() {
		return manager;
	}

	public BourseButton getButton(Player player) {
		return this.buttons.get(player);
	}

	public void setButton(Player player, BourseButton button) {
		this.buttons.put(player, button);
	}

	public SellManager getSellManager() {
		return sellManager;
	}

	public List<String> getMaterials() {
		return Config.categories.stream().map(category -> this.inventoryManager.getInventory(this, category))
				.filter(Optional::isPresent).map(Optional::get).map(i -> i.getButtons(BourseButton.class))
				.flatMap(List::stream).map(e -> e.getItemStack().build(null).getType().name().toLowerCase())
				.collect(Collectors.toList());
	}

}
