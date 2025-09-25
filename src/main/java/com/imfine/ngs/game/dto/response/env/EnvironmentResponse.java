package com.imfine.ngs.game.dto.response.env;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentResponse {
    private Long id;
    private String name;
    private String type;
}
