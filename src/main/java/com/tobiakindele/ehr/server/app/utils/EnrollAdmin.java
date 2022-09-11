package com.tobiakindele.ehr.server.app.utils;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Properties;

public class EnrollAdmin {

    private final static Logger logger = LoggerFactory.getLogger(EnrollAdmin.class);

    public static void init() throws Exception {

        try {
            Properties props = new Properties();
            props.load(ClassLoader.getSystemResourceAsStream("application.properties"));

            // Create a CA client for interacting with the CA.
            HFCAClient caClient = HFCAClient.createNewInstance(props.getProperty("fabric.ca.host"), props);
            CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
            caClient.setCryptoSuite(cryptoSuite);

            // Create a wallet for managing identities
            Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

            // Check to see if we've already enrolled the admin user.
            if (wallet.get("admin") != null) {
                logger.info("An identity for the admin user \"admin\" already exists in the wallet");
                return;
            }

            // Enroll the admin user, and import the new identity into the wallet.
            final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
            enrollmentRequestTLS.addHost("localhost");
            enrollmentRequestTLS.setProfile("tls");
            Enrollment enrollment = caClient.enroll("admin", "adminpw", enrollmentRequestTLS);
            Identity user = Identities.newX509Identity("Org1MSP", enrollment);
            wallet.put("admin", user);
            logger.info("Successfully enrolled user \"admin\" and imported it into the wallet");
        } catch (Exception e) {
            logger.error("[-] Error occurred: ", e);
            throw e;
        }
    }
}
