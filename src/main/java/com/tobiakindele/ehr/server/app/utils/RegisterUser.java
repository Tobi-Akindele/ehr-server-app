package com.tobiakindele.ehr.server.app.utils;

import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

public class RegisterUser {

    private final static Logger logger = LoggerFactory.getLogger(RegisterUser.class);

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

            // Check to see if we've already enrolled the user.
            if (wallet.get("appUser") != null) {
                logger.info("An identity for the admin user \"appUser\" already exists in the wallet");
                return;
            }

            X509Identity adminIdentity = (X509Identity)wallet.get("admin");
            if (adminIdentity == null) {
                logger.info("\"admin\" needs to be enrolled and added to the wallet first");
                return;
            }
            User admin = new User() {

                @Override
                public String getName() {
                    return "admin";
                }

                @Override
                public Set<String> getRoles() {
                    return null;
                }

                @Override
                public String getAccount() {
                    return null;
                }

                @Override
                public String getAffiliation() {
                    return "org1.department1";
                }

                @Override
                public Enrollment getEnrollment() {
                    return new Enrollment() {
                        @Override
                        public PrivateKey getKey() {
                            return adminIdentity.getPrivateKey();
                        }

                        @Override
                        public String getCert() {
                            return Identities.toPemString(adminIdentity.getCertificate());
                        }
                    };
                }

                @Override
                public String getMspId() {
                    return "Org1MSP";
                }
            };

            // Register the user, enroll the user, and import the new identity into the wallet.
            RegistrationRequest registrationRequest = new RegistrationRequest("appUser");
            registrationRequest.setAffiliation("org1.department1");
            registrationRequest.setEnrollmentID("appUser");
            String enrollmentSecret = caClient.register(registrationRequest, admin);
            Enrollment enrollment = caClient.enroll("appUser", enrollmentSecret);
            Identity user = Identities.newX509Identity("Org1MSP", enrollment);
            wallet.put("appUser", user);
            logger.info("Successfully enrolled user \"appUser\" and imported it into the wallet");
        } catch (Exception  e) {
            logger.error("[-] Error occurred: ", e);
            throw e;
        }
    }
}
