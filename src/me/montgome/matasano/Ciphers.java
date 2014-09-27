package me.montgome.matasano;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Ciphers {
    public static byte[] encryptEcb(byte[] plaintext, byte[] key) {
        try {
            plaintext = Paddings.addPkcs7(plaintext, key.length);
            Cipher aes = Cipher.getInstance("AES/ECB/NoPadding");
            aes.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            return aes.doFinal(plaintext);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static byte[] decryptEcb(byte[] ciphertext, byte[] key) {
            try {
                Cipher aes = Cipher.getInstance("AES/ECB/NoPadding");
                aes.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
                return Paddings.removePkcs7(aes.doFinal(ciphertext));
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
    }
    
    public static byte[] encryptCbc(byte[] plaintext, byte[] key, byte[] iv) {
        plaintext = Paddings.addPkcs7(plaintext, key.length);
        byte[][] blocks = Bytes.split(plaintext, key.length);
        
        
        try {
            Cipher aes = Cipher.getInstance("AES/ECB/NoPadding");
            aes.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));

            byte[] previous = iv;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            for (byte[] block : blocks) {
                byte[] encrypted = aes.doFinal(Bytes.xor(block, previous));
                output.write(encrypted);
                previous = encrypted;
            }
            
            return output.toByteArray();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static byte[] decryptCbc(byte[] ciphertext, byte[] key, byte[] iv) {
        byte[][] blocks = Bytes.split(ciphertext, key.length);
        
        try {
            Cipher aes = Cipher.getInstance("AES/ECB/NoPadding");
            aes.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
            
            byte[] previous = iv;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            for (byte[] block : blocks) {
                byte[] decrypted = Bytes.xor(previous, aes.doFinal(block));
                output.write(decrypted);
                previous = block;
            }
            
            return Paddings.removePkcs7(output.toByteArray());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
