package com.imfine.ngs.game.dto.request;


import com.imfine.ngs.game.enums.EnvType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnvRequest {
    private EnvType envType;
}
