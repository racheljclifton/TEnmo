package com.techelevator.tenmo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "The account was not found.")
public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(){
        super("The account was not found.");
    }

}
