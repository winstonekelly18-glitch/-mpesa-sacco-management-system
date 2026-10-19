package com.example.mobile_basedsaccomanagement;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PinUtils {

    // Converts a 4-digit PIN into a secure hashed string
    public static String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Checks if the typed PIN matches the hashed PIN saved in Firestore
    public static boolean verifyPin(String inputPin, String storedHash) {
        return hashPin(inputPin).equals(storedHash);
    }
}