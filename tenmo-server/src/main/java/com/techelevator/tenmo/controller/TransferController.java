package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.NewTransferDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/transfers")
public class TransferController {

    private TransferDAO transferDAO;

    public TransferController(TransferDAO transferDAO) {
        this.transferDAO = transferDAO;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public Transfer sendTransfer(@Valid @RequestBody NewTransferDTO newTransferDTO, Principal principal) {

        Transfer transfer = transferDAO.createSendTransfer(newTransferDTO, principal);
        return transfer;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public Transfer requestTransfer(@Valid @RequestBody NewTransferDTO newTransferDTO, Principal principal) {

        Transfer transfer = transferDAO.createRequestTransfer(newTransferDTO, principal);
        return transfer;
    }

    @RequestMapping(path = "/history", method = RequestMethod.GET)
    public List<Transfer> getTransferHistory(Principal principal){
        List<Transfer> transferHistory = new ArrayList<>();
        transferHistory = transferDAO.findTransferHistory(principal);
        return transferHistory;
    }

    @RequestMapping(path = "/pending", method = RequestMethod.GET)
    public List<Transfer> getPendingTransfers(Principal principal){
        List<Transfer> pendingTransfers = new ArrayList<>();
        pendingTransfers = transferDAO.findPendingRequests(principal);
        return pendingTransfers;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "{id}/approve", method = RequestMethod.PUT)
    public void updateTransferStatusToApprove(@Valid @RequestBody Transfer transfer){
        transferDAO.updateAccountBalances(transfer);
        transferDAO.updateTransferStatus(transfer);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "{id}/reject", method = RequestMethod.PUT)
    public void updateTransferStatusToReject(@Valid @RequestBody Transfer transfer){
        transferDAO.updateTransferStatus(transfer);
    }
}
