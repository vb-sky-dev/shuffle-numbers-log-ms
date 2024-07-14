package com.ezbob.ms.servicelog;

import com.ezbob.ms.servicelog.dto.ShuffleResponseNotLoggedRequests;
import com.ezbob.ms.servicelog.model.ShuffleLoggedModel;
import com.ezbob.ms.servicelog.repository.ShuffleLoggedRepository;
import com.ezbob.ms.serviceshuffle.dto.ShuffleRequest;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class LogShuffleServiceIntegrationTest {

    @Value("${service.shuffle.url}")
    private String shuffleServiceUrl;

    @Autowired
    private ShuffleLoggedRepository shuffleLoggedRepository;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = restTemplateBuilder.build();
        shuffleLoggedRepository.deleteAll();
    }

    @Test
    void checkAndMoveUnloggedRecords() {
        URI uri = URI.create(shuffleServiceUrl + "/logged-false");

        ResponseEntity<ShuffleResponseNotLoggedRequests[]> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, null, ShuffleResponseNotLoggedRequests[].class);

        List<ShuffleResponseNotLoggedRequests> fetchedRecords = List.of(responseEntity.getBody());

        List<ShuffleLoggedModel> loggedModels = fetchedRecords.stream()
                .map(record -> new ShuffleLoggedModel(
                        record.getShuffleId(),
                        record.getRequest(),
                        record.getResponse()))
                .collect(Collectors.toList());

        shuffleLoggedRepository.saveAll(loggedModels);

        List<String> shuffleIds = loggedModels.stream()
                .map(ShuffleLoggedModel::getShuffleId)
                .collect(Collectors.toList());
        URI updateUri = URI.create(shuffleServiceUrl + "/update-log-status?isLogged=true");
        restTemplate.put(updateUri, shuffleIds);

        List<ShuffleLoggedModel> storedModels = shuffleLoggedRepository.findAll();
        assertThat(storedModels).hasSize(loggedModels.size());
        assertThat(storedModels).containsAll(loggedModels);
    }

    @Test
    void compareAndDeleteRecords() {
        URI uri = URI.create(shuffleServiceUrl + "/logged-all");
        ResponseEntity<ShuffleResponseNotLoggedRequests[]> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, null, ShuffleResponseNotLoggedRequests[].class);

        List<ShuffleResponseNotLoggedRequests> allRecords = List.of(responseEntity.getBody());

        List<ShuffleLoggedModel> loggedModels = allRecords.stream()
                .filter(ShuffleResponseNotLoggedRequests::isLogged)
                .map(record -> new ShuffleLoggedModel(
                        record.getShuffleId(),
                        record.getRequest(),
                        record.getResponse()))
                .collect(Collectors.toList());
        shuffleLoggedRepository.saveAll(loggedModels);

        List<String> idsToDelete = loggedModels.stream()
                .map(ShuffleLoggedModel::getShuffleId)
                .collect(Collectors.toList());
        URI deleteUri = URI.create(shuffleServiceUrl + "/delete-shuffles");
        restTemplate.postForObject(deleteUri, idsToDelete, Void.class);
    }
}