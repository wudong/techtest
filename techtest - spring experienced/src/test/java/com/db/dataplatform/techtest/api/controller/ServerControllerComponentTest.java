package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.common.api.model.PatchResponse;
import com.db.dataplatform.techtest.common.api.model.PushResponse;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
import com.db.dataplatform.techtest.common.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.ir.Block;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class ServerControllerComponentTest {

	public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
	public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
	public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");

	@Mock
	private Server serverMock;

	private DataEnvelope testDataEnvelope;
	private ObjectMapper objectMapper;
	private MockMvc mockMvc;
	private ServerController serverController;

	@Before
	public void setUp() throws HadoopClientException, NoSuchAlgorithmException, IOException {
		serverController = new ServerController(serverMock);
		mockMvc = standaloneSetup(serverController).build();
		objectMapper = Jackson2ObjectMapperBuilder
				.json()
				.build();

		testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();

		when(serverMock.saveDataEnvelope(any(DataEnvelope.class))).thenReturn(true);
	}

	@Test
	public void testPushDataPostCallWorksAsExpected() throws Exception {

		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA)
				.content(testDataEnvelopeJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		String contentAsString = mvcResult.getResponse().getContentAsString();
		PushResponse pushResponse = objectMapper.readValue(contentAsString, PushResponse.class);
		assertThat(pushResponse.isSuccessful()).isTrue();
	}

	@Test
	public void testQueryDataWorksAsExpected() throws Exception {

		BlockTypeEnum type = BlockTypeEnum.BLOCKTYPEA;

		when(serverMock.findDataEnvelopByBlocktype(eq(type))).thenReturn(Arrays.asList(testDataEnvelope));
		MvcResult mvcResult = mockMvc.perform(get(URI_GETDATA.expand(type))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		String contentAsString = mvcResult.getResponse().getContentAsString();
		List list = objectMapper.readValue(contentAsString, List.class);
		assertThat(list).hasSize(1);
	}

	@Test
	public void testUpdatePatchCallWorksAsExpected() throws Exception {

		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		String name= "ABC-BDC-EDF";
		BlockTypeEnum type = BlockTypeEnum.BLOCKTYPEA;
		when(serverMock.updateBlocktypeByName(eq(name), eq(type))).thenReturn(true);
		MvcResult mvcResult = mockMvc.perform(patch(URI_PATCHDATA.expand(name, type))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		String contentAsString = mvcResult.getResponse().getContentAsString();
		PatchResponse response = objectMapper.readValue(contentAsString, PatchResponse.class);
		assertThat(response.isSuccessful()).isTrue();
	}

	// TODO the test instance didn''t have validation enabled, need to fix to make this test work.
	@Ignore
	public void testUpdatePatchInvalidNameShouldHaveClientError() throws Exception {

		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		String name= "ABCEDF";
		BlockTypeEnum type = BlockTypeEnum.BLOCKTYPEA;
		when(serverMock.updateBlocktypeByName(eq(name), eq(type))).thenReturn(true);
		MvcResult mvcResult = mockMvc.perform(patch(URI_PATCHDATA.expand(name, type))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is4xxClientError())
				.andReturn();
	}


}
