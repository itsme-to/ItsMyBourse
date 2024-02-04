package fr.maxlego08.bourse.sell;

import org.bukkit.inventory.ItemStack;

public class SellItem {

	private final ItemStack itemStack;
	private final double boost;
	private final int durability;
	private final String name;

	/**
	 * @param itemStack
	 * @param boost
	 * @param durability
	 * @param name
	 */
	public SellItem(ItemStack itemStack, double boost, int durability, String name) {
		super();
		this.itemStack = itemStack;
		this.boost = boost;
		this.durability = durability;
		this.name = name;
	}

	/**
	 * @return the itemStack
	 */
	public ItemStack getItemStack() {
		return itemStack;
	}

	/**
	 * @return the boost
	 */
	public double getBoost() {
		return boost;
	}

	/**
	 * @return the durability
	 */
	public int getDurability() {
		return durability;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
