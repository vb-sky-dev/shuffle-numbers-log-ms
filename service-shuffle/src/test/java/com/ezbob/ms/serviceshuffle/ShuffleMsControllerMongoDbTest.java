package com.ezbob.ms.serviceshuffle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.ezbob.ms.serviceshuffle.dto.ShuffleRequest;
import com.ezbob.ms.serviceshuffle.model.ShuffleModel;
import com.ezbob.ms.serviceshuffle.repository.ShuffleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ShuffleMsControllerMongoDbTest {
    private static final Logger logger = LoggerFactory.getLogger(ShuffleMsControllerMongoDbTest.class);

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
    void send5RequestsIsLoggedFalse() throws Exception {
        for (int i = 0; i < 5; i++) {
            int shuffleTillThisNumber = (int) (Math.random() * 100) + 1;
            ShuffleRequest request = ShuffleRequest.builder()
                    .shuffleTillThisNumber(shuffleTillThisNumber)
                    .build();

            logger.info("Sending POST request to create shuffle request with number: {}", shuffleTillThisNumber);

            mockMvc.perform(post("/api/service-shuffle")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        List<ShuffleModel> shuffleModels = shuffleRepository.findAll();
        assertThat(shuffleModels).hasSize(5);

        logger.info("Stored shuffle requests in DB: {}", shuffleModels);
    }

    @Test
    void updateAllIsLoggedFalse() throws Exception {
        List<ShuffleModel> models = IntStream.range(0, 5)
                .mapToObj(i -> ShuffleModel.builder()
                        .shuffleId(UUID.randomUUID().toString())
                        .request(ShuffleRequest.builder().shuffleTillThisNumber((int) (Math.random() * 100) + 1).build())
                        .response(List.of(1, 2, 3, 4, 5))
                        .isLogged(false)
                        .build())
                .collect(Collectors.toList());

        shuffleRepository.saveAll(models);
        logger.info("Initial entries saved with isLogged=false.");

        List<String> shuffleIds = models.stream().map(ShuffleModel::getShuffleId).collect(Collectors.toList());
        mockMvc.perform(put("/api/service-shuffle/update-log-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shuffleIds))
                        .param("isLogged", "true"))
                .andExpect(status().isOk());

        List<ShuffleModel> updatedModels = shuffleRepository.findAll();
        assertThat(updatedModels).allMatch(model -> model.isLogged() == true);

        logger.info("All entries updated to isLogged=true.");
    }

    @Test
    void clearDbTest() {
        shuffleRepository.deleteAll();
        logger.info("Database cleared.");

        List<ShuffleModel> shuffleModels = shuffleRepository.findAll();
        assertThat(shuffleModels).isEmpty();
    }
}
