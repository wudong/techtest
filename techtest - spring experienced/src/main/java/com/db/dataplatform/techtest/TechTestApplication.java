package com.db.dataplatform.techtest;

import com.db.dataplatform.techtest.common.api.model.DataBody;
import com.db.dataplatform.techtest.common.api.model.DataEnvelope;
import com.db.dataplatform.techtest.common.api.model.DataHeader;
import com.db.dataplatform.techtest.client.component.Client;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.db.dataplatform.techtest.Constant.DUMMY_DATA;

@Slf4j
@SpringBootApplication
@EnableRetry
@EnableAsync
public class TechTestApplication {

	public static final String HEADER_NAME = "TSLA-USDGBP-10Y";
	public static final String HEADER_NAME1 = "TSLA-USDGBP-20Y";
	public static final String MD5_CHECKSUM = "cecfd3953783df706878aaec2c22aa70";

	@Autowired
	private Client client;

	public static void main(String[] args) {

		SpringApplication.run(TechTestApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initiatePushDataFlow() throws JsonProcessingException, UnsupportedEncodingException {
		pushData(HEADER_NAME);
		pushData(HEADER_NAME1);

		queryData();
		updateData();
		queryDataAgain();
	}

	private void updateData() throws UnsupportedEncodingException {
		boolean success = client.updateData(HEADER_NAME, BlockTypeEnum.BLOCKTYPEB.name());
		if (!success) {
			throw new AssertionError("We should have successfully updated the blocktype");
		}
		log.info("Updated data successfully");
	}

	private void queryData() {

		List<DataEnvelope> data = client.getData(BlockTypeEnum.BLOCKTYPEA.name());
		if (data.size()!=2) {
			throw new AssertionError("We should have only 1 from the query");
		}

		log.info("queried data successfully");

	}

	private void queryDataAgain() {

		List<DataEnvelope> data = client.getData(BlockTypeEnum.BLOCKTYPEA.name());
		if (data.size()!=1) {
			throw new AssertionError("We should have only 1 from the query for blocktypea");
		}

		List<DataEnvelope> data2 = client.getData(BlockTypeEnum.BLOCKTYPEB.name());
		if (data2.size()!=1) {
			throw new AssertionError("We should have only 1 from the query for blocktypeb");
		}

		log.info("queried data again successfully");

	}

	private void pushData(String name) throws JsonProcessingException {

		DataBody dataBody = new DataBody(DUMMY_DATA);

		DataHeader dataHeader = new DataHeader(name, BlockTypeEnum.BLOCKTYPEA);

		DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);

		client.pushData(dataEnvelope);
	}

}
