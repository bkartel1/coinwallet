package com.teambitcoin.coinwallet.api;

import java.math.BigInteger;

public class Statistics {
	private BigInteger TotalBTC;
	private float Difficulty;
	private float HashRate;
	private int NumberOfBlocks;
	
	public BigInteger getTotalBTC() {
		return TotalBTC;
	}
	public void setTotalBTC(BigInteger totalBTC) {
		TotalBTC = totalBTC;
	}
	public float getDifficulty() {
		return Difficulty;
	}
	public void setDifficulty(float difficulty) {
		Difficulty = difficulty;
	}
	public float getHashRate() {
		return HashRate;
	}
	public void setHashRate(float hashRate) {
		HashRate = hashRate;
	}
	public int getNumberOfBlocks() {
		return NumberOfBlocks;
	}
	public void setNumberOfBlocks(int numberOfBlocks) {
		NumberOfBlocks = numberOfBlocks;
	}
}
