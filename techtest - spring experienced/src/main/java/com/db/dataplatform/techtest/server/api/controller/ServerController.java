package com.db.dataplatform.techtest.server.api.controller;

import com.db.dataplatform.techtest.common.api.model.DataEnvelope;
import com.db.dataplatform.techtest.common.api.model.PatchResponse;
import com.db.dataplatform.techtest.common.api.model.PushResponse;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final Server server;

    @PostMapping(value = "/pushdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PushResponse> pushData(@Valid @RequestBody DataEnvelope dataEnvelope) throws IOException, NoSuchAlgorithmException {

        log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());
        boolean checksumPass = server.saveDataEnvelope(dataEnvelope);

        log.info("Data envelope persisted. Attribute name: {}", dataEnvelope.getDataHeader().getName());
        return ResponseEntity.ok(new PushResponse(checksumPass));
    }

    @GetMapping(value = "/data/{blockType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DataEnvelope>> getDataByBlockType(@PathVariable("blockType") BlockTypeEnum blockType) throws IOException {
        List<DataEnvelope> dataEnvelopByBlocktype = server.findDataEnvelopByBlocktype(blockType);
        return ResponseEntity.ok(dataEnvelopByBlocktype);
    }

    @PatchMapping(value = "/update/{name}/{blockType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatchResponse> getDataByBlockType(@PathVariable("name") String name, @PathVariable("blockType") BlockTypeEnum blockType) throws IOException {
        boolean success = server.updateBlocktypeByName(name, blockType);
        return ResponseEntity.ok(new PatchResponse(success));
    }

}
