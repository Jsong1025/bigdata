package com.bigdata.dht.chord;

import java.math.BigInteger;

public class HexUtils {

    public static String add(String num1, String num2) {
        return new BigInteger(num1, 16).add(new BigInteger(num2, 16)).toString(16);
    }

    public static String add(String num1, Integer num2) {
        return new BigInteger(num1, 16).add(new BigInteger(num2.toString())).toString(16);
    }

    public static boolean compareTo(String num1, String num2) {
        return new BigInteger(num1, 16).compareTo(new BigInteger(num2, 16)) > 0;
    }

    public static boolean compareTo(String num1, Integer num2) {
        return new BigInteger(num1, 16).compareTo(new BigInteger(num2.toString())) > 0;
    }
}
