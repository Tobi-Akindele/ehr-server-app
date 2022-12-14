package com.tobiakindele.ehr.server.app.service;

import com.tobiakindele.ehr.server.app.EhrServerAppApplication;
import com.tobiakindele.ehr.server.app.enums.TransactionType;
import com.tobiakindele.ehr.server.app.utils.GatewayConnection;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeoutException;

@Component
public final class ContractService {

    private final static Logger logger = LoggerFactory.getLogger(EhrServerAppApplication.class);

    public Object invokeTransaction(String contractName, TransactionType transactionType)
            throws ContractException, InterruptedException, TimeoutException {

        Object response;

        try (Gateway gateway = GatewayConnection.connect()) {

            // get the network and contract
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("ehr");

            switch (transactionType) {
                case SUBMIT:
                    response = contract.submitTransaction(contractName);
                    break;
                case EVALUATE:
                    response = contract.evaluateTransaction(contractName);
                    break;
                default:
                    //do nothing
                    response = null;
            }
        } catch (Exception e) {
            logger.error("[-] Error occurred: ", e);
            throw e;
        }

        return response;
    }

    public Object invokeTransaction(String contractName, TransactionType transactionType, String payload)
            throws ContractException, InterruptedException, TimeoutException {

        Object response;

        try (Gateway gateway = GatewayConnection.connect()) {

            // get the network and contract
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("ehr");

            switch (transactionType) {
                case SUBMIT:
                    response = contract.submitTransaction(contractName, payload);
                    break;
                case EVALUATE:
                    response = contract.evaluateTransaction(contractName,payload);
                    break;
                default:
                    //do nothing
                    response = null;
            }
        } catch (Exception e) {
            logger.error("[-] Error occurred: ", e);
            throw e;
        }

        return response;
    }

    public Object invokeTransaction(String contractName, String[] pageInfo)
            throws ContractException {

        Object response;

        try (Gateway gateway = GatewayConnection.connect()) {

            // get the network and contract
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("ehr");

            response = contract.evaluateTransaction(contractName, pageInfo);

        } catch (Exception e) {
            logger.error("[-] Error occurred: ", e);
            throw e;
        }

        return response;
    }
}
