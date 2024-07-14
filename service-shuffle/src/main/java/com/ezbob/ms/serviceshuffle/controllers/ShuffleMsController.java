package com.ezbob.ms.serviceshuffle.controllers;

import com.ezbob.ms.serviceshuffle.dto.ShuffleRequest;
import com.ezbob.ms.serviceshuffle.model.ShuffleModel;
import com.ezbob.ms.serviceshuffle.service.ShuffleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/service-shuffle")
public class ShuffleMsController {

    private final ShuffleService shuffleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createShuffleRequest(@RequestBody ShuffleRequest shuffleRequest) {
        shuffleService.createShuffleRequest(shuffleRequest);
    }

    @GetMapping("/logged-false")
    public List<ShuffleModel> getAllLoggedFalseShuffles() {
        return shuffleService.getAllLoggedFalseShuffles();
    }

    @GetMapping("/logged-all")
    public List<ShuffleModel> getAllShuffles() {
        return shuffleService.getAllShuffles();
    }

    @PutMapping("/update-log-status")
    @ResponseStatus(HttpStatus.OK)
    public void updateShuffleLogStatus(@RequestBody List<String> shuffleIds, @RequestParam boolean isLogged) {
        shuffleService.updateShuffleLogStatus(shuffleIds, isLogged);
    }

    @PostMapping("/delete-shuffles")
    @ResponseStatus(HttpStatus.OK)
    public void deleteShuffles(@RequestBody List<String> shuffleIds) {
        shuffleService.deleteShuffles(shuffleIds);
    }
}
