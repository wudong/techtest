package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.common.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import jdk.nashorn.internal.ir.Block;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface Server {
    boolean saveDataEnvelope(DataEnvelope envelope) throws IOException, NoSuchAlgorithmException;

    List<DataEnvelope> findDataEnvelopByBlocktype(BlockTypeEnum type) throws IOException;

    boolean updateBlocktypeByName(String name, BlockTypeEnum blocktype) throws IOException;
}
