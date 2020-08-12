package com.db.dataplatform.techtest.common.util.impl;


import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import static org.assertj.core.api.Assertions.assertThat;


public class ChecksumCalcImplTest {

    @Test
    public void testChecksum() throws NoSuchAlgorithmException {
        ChecksumCalcImpl checksumCalc = new ChecksumCalcImpl();
        String c1 = checksumCalc.checksum("ABCDEFG");
        String c2 = checksumCalc.checksum("ABCDEFG2");
        String c3 = checksumCalc.checksum("ABCDEFG2");

        assertThat(c1).isNotEqualTo(c2);
        assertThat(c2).isEqualTo(c3);
    }

}