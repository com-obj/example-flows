package com.obj.examplesflows.genericDataToMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseAgreement {
    
    private String description;
    
    @JsonProperty("expiry_date")
    private Instant expiryDate;
    
}
