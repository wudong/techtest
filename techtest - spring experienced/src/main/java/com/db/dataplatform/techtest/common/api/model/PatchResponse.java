package com.db.dataplatform.techtest.common.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonSerialize(as = PatchResponse.class)
@JsonDeserialize(as = PatchResponse.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PatchResponse {

    private boolean successful;
}
