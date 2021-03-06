package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.common.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import com.db.dataplatform.techtest.common.api.model.PatchResponse;
import com.db.dataplatform.techtest.common.api.model.PushResponse;
import com.db.dataplatform.techtest.common.util.ChecksumCalc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {

    public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");

    private final RestTemplate restTemplate;
    private final ChecksumCalc checksumCalc;

    @Override
    public void pushData(DataEnvelope dataEnvelope) {
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            String checksum = checksumCalc.checksum(dataEnvelope.getDataBody().getDataBody());
            dataEnvelope.setChecksum(checksum);

            HttpEntity<DataEnvelope> request = new HttpEntity<>(dataEnvelope, httpHeaders);
            ResponseEntity<PushResponse> response = restTemplate.postForEntity(URI_PUSHDATA, request, PushResponse.class);
            if (response.getStatusCode().isError()) {
                log.warn("Error response received: {}", response);
            }
        }catch (RestClientException e) {
            log.error("Error while pushing data", e);
            throw e;
        }
    }

    @Override
    public List<DataEnvelope> getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);
        try{
            ResponseEntity<List> entity = restTemplate.getForEntity(URI_GETDATA.expand(blockType), List.class);
            if (entity.getStatusCode().isError()) {
                return Collections.emptyList();
            }else{
                return (List<DataEnvelope>) entity.getBody();
            }
        }catch (RestClientException e) {
            log.error("Error while getting data", e);
            throw e;
        }
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
        try{
            PatchResponse patchResponse = restTemplate.patchForObject(URI_PATCHDATA.expand(blockName, newBlockType),
                    null, PatchResponse.class);
            return patchResponse.isSuccessful();
        }catch (RestClientException e) {
            log.error("Error while getting data", e);
            throw e;
        }
    }


}
