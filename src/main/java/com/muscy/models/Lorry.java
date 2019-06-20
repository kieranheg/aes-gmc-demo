package com.muscy.models;

import com.muscy.annotations.JsonField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Lorry {
    @JsonField("manufacturer")
    private String make;
    @JsonField
    private String model;
    @JsonField("yr")
    private String year;
    private String wheels;
}
