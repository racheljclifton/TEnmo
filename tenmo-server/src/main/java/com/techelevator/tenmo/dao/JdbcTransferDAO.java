package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.NewTransferDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDAO{

    private static final long TRANSFER_TYPE_REQUEST = 1;
    private static final long TRANSFER_TYPE_SEND = 2;
    private static final long TRANSFER_STATUS_PENDING = 1;
    private static final long TRANSFER_STATUS_APPROVED = 2;
    private static final long TRANSFER_STATUS_REJECTED = 3;


    private JdbcTemplate jdbcTemplate;
    private UserDAO userDAO;
    public JdbcTransferDAO (JdbcTemplate jdbcTemplate, UserDAO userDAO){
        this.jdbcTemplate = jdbcTemplate;
        this.userDAO = userDAO;
    }


    @Override
    public Transfer createSendTransfer(NewTransferDTO newTransferDTO, Principal principal) {
        Transfer transfer = mapNewSendTransferToTransfer(newTransferDTO, principal);
        updateAccountBalances(transfer);
        String sql =
                "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id; ";
        long transferId = jdbcTemplate.queryForObject(sql, Long.class,
                                                        transfer.getTransferTypeId(),
                                                        transfer.getTransferStatusId(),
                                                        transfer.getAccountFrom(),
                                                        transfer.getAccountTo(),
                                                        transfer.getAmount());
        transfer.setTransferId(transferId);
        return transfer;
    }

    @Override
    public void updateAccountBalances(Transfer transfer){
        String sql = "BEGIN TRANSACTION; " +
                "UPDATE accounts SET balance = balance - ? WHERE account_id = ?; " +
                "UPDATE accounts SET balance = balance + ? WHERE account_id = ?; " +
                "COMMIT; ";
        jdbcTemplate.update(sql,
                transfer.getAmount(),
                transfer.getAccountFrom(),
                transfer.getAmount(),
                transfer.getAccountTo());
    }

    @Override
    public Transfer createRequestTransfer(NewTransferDTO newTransferDTO, Principal principal) {
        Transfer transfer = mapNewRequestTransferToTransfer(newTransferDTO, principal);
        String sql =
                "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                        "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id; ";
        long transferId = jdbcTemplate.queryForObject(sql, Long.class,
                transfer.getTransferTypeId(),
                transfer.getTransferStatusId(),
                transfer.getAccountFrom(),
                transfer.getAccountTo(),
                transfer.getAmount());
        transfer.setTransferId(transferId);
        return transfer;
    }

    @Override
    public List<Transfer> findPendingRequests(Principal principal) {
        List<Transfer> pendingTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id," +
                "account_from, account_to, amount FROM transfers WHERE account_from IN" +
                "(SELECT account_id FROM accounts WHERE user_id = ?)" +
                "AND transfer_status_id = ?;";
        long userId = userDAO.findIdByUsername(principal.getName());
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, TRANSFER_STATUS_PENDING);
        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            addUserInfoToTransfer(transfer);
            pendingTransfers.add(transfer);
        }
        return pendingTransfers;
    }

    @Override
    public void updateTransferStatus(Transfer transfer) {
        Long transferId = transfer.getTransferId();
        Long transferStatus = transfer.getTransferStatusId();
        String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?";
        jdbcTemplate.update(sql, transferStatus, transferId);
    }


    @Override
    public List<Transfer> findTransferHistory(Principal principal) {
        List<Transfer> transferHistory = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id," +
                "account_from, account_to, amount FROM transfers WHERE account_from IN" +
                "(SELECT account_id FROM accounts WHERE user_id = ?) OR account_to IN" +
                "(SELECT account_id FROM accounts WHERE user_id = ?);";
        long userId = userDAO.findIdByUsername(principal.getName());
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while(results.next()){
            Transfer transfer = mapRowToTransfer(results);
            addUserInfoToTransfer(transfer);
            transferHistory.add(transfer);
        }
        return transferHistory;
    }



    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getLong("transfer_id"));
        transfer.setTransferTypeId(results.getLong("transfer_type_id"));
        transfer.setTransferStatusId(results.getLong("transfer_status_id"));
        transfer.setAccountFrom(results.getLong("account_from"));
        transfer.setAccountTo(results.getLong("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));
        return transfer;
    }

    private void addUserInfoToTransfer(Transfer transfer){
        //set user info: From
        String sql = "SELECT user_id FROM accounts WHERE account_id = ?";
        long userFromId = jdbcTemplate.queryForObject(sql, Long.class, transfer.getAccountFrom());
        transfer.setUserFromId(userFromId);
        String userFromName = userDAO.findUsernameById(userFromId);
        transfer.setUserFromName(userFromName);

        //set user info: To
        sql = "SELECT user_id FROM accounts WHERE account_id = ?";
        long userToId = jdbcTemplate.queryForObject(sql, Long.class, transfer.getAccountTo());
        transfer.setUserToId(userToId);
        String userToName = userDAO.findUsernameById(userToId);
        transfer.setUserToName(userToName);
    }

    private Transfer mapNewSendTransferToTransfer(NewTransferDTO newTransferDTO, Principal principal){
        Transfer transfer = new Transfer();
        transfer.setTransferTypeId(TRANSFER_TYPE_SEND);
        transfer.setTransferStatusId(TRANSFER_STATUS_APPROVED);
        transfer.setAmount(newTransferDTO.getTransferAmount());

        //set info about sender
        long userFromId = userDAO.findIdByUsername(principal.getName());
        long accountFromId = getAccountIdFromUserId(userFromId);
        transfer.setAccountFrom(accountFromId);

        //set info about receiver
        long accountToId = getAccountIdFromUserId(newTransferDTO.getOtherUserId());
        transfer.setAccountTo(accountToId);

        return transfer;
    }

    private Transfer mapNewRequestTransferToTransfer(NewTransferDTO newTransferDTO, Principal principal) {
        Transfer transfer = new Transfer();
        transfer.setTransferTypeId(TRANSFER_TYPE_REQUEST);
        transfer.setTransferStatusId(TRANSFER_STATUS_PENDING);
        transfer.setAmount(newTransferDTO.getTransferAmount());

        //set info about requester
        long userToId = userDAO.findIdByUsername(principal.getName());
        long accountToId = getAccountIdFromUserId(userToId);
        transfer.setAccountTo(accountToId);

        //set info about receiver/future sender
        long accountFromId = getAccountIdFromUserId(newTransferDTO.getOtherUserId());
        transfer.setAccountFrom(accountFromId);

        return transfer;
    }

    private long getAccountIdFromUserId(long userId){
        long accountId = 0;
        String sql = "SELECT account_id FROM accounts WHERE user_id = ? LIMIT 1;";
        accountId = jdbcTemplate.queryForObject(sql, Long.class, userId);
        return accountId;
    }



}
