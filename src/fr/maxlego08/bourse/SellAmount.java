package fr.maxlego08.bourse;

import fr.maxlego08.bourse.save.Config;

public class SellAmount {

	private final long amount;
	private final long createdAt;

	/**
	 * @param amount
	 * @param createdAt
	 */
	public SellAmount(long amount) {
		super();
		this.amount = amount;
		this.createdAt = System.currentTimeMillis() + Config.milliSeconds;
	}

	/**
	 * @return the amount
	 */
	public long getAmount() {
		return amount;
	}

	/**
	 * @return the createdAt
	 */
	public long getCreatedAt() {
		return createdAt;
	}

	public boolean hasExpired() {
		return System.currentTimeMillis() > this.createdAt;
	}

}
