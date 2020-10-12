# Book Manager

![Build on CI with Maven](https://github.com/n3d1117/book-manager/workflows/Build%20on%20CI%20with%20Maven/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/n3d1117/book-manager/badge.svg)](https://coveralls.io/github/n3d1117/book-manager)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=n3d1117_book-manager&metric=alert_status)](https://sonarcloud.io/dashboard?id=n3d1117_book-manager)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=n3d1117_book-manager&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=n3d1117_book-manager)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=n3d1117_book-manager&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=n3d1117_book-manager)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=n3d1117_book-manager&metric=security_rating)](https://sonarcloud.io/dashboard?id=n3d1117_book-manager)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=n3d1117_book-manager&metric=bugs)](https://sonarcloud.io/dashboard?id=n3d1117_book-manager)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=n3d1117_book-manager&metric=code_smells)](https://sonarcloud.io/dashboard?id=n3d1117_book-manager)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=n3d1117_book-manager&metric=sqale_index)](https://sonarcloud.io/dashboard?id=n3d1117_book-manager)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=n3d1117_book-manager&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=n3d1117_book-manager)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=n3d1117_book-manager&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=n3d1117_book-manager)

A simple book manager, built using test driven development (TDD) with Java and Maven.

![Screenshot](https://user-images.githubusercontent.com/11541888/94684440-5e43aa00-0328-11eb-9f79-75986361e5fd.png)

## Requirements
* Java 8
* Maven 
* Docker

## Run Tests
To test the app, clone this repository somewhere on your machine and run the following command:
```bash
mvn clean verify
```

### Code Coverage
You can enable 100% code coverage checks with [JaCoCo](https://www.eclemma.org/jacoco/) by adding the `jacoco-check` profile to the previous command:
```bash
mvn clean verify -P jacoco-check
```

### Mutation Testing
Similarly, you can enable mutation testing with [PIT](https://pitest.org) by adding the `mutation-testing` profile:
```bash
mvn clean verify -P mutation-testing
```

## Run the app
* Build the fat Jar file using Maven:
```bash
mvn -DskipTests=true clean package
```
This will create a file called `book-manager-1.0-SNAPSHOT-jar-with-dependencies.jar` inside `target` folder. Alternatively, you can download a precompiled fat `.jar` from the [releases page](https://github.com/n3d1117/book-manager/releases).

* A MongoDB replica set is required for MongoDB transactions, as stated in [documentation](https://docs.mongodb.com/manual/core/transactions/). This project includes a `Dockerfile` that automatically deploys a single node replica set. You can build the image with:
```bash
docker build -t book-manager-db .
```
* Once done, run the image (specifying the port) and wait a few seconds:
```bash
docker run -p 27017:27017 --rm book-manager-db
```
* Finally, start the app:
```bash
java -jar target/book-manager-1.0-SNAPSHOT-jar-with-dependencies.jar [options]
```

### Available Options
| Option | Description |
|-|-|
| `--mongo-replica-set-url` | The URL of the MongoDB replica set. Defaults to `mongodb://localhost:27017` |
| `--db-name` | The database name. Defaults to `bookmanager` |
| `--db-author-collection` | Name of the authors collection in database. Defaults to `authors` |
| `--db-book-collection` | Name of the books collection in database. Defaults to `books` |

### Coveralls Integration
To enable [Coveralls](https://coveralls.io) integration, enable the `jacoco-report` profile to generate the JaCoCo report, and then add the `coveralls:report` goal:
```bash
mvn clean verify -P jacoco-report coveralls:report -D repoToken=YOUR_COVERALLS_TOKEN
```
*Note:* replace `YOUR_COVERALLS_TOKEN` with your Coveralls token.

### SonarQube Integration
To test the project locally with [SonarQube](https://www.sonarqube.org), a Docker Compose file is included in the `sonarqube` folder. Run the following commands to start the local analysis:
```bash
$ cd sonarqube
$ docker-compose up
$ cd ..
$ mvn clean verify sonar:sonar
```
See [sonarqube folder](sonarqube) for more information.

### SonarCloud Integration
To enable [SonarCloud](https://sonarcloud.io) code analysis, enable the `sonar:sonar` goal when testing:
```bash
mvn clean verify sonar:sonar \
-D sonar.host.url=SONAR_URL \
-D sonar.organization=SONAR_ORGANIZATION \
-D sonar.projectKey=SONAR_PROJECT
```
*Note:* replace `SONAR_URL`, `SONAR_ORGANIZATION` and `SONAR_PROJECT` with your values. You'll find them in the SonarCloud dashboard once you set up the project. You will also need to specify an environment variable `SONAR_TOKEN` with your own SonarCloud token.

## Continuous Integration
This repository uses [Github Actions](https://github.com/features/actions) to build, test, and deploy the app right from GitHub. Check out the `.github/workflows` folder for more information.
To increase reliability, all tests on CI servers are executed on a secondary desktop with [TightVNC](https://www.tightvnc.com), using the `execute-on-vnc.sh` script included in this project, as recommended in the [official AssertJ Swing documentation](https://joel-costigliola.github.io/assertj/assertj-swing-running.html).

## Project Report
Full project report is available [here](project-report.md).  