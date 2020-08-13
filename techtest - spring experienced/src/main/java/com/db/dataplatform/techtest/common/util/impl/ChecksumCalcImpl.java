package com.db.dataplatform.techtest.common.util.impl;

import com.db.dataplatform.techtest.common.util.ChecksumCalc;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class ChecksumCalcImpl implements ChecksumCalc {

    private final MessageDigest mdDigester;


    public ChecksumCalcImpl() throws NoSuchAlgorithmException {
        this.mdDigester = MessageDigest.getInstance("MD5");

    }

    @Override
    public String checksum(String content) {
        mdDigester.update(content.getBytes());
        byte[] digest = mdDigester.digest();
        return DatatypeConverter.printHexBinary(digest);
    }
}
