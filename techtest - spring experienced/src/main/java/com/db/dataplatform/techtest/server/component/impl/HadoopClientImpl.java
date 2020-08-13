package com.db.dataplatform.techtest.server.component.impl;


import com.db.dataplatform.techtest.server.component.HadoopClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@AllArgsConstructor
@Component
public class HadoopClientImpl implements HadoopClient {

    private final RestTemplate restTemplate;

    private final String HADOOP_URL = "http://localhost:8090/hadoopserver/pushbigdata";

    @Async
    @Override
    public void pushToHadoop(String body) {
        log.info("Pushing data {} to Hadoop", body);
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
