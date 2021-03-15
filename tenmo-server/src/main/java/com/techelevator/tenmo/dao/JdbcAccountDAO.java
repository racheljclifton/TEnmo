package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDAO implements AccountDAO{

    private JdbcTemplate jdbcTemplate;
    public JdbcAccountDAO (JdbcTemplate jdbcTemplate){this.jdbcTemplate = jdbcTemplate;}



    @Override
    public BigDecimal getBalance(long userId) {
        BigDecimal balance = BigDecimal.ZERO;
        String sql = "SELECT SUM(balance) AS balance FROM accounts WHERE user_id = ?;";
        balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
        return balance;
    }

    @Override
    public Integer numberOfAccounts(long userId){
        Integer result = 0;
        String sql = "SELECT COUNT(account_id) FROM accounts WHERE user_id = ?;";
        result = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return result;
    }

    //    @Override
//    public List<Account> getListOfAccounts(long userId) {
//        List<Account> accounts = new ArrayList<>();
//        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE user_id = ?;";
//        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
//        while(results.next()){
//            Account account = mapRowToAccount(results);
//            accounts.add(account);
//        }
//        return accounts;
//    }

//    private Account mapRowToAccount(SqlRowSet rs){
//        Account account = new Account();
//        account.setAccountId(rs.getLong("account_id"));
//        account.setUserId(rs.getLong("user_id"));
//        account.setBalance(rs.getDouble("balance"));
//        return account;
//    }

}
