package com.imfine.ngs.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailAvailabilityResponse {
    private String email;
    private boolean available;
}

