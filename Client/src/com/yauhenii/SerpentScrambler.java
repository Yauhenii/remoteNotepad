package com.yauhenii;

import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import lombok.Getter;

public class SerpentScrambler {

    @Getter
    private byte[] iv;
    @Getter
    private SecretKey key;
    private KeyGenerator keyGenerator;
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public SerpentScrambler(SecretKey key, byte[] iv) throws GeneralSecurityException {
        keyGenerator = KeyGenerator.getInstance("Serpent", "BC");
        keyGenerator.init(256);

        this.key = key;
        this.iv = iv;

        encryptCipher = Cipher.getInstance("Serpent/CFB/NoPadding", "BC");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        decryptCipher = Cipher.getInstance("Serpent/CFB/NoPadding", "BC");
        decryptCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
    }

    public void changeKey(SecretKey key, byte[] iv) throws GeneralSecurityException{
        this.key = key;
        this.iv = iv;

        encryptCipher = Cipher.getInstance("Serpent/CFB/NoPadding", "BC");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        decryptCipher = Cipher.getInstance("Serpent/CFB/NoPadding", "BC");
        decryptCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
    }

    public byte[] encrypt(byte[] data)
        throws GeneralSecurityException {
        return encryptCipher.doFinal(data);
    }

    public byte[] decrypt(byte[] cipherText)
        throws GeneralSecurityException {
        return decryptCipher.doFinal(cipherText);
    }

}
