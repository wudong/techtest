package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.common.api.model.DataEnvelope;
import com.db.dataplatform.techtest.common.util.ChecksumCalc;
import com.db.dataplatform.techtest.server.component.HadoopClient;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;

    @Mock
    private ChecksumCalc checksumCalc;

    @Mock
    private HadoopClient client;

    private ModelMapper modelMapper;

    private DataBodyEntity expectedDataBodyEntity;
    private DataEnvelope testDataEnvelope;

    private Server server;

    @Before
    public void setup() {
        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        modelMapper = serverMapperConfiguration.createModelMapperBean();

        testDataEnvelope = createTestDataEnvelopeApiObject();
        expectedDataBodyEntity = modelMapper.map(testDataEnvelope.getDataBody(), DataBodyEntity.class);
        expectedDataBodyEntity.setDataHeaderEntity(modelMapper.map(testDataEnvelope.getDataHeader(), DataHeaderEntity.class));

        when(checksumCalc.checksum(eq(testDataEnvelope.getDataBody().getDataBody()))).thenReturn(testDataEnvelope.getChecksum());
        server = new ServerImpl(dataBodyServiceImplMock, modelMapper, checksumCalc, client);
    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException {
        boolean success = server.saveDataEnvelope(testDataEnvelope);

        assertThat(success).isTrue();
        verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
        verify(client, times(1)).pushToHadoop(eq(testDataEnvelope.getDataBody().getDataBody()));
    }

    @Test
    public void sholdUpdateDataEnvelopAsExpected() throws NoSuchAlgorithmException, IOException{
        String name = "TSLA-GDPUSD-10Y";
        boolean success = server.updateBlocktypeByName(name, BlockTypeEnum.BLOCKTYPEB);
        assertThat(success).isTrue();
        verify(dataBodyServiceImplMock, times(1)).updateBlocktypeByName(eq(name), eq(BlockTypeEnum.BLOCKTYPEB));
    }

    @Test
    public void shouldQueryDataAsExpected() throws NoSuchAlgorithmException, IOException{
        when(dataBodyServiceImplMock.getDataByBlockType(eq(BlockTypeEnum.BLOCKTYPEB))).thenReturn(Arrays.asList(expectedDataBodyEntity));
        List<DataEnvelope> dataEnvelopByBlocktype = server.findDataEnvelopByBlocktype(BlockTypeEnum.BLOCKTYPEB);
        assertThat(dataEnvelopByBlocktype).hasSize(1);
        assertThat(dataEnvelopByBlocktype.get(0).getDataBody().getDataBody()).isEqualTo(TestDataHelper.DUMMY_DATA);
        verify(dataBodyServiceImplMock, times(1)).getDataByBlockType(eq(BlockTypeEnum.BLOCKTYPEB));
    }

}
