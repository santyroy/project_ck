package dev.roy.coinkeeper.security.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeyGeneratorUtility {

    private KeyGeneratorUtility() {}

    protected static KeyPair generateRSAKeys() {
        KeyPair keyPair;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            keyPair = generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
        return keyPair;
    }
}
