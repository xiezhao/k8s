package org.example.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class IdGen {
    private static final int maxLength = 13;

    public IdGen() {
    }

    public static String getDateTime() {
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(new Date());
    }

    public static long getRandom(long n) {
        long min = 1L;
        long max = 9L;

        for(int i = 1; (long)i < n; ++i) {
            min *= 10L;
            max *= 10L;
        }

        long rangeLong = (long)((new Random()).nextDouble() * (double)(max - min)) + min;
        return rangeLong;
    }

    public static String genId(String preCode) {
        if (preCode == null) {
            preCode = "NM";
        } else if (preCode.length() > 2) {
            preCode = preCode.substring(0, 2);
        } else if (preCode.length() < 2) {
            StringBuilder sb = new StringBuilder(preCode);

            while(sb.length() < 2) {
                sb.append('N');
            }

            preCode = sb.toString();
        }

        return preCode + getDateTime() + getRandom(13L);
    }
}