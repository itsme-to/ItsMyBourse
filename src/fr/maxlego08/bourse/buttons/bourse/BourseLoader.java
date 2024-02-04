package fr.maxlego08.bourse.buttons.bourse;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;

public class BourseLoader implements ButtonLoader {

	private final BoursePlugin plugin;

	/**
	 * @param plugin
	 */
	public BourseLoader(BoursePlugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public Class<? extends Button> getButton() {
		return BourseButton.class;
	}

	@Override
	public String getName() {
		return "bourse";
	}

	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}

	@Override
	public Button load(YamlConfiguration configuration, String path, DefaultButtonValue var3) {

		double initialPrice = configuration.getDouble(path + "initialPrice");
		double buyPrice = configuration.getDouble(path + "buyPrice");

		return new ZBourseButton(this.plugin, initialPrice, buyPrice);
	}

}
