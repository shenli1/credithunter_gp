package com.id.cash.common;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static javax.crypto.Cipher.ENCRYPT_MODE;

public class SecurityUtil {
    public static String aesEncrypt(String text) {
        try {
            Key secretKeySpec = new SecretKeySpec("Sw432109#zxcvbn!".getBytes(), "AES");
            Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
            instance.init(ENCRYPT_MODE, secretKeySpec, new IvParameterSpec("123NullPointerEx".getBytes()));
            return Base64.encodeToString(instance.doFinal(text.getBytes("UTF-8")), Base64.NO_WRAP);
        } catch (Throwable e) {
            LogUtil.e(e);
            return null;
        }
    }
}
