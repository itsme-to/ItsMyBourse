package fr.maxlego08.bourse.buttons.loader;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.buttons.ShowItemButton;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.loader.ButtonLoader;

public class ShowLoader implements ButtonLoader {

	private final Plugin plugin;

	/**
	 * @param plugin
	 */
	public ShowLoader(Plugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public Class<? extends Button> getButton() {
		return ShowItemButton.class;
	}

	@Override
	public String getName() {
		return "bourse_show_item";
	}

	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}

	@Override
	public Button load(YamlConfiguration configuration, String path) {
		List<String> lore = configuration.getStringList(path + "lore");
		return new ShowItemButton((BoursePlugin) this.plugin, lore);
	}

}
