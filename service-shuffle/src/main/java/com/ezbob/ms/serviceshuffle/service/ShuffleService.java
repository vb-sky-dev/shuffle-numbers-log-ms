package com.ezbob.ms.serviceshuffle.service;


import static com.ezbob.ms.serviceshuffle.utils.ShuffleHelper.shuffleArray;

import com.ezbob.ms.serviceshuffle.dto.ShuffleRequest;
import com.ezbob.ms.serviceshuffle.dto.ShuffleResponse;
import com.ezbob.ms.serviceshuffle.model.ShuffleModel;
import com.ezbob.ms.serviceshuffle.repository.ShuffleRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShuffleService {

    private final ShuffleRepository shuffleRepository;

    public ShuffleResponse createShuffleRequest(ShuffleRequest shuffleRequest) {
        List<Integer> shuffledList = shuffleArray(shuffleRequest.getShuffleTillThisNumber());

        ShuffleModel shuffleModel = ShuffleModel.builder()
                .shuffleId(UUID.randomUUID().toString())
                .request(shuffleRequest)
                .response(shuffledList)
                .isLogged(false)
                .build();

        shuffleRepository.save(shuffleModel);

        return ShuffleResponse.builder()
                .shuffledList(shuffledList)
                .build();
    }

    public List<ShuffleModel> getAllLoggedFalseShuffles() {
        return shuffleRepository.findByIsLoggedFalse();
    }

    public List<ShuffleModel> getAllShuffles() {
        return shuffleRepository.findAll();
    }

    public void updateShuffleLogStatus(List<String> shuffleIds, boolean isLogged) {
        List<ShuffleModel> shuffleModels = shuffleRepository.findAllById(shuffleIds);
        shuffleModels.forEach(shuffleModel -> shuffleModel.setLogged(isLogged));
        shuffleRepository.saveAll(shuffleModels);
    }

    public void deleteShuffles(List<String> shuffleIds) {
        shuffleRepository.deleteAllById(shuffleIds);
    }
}
