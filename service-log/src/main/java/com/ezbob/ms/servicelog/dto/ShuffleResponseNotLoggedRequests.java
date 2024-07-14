package com.ezbob.ms.servicelog.dto;

import com.ezbob.ms.serviceshuffle.dto.ShuffleRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ShuffleResponseNotLoggedRequests {
    private String shuffleId;
    private ShuffleRequest request;
    private List<Integer> response;
    private boolean isLogged;

}
