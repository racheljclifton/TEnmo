package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/users")
@PreAuthorize("isAuthenticated()")
@RestController
public class UserController {

    private UserDAO userDAO;
    private AccountDAO accountDAO;

    public UserController(UserDAO userDAO, AccountDAO accountDAO){
        this.userDAO = userDAO;
        this.accountDAO = accountDAO;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public Map<Long, String> getUsers() {
        List<User> users = userDAO.findAll();
        Map<Long, String> userMap = new HashMap<>();
        for (User thisUser : users) {
            userMap.put(thisUser.getId(), thisUser.getUsername());
        }
        return userMap;
    }



    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public int getNumberOfAccounts(@PathVariable int id){
        int numberOfAccounts = accountDAO.numberOfAccounts(id);
        return numberOfAccounts;
    }


}
