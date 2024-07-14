package com.ezbob.ms.serviceshuffle.model;

import com.ezbob.ms.serviceshuffle.dto.ShuffleRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "service-shuffle")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ShuffleModel {
    @Id
    private String shuffleId;
    private ShuffleRequest request;
    private List<Integer> response;
    private boolean isLogged;

}
