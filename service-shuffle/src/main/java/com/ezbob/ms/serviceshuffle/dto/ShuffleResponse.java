package com.ezbob.ms.serviceshuffle.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ShuffleResponse {
    private List<Integer> shuffledList;

}
