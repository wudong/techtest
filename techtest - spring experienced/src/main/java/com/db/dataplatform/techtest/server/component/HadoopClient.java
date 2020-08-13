package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.common.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface HadoopClient {

    @Async
    @Retryable(value = {RestClientException.class, HttpClientErrorException.class}, maxAttempts = 10, backoff = @Backoff(3000))
    void pushToHadoop(String body);
}
