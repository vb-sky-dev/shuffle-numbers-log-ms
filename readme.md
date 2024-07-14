# Shuffle Microservice Project

## Overview

This project contains two microservices: shuffle-service and log-service. The shuffle-service handles shuffle requests, updates log status, and retrieves unlogged shuffles. The log-service checks for unlogged shuffles, moves them to a log table, and deletes logged entries. The project uses MongoDB for data storage, and tests are provided to ensure the functionality of the services.
## Prerequisites

- Docker and Docker Compose
- Java 17 and Maven

## Getting Started

### Step 1: Start Docker Compose

Navigate to the root directory of the project and run the following command to start MongoDB and mongo-express:

```sh
docker-compose up -d
```

#### MongoDB will be available at 
```sh 
mongodb://localhost:27017mongo-express will be available at http://localhost:8081
```

### Step 2: Run Tests for Shuffle MS
The project contains two test classes: **ShuffleMsControllerMongoDbTest** and **ShuffleMsControllerTest**. 
Follow the instructions below to run and verify the tests.

#### ShuffleMsControllerMongoDbTest
*send5RequestsIsLoggedFalse*

Sends 5 random shuffle requests with isLogged set to false.
Verify the entries in MongoDB using mongo-express at http://localhost:8081.
---
*updateAllIsLoggedFalse*

Updates all entries with isLogged set to false to true.
Verify the updated entries in MongoDB using mongo-express.
---
*clearDbTest*

Clears all records in the database.
Verify that the database is empty using mongo-express.
---

#### ShuffleMsControllerTest

*shouldCreateShuffleRequestAndStoreInDB*
Sends a shuffle request and verifies it is stored in the database.
---
*shouldGetAllLoggedFalseShuffles*
Retrieves all entries with isLogged set to false and verifies the response.
---

*shouldUpdateShuffleLogStatus*
Updates the isLogged status of specific entries and verifies the update.

### Step 3: Run Tests for Log MS
```info
To test the log-service, follow these steps

Verify the data CRUD operations in MongoDB using mongo-express.
After each test, check the database:
```

**Start the shuffle-service**:

1. Run send5RequestsIsLoggedFalse in shuffle-service module:
Ensure there are 5 shuffle requests with isLogged set to false in the database.
2. Run checkAndMoveUnloggedRecords:
This will move unlogged records to the log table in the log-service.
Verify the entries in the log table using mongo-express.
3. Run compareAndDeleteRecords:
This will compare and delete logged entries from the shuffle-service.


### Accessing mongo-express:

After running the tests, you can use mongo-express to manually verify the database state:

Open your web browser and go to http://localhost:8081

Log in with the username root and password example (as specified in the docker-compose.yml file)