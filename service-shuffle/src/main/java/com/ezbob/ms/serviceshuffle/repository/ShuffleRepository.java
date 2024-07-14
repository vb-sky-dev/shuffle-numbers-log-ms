package com.ezbob.ms.serviceshuffle.repository;


import com.ezbob.ms.serviceshuffle.model.ShuffleModel;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public interface ShuffleRepository extends MongoRepository<ShuffleModel, String> {
    List<ShuffleModel> findByIsLoggedFalse();
}
