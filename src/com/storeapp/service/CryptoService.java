package com.storeapp.service;
import com.storeapp.util.Constants;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class CryptoService {
	
	
	private static SecretKeySpec getSecretKey() {
	    return new SecretKeySpec(
	            Constants.SECRET_KEY.getBytes(StandardCharsets.UTF_8),
	            "AES");
	}
	
	
	
	public static String encrypt(String data) {
	    try {

	        Cipher cipher = Cipher.getInstance("AES");

	        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());

	        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

	        return Base64.getEncoder().encodeToString(encrypted);

	    } catch (Exception e) {
	        throw new RuntimeException("Encryption failed.", e);
	    }
	}
	
}
