package com.techelevator.tenmo.services;import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;


public class TransferService {

    private final String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();
    public AuthenticatedUser user;
    public ConsoleService consoleService = new ConsoleService();
    public UserService userService = new UserService("http://localhost:8080/user");
    public AccountService accountService = new AccountService("http://localhost:8080/account");


    public TransferService(String url) {
        BASE_URL = url;
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }

    public Transfer[] findAllTransfersForCurrentUser(AuthenticatedUser user) {
        Transfer[] transferHistory = null;
        try {
            setUser(user);
            transferHistory = restTemplate.exchange(BASE_URL + "/user/" + user.getUser().getId(), HttpMethod.GET, makeAuthEntity(user), Transfer[].class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println("Transfers cannot be displayed.");
        }
        return transferHistory;
    }

    public Transfer[] seeAllTransfers() {
        Transfer[] transfers = null;
        try {
            transfers = restTemplate.exchange(BASE_URL + "/all", HttpMethod.GET, makeAuthEntity(user), Transfer[].class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println("Transfers cannot be displayed.");
        }
        return transfers;
    }

    public Transfer findTransferByTransferId() {
        Transfer transfer = null;
        int transferId = consoleService.promptForInt("Please enter the transfer ID for the transfer you'd like to view: ");
        try {
            transfer = restTemplate.exchange(BASE_URL + "/" + transferId, HttpMethod.GET, makeAuthEntity(user), Transfer.class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println("Your transfer could not be found.");
        }
        return transfer;
    }

    public String createTransfer(Transfer transfer) {
        String transferSuccessReport = "";
        try {
            transferSuccessReport = restTemplate.exchange(BASE_URL + "/send", HttpMethod.POST, makeTransferEntity(transfer, user), String.class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println("Transfer failed. Try again.");
        } return transferSuccessReport;
    }

    public void printTransfers(Transfer[] transfers){
        if (transfers.length == 0) {
            System.out.println("There are no transfers to display.");
        }
        for (Transfer transfer : transfers) {
            System.out.println(transfer.transferDetailsPrintOut());
        }
    }

    public Transfer[] getPendingRequests(AuthenticatedUser user) {
        Transfer[] pendingTransfersList = null;
        try {
            pendingTransfersList = restTemplate.exchange(BASE_URL + "/user/" + user.getUser().getId() + "/pending", HttpMethod.GET, makeAuthEntity(user), Transfer[].class).getBody();
        } catch (RestClientResponseException e) {
            e.printStackTrace();
        }
        return pendingTransfersList;
    }

    public String updateTransfer(Transfer transfer, int statusId, AuthenticatedUser user) {
        String transferSuccessReport = "";
        try {
            transferSuccessReport = restTemplate.exchange(BASE_URL + "/update/" + statusId, HttpMethod.PUT, makeTransferEntity(transfer, user), String.class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            transferSuccessReport = "Your transfer could not be updated.";
        }
        return transferSuccessReport;
    }

    public void updatePendingTransferStatus(Transfer[] pendingTransfersList, int userSelection, AuthenticatedUser user) {
        accountService.setUser(user);
        Account userAccount = accountService.findAccountByUserId(user.getUser().getId());
        boolean matchFound = false;
        for (Transfer transfer : pendingTransfersList) {
            if (userSelection == 0) {
                System.out.println("You have opted not to update any pending transfers.");
                break;
            }
            if (transfer.getAccountTo() != userAccount.getAccountId()) {
                if (transfer.getTransferId() == userSelection) {
                    matchFound = true;
                    System.out.println("-------------------------------------------------------------------------------------------\r\n");
                    System.out.println("You have selected the following transfer: \r\n");
                    System.out.println(transfer.transferDetailsPrintOut());
                    int userChoice = consoleService.promptForInt(
                            "1: Approve\r\n" +
                                    "2: Reject\r\n" +
                                    "0: Don't approve or reject\r\n" +
                                    "-------------------------------------------------------------------------------------------\r\n" +
                                    "Please enter a number to choose the corresponding option: ");
                    if (userChoice == 1 || userChoice == 2) {
                        int newTransferStatusId = userChoice + 1;
                        System.out.println(updateTransfer(transfer, newTransferStatusId, user));
                    } else if (userChoice == 0) {
                        System.out.println("You have elected not to update this transfer.");
                        break;
                    } else {
                        System.out.println("You have entered an invalid option.");
                    }
                }
            } else if (transfer.getTransferId() == userSelection && transfer.getAccountTo() == userAccount.getAccountId()){
                System.out.println("You can't approve or reject your own requested transfer, but nice try! :)");
                matchFound = true;
                break;
            }
        } if (!matchFound && userSelection != 0) {
            System.out.println("The transfer ID you entered was invalid.");
        }
    }

    public void requestBucks(AuthenticatedUser user) {
        accountService.setUser(user);
        Transfer transfer = new Transfer();
        try {
            int userSelection = consoleService.promptForInt("-------------------------------------------------------------------------------------------\r\n" +
                    "Enter the user ID of the user you are requesting from (or enter 0 to cancel): ");
            Account requestAccount = accountService.findAccountByUserId(userSelection);
            transfer.setAccountFrom(requestAccount.getAccountId());
            transfer.setAccountTo((accountService.findAccountByUserId(user.getUser().getId())).getAccountId());
            transfer.setUserTo(user.getUser().getId());
            transfer.setUserFrom(userSelection);
            if (transfer.getAccountTo() != 0) {
                try {
                    transfer.setAmount(consoleService.promptForBigDecimal("Enter amount you'd like to request: "));
                } catch (NumberFormatException e) {
                    System.out.println("There was an error processing the amount");
                }
                String transferSuccess = restTemplate.exchange(BASE_URL + "/request", HttpMethod.POST, makeTransferEntity(transfer, user), String.class).getBody();
                System.out.println(transferSuccess);
            }
        } catch (Exception e) {
            System.out.println("Something went wrong with your input.");
        }
    }

    public void sendBucks(AuthenticatedUser user) {
        accountService.setUser(user);
        Transfer transfer = new Transfer();
        try {
            int userSelection = consoleService.promptForInt("-------------------------------------------------------------------------------------------\r\n" +
                    "Enter the user ID of the user you are sending to (or enter 0 to cancel): ");
            transfer.setUserFrom(user.getUser().getId());
            transfer.setUserTo(userSelection);
            Account recipientAccount = accountService.findAccountByUserId(userSelection);
            transfer.setAccountTo(recipientAccount.getAccountId());
            transfer.setAccountFrom((accountService.findAccountByUserId(user.getUser().getId())).getAccountId());
            if (transfer.getAccountTo() != 0) {
                try {
                    transfer.setAmount(consoleService.promptForBigDecimal("Enter amount you'd like to send: "));
                } catch (NumberFormatException e) {
                    System.out.println("There was an error processing the amount");
                }
                String transferSuccess = restTemplate.exchange(BASE_URL + "/send", HttpMethod.POST, makeTransferEntity(transfer, user), String.class).getBody();
                System.out.println(transferSuccess);
            }
        } catch (Exception e) {
            System.out.println("Something went wrong with your input.");
        }
    }

    //SECURITY METHODS BELOW - do not alter

    public HttpEntity<Void> makeAuthEntity(AuthenticatedUser user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<>(headers);
    }

    public HttpEntity<Transfer> makeTransferEntity(Transfer transfer, AuthenticatedUser user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<>(transfer, headers);
    }

}
