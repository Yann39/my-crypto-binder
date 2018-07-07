/*
 * Copyright (c) 2018 by Yann39.
 *
 * This file is part of MyCryptoBinder.
 *
 * MyCryptoBinder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCryptoBinder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCryptoBinder. If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycryptobinder.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class UtilsHelper {


    public UtilsHelper() {
    }

    /**
     * Get the current locale
     *
     * @return The current locale
     */
    public static Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }

    /**
     * Get myCryptoBinder properties
     *
     * @return The set of properties from the myCryptoBinder property file
     */
    public static Properties getProperties(Context context) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = context.getAssets().open("myCryptoBinder.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * AES Encrypt the specified string using the specified key and initial vector
     *
     * @param key        The key used for encryption
     * @param initVector The initial vector to use
     * @param value      The value to encrypt
     * @return The encrypted value
     */
    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * AES Decrypt the specified string using the specified key and initial vector
     *
     * @param key        The key used for encryption
     * @param initVector The initial vector to use
     * @param encrypted  The encrypted value to decrypt
     * @return The decrypted value
     */
    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Convert a Vector graphic to a Bitmap image
     *
     * @param context    The context
     * @param drawableId The vector drawable id
     * @return a Bitmap object representing the new Bitmap image generated from the vector drawable
     */
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawable = (DrawableCompat.wrap(drawable)).mutate();
            }

            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        }
        return null;
    }

    /**
     * Convert a byte array to an hexadecimal String
     *
     * @param bytes The byte array to convert
     * @return The converted byte array as String
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Escape a string to be written in a CSV file
     * Actually enclose value with " and double the ones in the string (" become "")
     *
     * @param value The value to escape
     * @return The escaped value
     */
    public static String escapeForCsv(String value) {
        if (value != null && value.length() > 0) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return "";
    }

    /**
     * Unescape a string got from a CSV file
     * Actually remove enclosing " and replace the double ones in the string ("" become ")
     *
     * @param value The value to unescape
     * @return The unescaped value
     */
    public static String unescapeFromCsv(String value) {
        if (value != null && value.length() > 0) {
            return value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return "";
    }

    /**
     * Format a number to short notation
     * 1200 -> 1.2k, -15894 -> -15.89k, 4526800 -> 4.53m, etc.
     *
     * @param number The number to format
     * @return The formatted number as string
     */
    public static String formatNumber(double number) {
        String[] denominations = {"", "k", "m", "b", "t"};
        int denominationIndex = 0;

        // if number is greater than 1000, divide it by 1000 and increment the index for the denomination
        while (number > 1000.0 || number < -1000) {
            denominationIndex++;
            number = number / 1000.0;
        }

        // round it to 2 digits
        BigDecimal bigDecimal = new BigDecimal(number);
        bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        // concat number and denomination to get the final value
        return bigDecimal + denominations[denominationIndex];
    }

}
