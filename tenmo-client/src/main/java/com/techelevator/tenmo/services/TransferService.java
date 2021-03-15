package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.NewTransfer;
import com.techelevator.tenmo.models.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {

   // public static String AUTH_TOKEN = "";
    private final String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final long TRANSFER_STATUS_APPROVED = 2;
    private static final long TRANSFER_STATUS_REJECTED = 3;

    public TransferService(String url) {
        BASE_URL = url;
    }

    public Transfer transferSend(long transferToId, BigDecimal transferAmount) throws AccountServiceException {
        try {
            NewTransfer newTransfer = new NewTransfer(transferToId, transferAmount);
            Transfer transfer = restTemplate.exchange(BASE_URL + "/transfers/send", HttpMethod.POST, makeNewTransferEntity(newTransfer), Transfer.class).getBody();
            if (transfer == null){
                throw new AccountServiceException();
            }
            return transfer;
        } catch (RestClientResponseException ex) {
            throw new AccountServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new AccountServiceException(ex.getMessage());
        }
    }

    public Transfer[] getTransferHistory() throws AccountServiceException{
        Transfer[] transferHistory;
        try {
            transferHistory = restTemplate.exchange(BASE_URL + "/transfers/history", HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
            return transferHistory;
        } catch (RestClientResponseException ex) {
            throw new AccountServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new AccountServiceException(ex.getMessage());
        }
    }

    public Transfer transferRequest(long otherUserId, BigDecimal transferAmount) throws AccountServiceException {
        try {
            NewTransfer newTransfer = new NewTransfer(otherUserId, transferAmount);
            Transfer transfer = restTemplate.exchange(BASE_URL + "/transfers/request", HttpMethod.POST, makeNewTransferEntity(newTransfer), Transfer.class).getBody();
            if (transfer == null){
                throw new AccountServiceException();
            }
            return transfer;
        } catch (RestClientResponseException ex) {
            throw new AccountServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new AccountServiceException(ex.getMessage());
        }
    }

    public Transfer[] getPendingTransfers() throws AccountServiceException{
        Transfer[] pendingTransfers;
        try{
            pendingTransfers = restTemplate.exchange(BASE_URL + "/transfers/pending", HttpMethod.GET, makeAuthEntity(),Transfer[].class).getBody();
            return pendingTransfers;
        } catch (RestClientResponseException ex) {
            throw new AccountServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new AccountServiceException(ex.getMessage());
        }
    }

    public void updateTransferStatus(Transfer transfer) throws AccountServiceException{
        try {
            if(transfer.getTransferStatusId() == TRANSFER_STATUS_APPROVED){
                restTemplate.put(BASE_URL + "/transfers/"+ transfer.getTransferId() + "/approve", makeTransferEntity(transfer));
            } else if (transfer.getTransferStatusId() == TRANSFER_STATUS_REJECTED){
                restTemplate.put(BASE_URL + "/transfers/"+ transfer.getTransferId() + "/reject", makeTransferEntity(transfer));
            }
        } catch (RestClientResponseException ex) {
            throw new AccountServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new AccountServiceException(ex.getMessage());
        }
    }

    private HttpEntity makeNewTransferEntity(NewTransfer newTransfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AccountService.AUTH_TOKEN);
        HttpEntity entity = new HttpEntity(newTransfer,headers);
        return entity;
    }

    private HttpEntity makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AccountService.AUTH_TOKEN);
        HttpEntity entity = new HttpEntity(transfer,headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AccountService.AUTH_TOKEN);
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }

}
