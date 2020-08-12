package com.db.dataplatform.techtest.common.api.model;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonSerialize(as = DataHeader.class)
@JsonDeserialize(as = DataHeader.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataHeader {

    @NotNull
    @NotBlank
    public  String name;

    @NotNull
    private BlockTypeEnum blockType;

}
