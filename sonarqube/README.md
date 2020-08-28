## Instructions to run SonarQube locally

- Make sure Docker is installed on your machine
- Temporarily comment SonarCloud Properties in `pom.xml` (`sonar.projectKey`, `sonar.organization`, `sonar.host.url`) that are meant for CI
- Change directory to this folder 
- Run `docker-compose up` and wait until the local server is up (localhost:9000)
- Run `mvn clean test sonar:sonar` in project folder
