package com.tobiakindele.ehr.server.app.utils;

import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GatewayConnection {

    private final static Logger logger = LoggerFactory.getLogger(GatewayConnection.class);

    /**
     * Helper function for getting connected to the gateway
     *
     * @return
     */
    public static Gateway connect() {

        try {
            // Load a file system based wallet for managing identities;
            Path walletPath = Paths.get("wallet");
            Wallet wallet = Wallets.newFileSystemWallet(walletPath);
            // Load a CCP
            Path networkConfigPath = Paths.get(
                    "..", "..", "test-network", "organizations", "peerOrganizations",
                    "org1.example.com", "connection-org1.yaml");

            Gateway.Builder builder = Gateway.createBuilder();
            builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);
            return builder.connect();
        } catch (IOException e) {
            logger.error("[-] Error occurred: ", e);
            return null;
        }
    }
}
