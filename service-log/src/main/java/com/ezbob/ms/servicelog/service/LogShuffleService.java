package com.ezbob.ms.servicelog.service;

import com.ezbob.ms.servicelog.dto.ShuffleResponseNotLoggedRequests;
import com.ezbob.ms.servicelog.model.ShuffleLoggedModel;
import com.ezbob.ms.servicelog.repository.ShuffleLoggedRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogShuffleService {

    private final ShuffleLoggedRepository shuffleLoggedRepository;
    private final RestTemplate restTemplate;

    @Value("${service.shuffle.url}")
    private String shuffleServiceUrl;

    @Scheduled(fixedRateString = "${service.log.check.interval}")
    public void checkAndMoveUnloggedRecords() {
        String url = shuffleServiceUrl + "/logged-false";
        List<ShuffleResponseNotLoggedRequests> responseList = Stream.of(
                        restTemplate.getForObject(url, ShuffleResponseNotLoggedRequests[].class))
                .collect(Collectors.toList());

        if (!responseList.isEmpty()) {
            List<ShuffleLoggedModel> loggedModels = responseList.stream()
                    .map(record -> new ShuffleLoggedModel(
                            record.getShuffleId(),
                            record.getRequest(),
                            record.getResponse()))
                    .peek(record -> log.info("Logging record: {}", record))
                    .collect(Collectors.toList());

            shuffleLoggedRepository.saveAll(loggedModels);
            log.info("Moved {} unlogged records to service-log.", loggedModels.size());

            // Update isLogged flag to true in service-shuffle
            List<String> shuffleIds = loggedModels.stream()
                    .map(ShuffleLoggedModel::getShuffleId)
                    .collect(Collectors.toList());

            String updateUrl = shuffleServiceUrl + "/update-log-status?isLogged=true";
            restTemplate.put(updateUrl, shuffleIds);
            log.info("Updated log status for {} records in service-shuffle.", shuffleIds.size());
        }
    }

    @Scheduled(fixedRateString = "${service.log.compare.interval}")
    public void compareAndDeleteRecords() {
        String url = shuffleServiceUrl + "/logged-all";
        List<ShuffleResponseNotLoggedRequests> allRecordsList = Stream.of(restTemplate.getForObject(url, ShuffleResponseNotLoggedRequests[].class))
                .filter(ShuffleResponseNotLoggedRequests::isLogged)
                .collect(Collectors.toList());

        if (!allRecordsList.isEmpty()) {
            List<String> loggedIds = shuffleLoggedRepository.findAll().stream()
                    .map(ShuffleLoggedModel::getShuffleId)
                    .collect(Collectors.toList());

            List<String> idsToDelete = allRecordsList.stream()
                    .filter(record -> loggedIds.contains(record.getShuffleId()))
                    .map(ShuffleResponseNotLoggedRequests::getShuffleId)
                    .collect(Collectors.toList());

            if (!idsToDelete.isEmpty()) {
                String deleteUrl = shuffleServiceUrl + "/delete-shuffles";
                restTemplate.postForObject(deleteUrl, idsToDelete, Void.class);
                log.info("Deleted {} records from service-shuffle.", idsToDelete.size());
            }
        }
    }
}