package com.db.dataplatform.techtest.common.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonSerialize(as = ErrorResponse.class)
@JsonDeserialize(as = ErrorResponse.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String error;
}
