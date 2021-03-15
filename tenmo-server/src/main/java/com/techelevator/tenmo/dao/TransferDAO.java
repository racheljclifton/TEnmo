package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.NewTransferDTO;

import java.security.Principal;
import java.util.List;

public interface TransferDAO {



  //  Transfer sendTransfer(Transfer transfer) throws AccountNotFoundException;

    Transfer createSendTransfer(NewTransferDTO newTransferDTO, Principal principal);

    void updateAccountBalances(Transfer transfer);

    List<Transfer> findTransferHistory(Principal principal);

    Transfer createRequestTransfer(NewTransferDTO newTransferDTO, Principal principal);

    List<Transfer> findPendingRequests(Principal principal);

    void updateTransferStatus(Transfer transfer);
}
