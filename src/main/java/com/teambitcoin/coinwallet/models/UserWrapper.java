package main.java.com.teambitcoin.coinwallet.models;

import main.java.com.teambitcoin.coinwallet.api.Account;

public class UserWrapper {
	public UserWrapper() {}
	
	public Account getUserAccount(User user) {
		return user.generateAccount();
	}
}
