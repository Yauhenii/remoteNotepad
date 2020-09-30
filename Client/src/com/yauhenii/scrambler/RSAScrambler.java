package com.yauhenii.scrambler;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class RSAScrambler {

    private KeyPair keyPair;
    private Cipher decryptCipher;

    public RSAScrambler() throws GeneralSecurityException {
        keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
    }

    public byte[] decrypt(byte[] data)
        throws GeneralSecurityException {
        return decryptCipher.doFinal(data);
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }
}
