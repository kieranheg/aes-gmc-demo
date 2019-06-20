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
public class Bus {
    @JsonField
    private String make;
    @JsonField
    private String model;
    @JsonField
    private String year;
    @JsonField
    private String seats;
    private String hp;
}
