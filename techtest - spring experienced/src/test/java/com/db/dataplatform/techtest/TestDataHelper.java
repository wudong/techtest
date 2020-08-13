package com.db.dataplatform.techtest;

import com.db.dataplatform.techtest.common.api.model.DataBody;
import com.db.dataplatform.techtest.common.api.model.DataEnvelope;
import com.db.dataplatform.techtest.common.api.model.DataHeader;
import com.db.dataplatform.techtest.common.util.ChecksumCalc;
import com.db.dataplatform.techtest.common.util.impl.ChecksumCalcImpl;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class TestDataHelper {

    public static final String TEST_NAME = "Test";
    public static final String TEST_NAME_EMPTY = "";
    public static final String DUMMY_DATA = "AKCp5fU4WNWKBVvhXsbNhqk33tawri9iJUkA5o4A6YqpwvAoYjajVw8xdEw6r9796h1wEp29D";

    public static ChecksumCalc checksumCalc;
    static {
        try {
            checksumCalc = new ChecksumCalcImpl();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public static DataHeaderEntity createTestDataHeaderEntity(Instant expectedTimestamp) {
        DataHeaderEntity dataHeaderEntity = new DataHeaderEntity();
        dataHeaderEntity.setName(TEST_NAME);
        dataHeaderEntity.setBlocktype(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity.setCreatedTimestamp(expectedTimestamp);
        return dataHeaderEntity;
    }

    public static DataBodyEntity createTestDataBodyEntity(DataHeaderEntity dataHeaderEntity) {
        DataBodyEntity dataBodyEntity = new DataBodyEntity();
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
        dataBodyEntity.setDataBody(DUMMY_DATA);
        return dataBodyEntity;
    }

    public static DataEnvelope createTestDataEnvelopeApiObject() {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA);
        String checksum = checksumCalc.checksum(DUMMY_DATA);
        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody, checksum);
        return dataEnvelope;
    }

    public static DataEnvelope createTestDataEnvelopeApiObjectWithEmptyName() {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME_EMPTY, BlockTypeEnum.BLOCKTYPEA);

        String checksum = checksumCalc.checksum(DUMMY_DATA);
        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody, checksum);
        return dataEnvelope;
    }
}
