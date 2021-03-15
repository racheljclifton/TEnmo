package com.techelevator.tenmo;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;

import java.math.BigDecimal;
import java.util.Map;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private static final long TRANSFER_STATUS_APPROVED = 2;
    private static final long TRANSFER_STATUS_REJECTED = 3;
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private TransferService transferService;
    private long transferOtherUserId;
    private BigDecimal transferAmount;

    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new AccountService(API_BASE_URL), new TransferService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService, TransferService transferService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.accountService = accountService;
        this.transferService = transferService;
    }

    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                listUsers();
                checkAccount();
                getTransferAmount();
                sendBucks();
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                listUsers();
                checkAccount();
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }

    private void viewCurrentBalance() {
        try {
            BigDecimal balance = accountService.getBalance();
            System.out.println("Your current account balance is: $" + balance);
        } catch (AccountServiceException ex) {
            System.out.println("There was an error accessing your balance");
        }

    }

    private void viewTransferHistory() {
        try {
            Transfer[] transferHistory = transferService.getTransferHistory();
            if (transferHistory.length == 0) {
                System.out.println("You have no transfer history to be found.");
            } else {
                System.out.println("-------------------------------------------");
                System.out.println("Transfers");
                System.out.println("ID          From/To                 Amount");
                System.out.println("-------------------------------------------");
                for (Transfer transfer : transferHistory) {
                    long transferId = transfer.getTransferId();
                    int currentUserId = currentUser.getUser().getId();
                    if (currentUserId == transfer.getUserToId()) {
                        System.out.println(transferId + "        From: " + transfer.getUserFromName() +
                                "      $ " + transfer.getAmount());
                    } else if (currentUserId == transfer.getUserFromId()) {
                        System.out.println(transferId + "        To:   " + transfer.getUserToName() +
                                "      $ " + transfer.getAmount());
                    }
                }
                viewTransferDetails(transferHistory);
            }
        } catch (AccountServiceException ex) {
            System.out.println("There was an error accessing your transfer history. Don't know what to tell ya.");
        }
    }

    private void viewTransferDetails(Transfer[] transferHistory) {
        long transferId = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel)");
        if (transferId == 0) {
            mainMenu();
        }
        Transfer selectedTransfer = getTransferDetailsFromId(transferId, transferHistory);

    }

    private void viewPendingRequests() {
        try {
            Transfer[] pendingTransfers = transferService.getPendingTransfers();
            if (pendingTransfers.length == 0) {
                System.out.println("You have no pending transfer requests.");
            } else {
                System.out.println("-------------------------------------------");
                System.out.println("Pending Transfers");
                System.out.println("ID          To                     Amount");
                System.out.println("-------------------------------------------");
                for (Transfer transfer : pendingTransfers) {
                    long transferId = transfer.getTransferId();
                    int currentUserId = currentUser.getUser().getId();
                    System.out.println(transferId + "        To:   " + transfer.getUserToName() +
                            "      $ " + transfer.getAmount());
                }
                approveOrRejectRequest(pendingTransfers);
            }
        } catch (AccountServiceException ex) {
            System.out.println("There was an error accessing your transfer history. Don't know what to tell ya.");
        }
    }

    private void approveOrRejectRequest(Transfer[] pendingTransfers) throws AccountServiceException {
        long transferId = console.getUserInputInteger("Please enter transfer ID to approve/reject (0 to cancel)");
        if(transferId == 0){
            mainMenu();
        }
        Transfer selectedTransfer = getTransferDetailsFromId(transferId, pendingTransfers);
        transferAmount = selectedTransfer.getAmount();
        if (selectedTransfer == null) {mainMenu();}
        System.out.println("1: Approve\n" +
                "2: Reject\n" +
                "0: Don't approve or reject\n" +
                "---------");
        int response = console.getUserInputInteger("Please choose an option");
        if(response == 1) {
            validateTransferAmount();
            selectedTransfer.setTransferStatusId(TRANSFER_STATUS_APPROVED);
            System.out.println("Pending transfer has been approved!\n" + selectedTransfer);
        } else if (response == 2){
            selectedTransfer.setTransferStatusId(TRANSFER_STATUS_REJECTED);
            System.out.println("Pending transfer has been rejected, sucka.\n" + selectedTransfer);
        } else if (response == 0) {
            System.out.println("Pending transfer has not been changed.");
            mainMenu();
        } else {
            System.out.println("Invalid response. Pending Transfer has not been changed.");
            mainMenu();
        }
        transferService.updateTransferStatus(selectedTransfer);
    }

    private Transfer getTransferDetailsFromId(long transferId, Transfer[] transfers){
        Transfer selectedTransfer = null;
        for (Transfer transfer : transfers) {
            if (transferId == transfer.getTransferId()) {
                selectedTransfer = transfer;
                break;
            }
        }
        if (selectedTransfer != null){
            System.out.println(selectedTransfer);
        } else {
            System.out.println("Invalid Transfer ID.");
            mainMenu();
        }
        return selectedTransfer;
    }

    private void listUsers() {
        try {
            String userId = currentUser.getUser().getId().toString();
            Map<String, String> results = accountService.getUsers();
            System.out.println("-------------------------------------------");
            System.out.println("Users");
            System.out.println("ID          Name");
            System.out.println("-------------------------------------------");
            for (Map.Entry<String, String> result : results.entrySet()) {
                String resultID = result.getKey();
                if (!userId.equals(resultID)) {
                    System.out.println(resultID + "        " + result.getValue());
                }
            }
            System.out.println("---------");
        } catch (AccountServiceException ex) {
            System.out.println("There was an error accessing list of users");
            mainMenu();
        }

    }

    private void checkAccount() {
        try {
            transferOtherUserId = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");
            if (transferOtherUserId == 0) {
                mainMenu();
            }
            int numberOfAccounts = accountService.getNumberOfAccounts(transferOtherUserId);
            if (numberOfAccounts == 0) {
                System.out.println("Sorry, no accounts found under that User ID.");
                mainMenu();
            }
        } catch (AccountServiceException ex) {
            System.out.println("There was an error accessing user accounts.");
            mainMenu();
        }
    }

    private void getTransferAmount(){
            transferAmount = console.getUserInputBigDecimal("Enter amount");
        if (transferAmount.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Invalid entry. Transfer amount must be greater than zero.");
            mainMenu();
        }
            validateTransferAmount();
    }

    private void validateTransferAmount() {
        try {
            BigDecimal balance = accountService.getBalance();
            if (transferAmount.compareTo(balance) > 0) {
                System.out.println("Insufficient funds in account. Transfer has not been processed.");
                mainMenu();
            }
        } catch (AccountServiceException ex) {
            System.out.println("There was an error accessing your account.");
            mainMenu();
        }
    }


    private void sendBucks() {
        try {
            Transfer transfer = transferService.transferSend(transferOtherUserId, transferAmount);
            System.out.println("Transfer successful!" +
                    "\nTransfer Status: " + transfer.getTransferStatusDescription() +
                    "\nTransfer Amount: " + transfer.getAmount() +
                    "\nTo: Account " + transfer.getAccountTo());

        } catch (AccountServiceException ex) {
            System.out.println("There was an error processing your transfer.");
            mainMenu();
        }

    }

    private void requestBucks() {
        try {
            transferAmount = console.getUserInputBigDecimal("Enter amount");
            if (transferAmount.compareTo(BigDecimal.ZERO) < 0) {
                System.out.println("Invalid entry. Transfer amount must be greater than zero.");
                mainMenu();
            }
            Transfer transfer = transferService.transferRequest(transferOtherUserId, transferAmount);
            System.out.println("Request has been sent!" +
                    "\nTransfer Status: " + transfer.getTransferStatusDescription() +
                    "\nTransfer Amount: " + transfer.getAmount() +
                    "\nFrom: Account " + transfer.getAccountFrom());
        } catch (AccountServiceException ex) {
            System.out.println("There was an error processing your transfer request.");
            mainMenu();
        }
    }


    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
                AccountService.AUTH_TOKEN = currentUser.getToken();
            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
}
