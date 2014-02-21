package com.teambitcoin.coinwallet.models;

import com.teambitcoin.coinwallet.api.Account;

public class UserWrapper {
    public UserWrapper() {
    }
    
    public Account getUserAccount(User user) {
        return user.generateAccount();
    }
}
