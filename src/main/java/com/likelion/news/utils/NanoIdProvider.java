package com.likelion.news.utils;


import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class NanoIdProvider {
    private final char[] ALPHABET;
    private final int ID_LENGTH;
    private final SecureRandom RANDOM = new SecureRandom();

    public NanoIdProvider(Environment env) {
        ALPHABET = env.getProperty("nanoId.alphabet").toCharArray();
        ID_LENGTH = Integer.parseInt(env.getProperty("nanoId.length"));
    }

    public String createNanoId() {
        char[] buffer = new char[ID_LENGTH];
        for (int idx = 0; idx < ID_LENGTH; ++idx) {
            buffer[idx] = ALPHABET[RANDOM.nextInt(ALPHABET.length)];
        }
        return new String(buffer);
    }
}
