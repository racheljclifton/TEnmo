package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.UserDAO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RequestMapping("/accounts")
@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

    private AccountDAO accountDAO;
    private UserDAO userDAO;

    public AccountController(AccountDAO accountDAO, UserDAO userDAO) {
        this.accountDAO = accountDAO;
        this.userDAO = userDAO;
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getAccountBalance(Principal principal) throws UsernameNotFoundException {
        long userId = getCurrentUserId(principal);
        return accountDAO.getBalance(userId);
    }




    private long getCurrentUserId (Principal principal){
        long userId = userDAO.findIdByUsername(principal.getName());
        return userId;
    }

}
