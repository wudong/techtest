package com.db.dataplatform.techtest.common.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonSerialize(as = DataEnvelope.class)
@JsonDeserialize(as = DataEnvelope.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataEnvelope {

    @NotNull
    @Valid
    private DataHeader dataHeader;

    @NotNull
    private DataBody dataBody;

    @Setter
    @NotNull
    private String checksum;
}
