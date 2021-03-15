package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.NewTransfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AccountService {

    public static String AUTH_TOKEN = "";
    private final String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url) {
        BASE_URL = url;
    }

    public BigDecimal getBalance() throws AccountServiceException {
        BigDecimal balance = null;
        try {
            balance = restTemplate.exchange(BASE_URL + "/accounts/balance", HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
            return balance;
        } catch (RestClientResponseException ex) {
            throw new AccountServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new AccountServiceException(ex.getMessage());
        }
    }

    public Map<String, String> getUsers() throws AccountServiceException {
        Map<String, String> users = new HashMap<>();
        try {
            users = restTemplate.exchange(BASE_URL + "/users", HttpMethod.GET, makeAuthEntity(), HashMap.class).getBody();
            return users;
        } catch (RestClientResponseException ex) {
            throw new AccountServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new AccountServiceException(ex.getMessage());
        }
    }

    public int getNumberOfAccounts(long transferToId) throws AccountServiceException {
        int numberOfAccounts = 0;
        try {
            numberOfAccounts = restTemplate.exchange(BASE_URL + "/users/" + transferToId, HttpMethod.GET, makeAuthEntity(), Integer.class).getBody();
        } catch (RestClientResponseException ex) {
            throw new AccountServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new AccountServiceException(ex.getMessage());
        }
        return numberOfAccounts;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }



}
