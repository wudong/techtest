package com.db.dataplatform.techtest.common.util.impl;

import com.db.dataplatform.techtest.common.util.ChecksumCalc;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class ChecksumCalcImpl implements ChecksumCalc {

    private final MessageDigest mdDigester;
    private final Base64.Encoder encoder;


    public ChecksumCalcImpl() throws NoSuchAlgorithmException {
        this.mdDigester = MessageDigest.getInstance("MD5");
        this.encoder = Base64.getEncoder();
    }

    @Override
    public String checksum(String content) {
        mdDigester.update(content.getBytes());
        byte[] digest = mdDigester.digest();
        byte[] encode = this.encoder.encode(digest);
        return new String(encode, StandardCharsets.ISO_8859_1);
    }
}
