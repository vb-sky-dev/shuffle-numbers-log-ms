package com.ezbob.ms.servicelog.repository;

import com.ezbob.ms.servicelog.model.ShuffleLoggedModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShuffleLoggedRepository extends MongoRepository<ShuffleLoggedModel, String> {
}
