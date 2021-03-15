package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {

    BigDecimal getBalance (long userId);

    Integer numberOfAccounts(long userId);
}
