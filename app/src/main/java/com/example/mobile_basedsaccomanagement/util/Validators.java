package com.example.mobile_basedsaccomanagement.util;

import android.text.TextUtils;
import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Validators {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Supports Kenyan formats: 07XXXXXXXX, 01XXXXXXXX, or +254XXXXXXXXX
    public static boolean isValidKenyanPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return false;
        String regex = "^(\\+254|0)[17][0-9]{8}$";
        return phone.matches(regex);
    }

    // 8+ chars, upper, lower, digit
    public static boolean isStrongPassword(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasUpper && hasLower && hasDigit;
    }

    public static boolean isAtLeast18YearsOld(String dobStr) {
        if (TextUtils.isEmpty(dobStr)) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            Calendar dob = Calendar.getInstance();
            dob.setTime(sdf.parse(dobStr));

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age >= 18;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isPositiveNumber(String amountStr) {
        if (TextUtils.isEmpty(amountStr)) return false;
        try {
            double val = Double.parseDouble(amountStr);
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
