package mie.ether_example;

import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.Bool;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import edu.toronto.dbservice.types.ClientRequest;
import edu.toronto.dbservice.types.EtherAccount;
import java.sql.Connection;
import org.web3j.crypto.Credentials;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import edu.toronto.dbservice.config.MIE354DBHelper;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import java.util.HashMap;
import org.web3j.abi.datatypes.Utf8String;

public class isRegistered implements JavaDelegate {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(DelegateExecution execution) {
        // Connect to blockchain server
        Web3j web3 = Web3j.build(new HttpService());

        // load the list of accounts
        HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) execution.getVariable("accounts");

        // load registry contract based on the process variable contractAddress
        String contractAddress = (String) execution.getVariable("contractAddress");
        Registry myRegistry = Registry.load(contractAddress, web3, accounts.get(0).getCredentials(), EtherUtils.GAS_PRICE, EtherUtils.GAS_LIMIT_CONTRACT_TX);

        // Retrieve the product ID from the process variables
        ClientRequest requestId = (ClientRequest) execution.getVariable("currentClientRequest");
        String productId=requestId.getItem();

        try {
            // Check if the product is registered
            boolean isRegistered = myRegistry.isRegistered(new Utf8String(productId)).get().getValue();
            // Set the result in the execution context
            execution.setVariable("isRegistered", isRegistered);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

