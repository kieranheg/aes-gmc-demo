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
public class LorryDto {
    @JsonField
    private String manufacturer;
    @JsonField
    private String model;
    @JsonField
    private String yr;
}
