package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.common.api.model.DataBody;
import com.db.dataplatform.techtest.common.api.model.DataEnvelope;
import com.db.dataplatform.techtest.common.api.model.DataHeader;
import com.db.dataplatform.techtest.common.util.ChecksumCalc;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private final DataBodyService dataBodyServiceImpl;
    private final ModelMapper modelMapper;
    private final ChecksumCalc checksumCalc;
    private final RestTemplate restTemplate;

    private final String HADOOP_URL = "http://localhost:8090/hadoopserver/pushbigdata";

    /**
     * @param envelope
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public boolean saveDataEnvelope(DataEnvelope envelope) {

        String checksum = checksumCalc.checksum(envelope.getDataBody().getDataBody());
        String checksumInRequest = envelope.getChecksum();
        boolean checkPassed = Objects.equals(checksum, checksumInRequest);
        if (checkPassed) {
            // Save to persistence.
            persist(envelope);
            log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());
        }

        //also push to hadoop
        pushToHadoop(envelope.getDataBody().getDataBody());

        return checkPassed;
    }

    @Override
    public List<DataEnvelope> findDataEnvelopByBlocktype(BlockTypeEnum type) throws IOException {
        List<DataBodyEntity> dataByBlockType = dataBodyServiceImpl.getDataByBlockType(type);
        return dataByBlockType.stream()
            .map(this::mapDataBodyEntityToDataEnvelop)
            .collect(Collectors.toList());
    }

    @Override
    public boolean updateBlocktypeByName(String name, BlockTypeEnum blocktype) {
        dataBodyServiceImpl.updateBlocktypeByName(name, blocktype);
        return true;
    }

    private void persist(DataEnvelope envelope) {
        log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);

        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
        dataBodyEntity.setChecksum(envelope.getChecksum());

        saveData(dataBodyEntity);
    }

    private void saveData(DataBodyEntity dataBodyEntity) {
        dataBodyServiceImpl.saveDataBody(dataBodyEntity);
    }

    private DataEnvelope mapDataBodyEntityToDataEnvelop(DataBodyEntity entity) {
        DataBody dataBody = new DataBody(entity.getDataBody());
        DataHeader dataHeader = new DataHeader(entity.getDataHeaderEntity().getName(), entity.getDataHeaderEntity().getBlocktype());
        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody, entity.getChecksum());
        return dataEnvelope;
    }

    @Override
    @Async
    public void pushToHadoop(String body) {
        log.info("Pushing data {} to Hadoop");
        try {

            ResponseEntity<String> response = restTemplate.postForEntity(HADOOP_URL, body, String.class);
            if (response.getStatusCode().isError()) {
                log.warn("Error response received: {}", response);
            }
        }catch (RestClientException e) {
            log.error("Error while pushing data to hadoop, will retry", e);
            throw e;
        }
    }

}
