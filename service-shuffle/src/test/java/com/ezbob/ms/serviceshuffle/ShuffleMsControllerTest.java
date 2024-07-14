package com.ezbob.ms.serviceshuffle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.ezbob.ms.serviceshuffle.dto.ShuffleRequest;
import com.ezbob.ms.serviceshuffle.model.ShuffleModel;
import com.ezbob.ms.serviceshuffle.repository.ShuffleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ShuffleMsControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ShuffleMsControllerTest.class);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ShuffleRepository shuffleRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        shuffleRepository.deleteAll();
        logger.info("Database cleared before each test.");
    }

    @Test
    void shouldCreateShuffleRequestAndStoreInDB() throws Exception {
        int shuffleTillThisNumber = 5;
        ShuffleRequest request = ShuffleRequest.builder()
                .shuffleTillThisNumber(shuffleTillThisNumber)
                .build();

        logger.info("Sending POST request to create shuffle request with number: {}", shuffleTillThisNumber);

        mockMvc.perform(post("/api/service-shuffle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        List<ShuffleModel> shuffleModels = shuffleRepository.findAll();
        assertThat(shuffleModels).hasSize(1);
        ShuffleModel storedModel = shuffleModels.get(0);

        logger.info("Shuffle request stored in DB: {}", storedModel);

        assertThat(storedModel.getRequest().getShuffleTillThisNumber()).isEqualTo(shuffleTillThisNumber);
    }

    @Test
    void shouldGetAllLoggedFalseShuffles() throws Exception {
        ShuffleModel model = ShuffleModel.builder()
                .shuffleId(UUID.randomUUID().toString())
                .request(ShuffleRequest.builder().shuffleTillThisNumber(5).build())
                .response(List.of(1, 2, 3, 4, 5))
                .isLogged(false)
                .build();
        shuffleRepository.save(model);

        logger.info("Saved shuffle model with isLogged=false: {}", model);

        mockMvc.perform(get("/api/service-shuffle/logged-false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shuffleId").value(model.getShuffleId()));

        logger.info("Retrieved shuffle models with isLogged=false.");
    }

    @Test
    void shouldUpdateShuffleLogStatus() throws Exception {
        String shuffleId = UUID.randomUUID().toString();
        ShuffleModel model = ShuffleModel.builder()
                .shuffleId(shuffleId)
                .request(ShuffleRequest.builder().shuffleTillThisNumber(5).build())
                .response(List.of(1, 2, 3, 4, 5))
                .isLogged(false)
                .build();
        shuffleRepository.save(model);

        logger.info("Saved shuffle model: {}", model);

        mockMvc.perform(put("/api/service-shuffle/update-log-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(shuffleId)))
                        .param("isLogged", "true"))
                .andExpect(status().isOk());

        Optional<ShuffleModel> updatedModel = shuffleRepository.findById(shuffleId);
        assertThat(updatedModel).isPresent();
        assertThat(updatedModel.get().isLogged()).isTrue();

        logger.info("Updated isLogged status for shuffle model with ID: {}", shuffleId);
    }
}
