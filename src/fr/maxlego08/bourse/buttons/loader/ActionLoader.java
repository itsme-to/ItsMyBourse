package fr.maxlego08.bourse.buttons.loader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.ZButton;

public class ActionLoader implements ButtonLoader {

	private final Plugin plugin;
	private final Class<? extends ZButton> classz;
	private final String name;

	/**
	 * @param plugin
	 */
	public ActionLoader(Plugin plugin, Class<? extends ZButton> classz, String name) {
		super();
		this.plugin = plugin;
		this.classz = classz;
		this.name = name;
	}

	@Override
	public Class<? extends Button> getButton() {
		return this.classz;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}

	@Override
	public Button load(YamlConfiguration configuration, String path, DefaultButtonValue var3) {
		try {
			int amount = configuration.getInt(path + "amount");
			Constructor<? extends ZButton> constructor = this.classz.getConstructor(BoursePlugin.class, int.class);
			return constructor.newInstance(this.plugin, amount);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

}
