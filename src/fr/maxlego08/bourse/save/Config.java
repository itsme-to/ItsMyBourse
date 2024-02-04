package fr.maxlego08.bourse.save;

import java.util.ArrayList;
import java.util.List;

import fr.maxlego08.bourse.zcore.utils.storage.Persist;
import fr.maxlego08.bourse.zcore.utils.storage.Saveable;

public class Config implements Saveable {

	public static boolean enableDebug = true;
	public static boolean enableDebugTime = false;

	public static String upColor = "§b↗ +%percent%%";
	public static String downColor = "§4↙ -%percent%%";
	
	public static String upNumberColor = "§b↗ +%value%";
	public static String downNumberColor = "§4↙ -%value%";

	public static String priceFormat = "#.##";

	public static List<String> categories = new ArrayList<String>();
	
	public static List<String> defaultLore = new ArrayList<String>();
	public static long milliSeconds = 1000 * 86400;
	public static long taskChangePricemilliSeconds = 1000;
	public static long minItemChange = 2000;

	static {
		
		categories.add("agriculture");
		categories.add("minerais");

		defaultLore.add("§8§m-+------------------------------+-");
		defaultLore.add("§f§l» §7Cette item est soucis à la bourse.");
		defaultLore.add("§f§l» §7Prix par défaut§8: §a%defaultPrice%$");
		defaultLore.add("§f§l» §7Prix de vente à l'unité§8: §2%price%$ %fluctuation%");
		defaultLore.add("§f§l» §7Prix d'achat§8: §2%buyPrice%$");
		defaultLore.add("§f%diff%");
		defaultLore.add("");
		defaultLore.add("§f§l» §7Items vendu en 24h§8: §d%count%");
		defaultLore.add("");
		defaultLore.add("§7§l» §fClique gauche pour vendre");
		defaultLore.add("§7§l» §fClique molette pour vendre §bx%itemAmount%§f items");
		defaultLore.add("§7§l» §fClique droit pour acheter");
		defaultLore.add("§8§m-+------------------------------+-");

	}

	/**
	 * static Singleton instance.
	 */
	private static volatile Config instance;

	/**
	 * Private constructor for singleton.
	 */
	private Config() {
	}

	/**
	 * Return a singleton instance of Config.
	 */
	public static Config getInstance() {
		// Double lock for thread safety.
		if (instance == null) {
			synchronized (Config.class) {
				if (instance == null) {
					instance = new Config();
				}
			}
		}
		return instance;
	}

	public void save(Persist persist) {
		persist.save(getInstance());
	}

	public void load(Persist persist) {
		persist.loadOrSaveDefault(getInstance(), Config.class);
	}

}
