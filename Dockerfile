# First stage: complete build environment
FROM maven:3.9.3-eclipse-temurin-17-alpine AS builder

# add pom.xml and source code
COPY ./pom.xml pom.xml
COPY ./src src/

# package jar
RUN mvn clean package

# Second stage: minimal runtime environment
FROM maven:3.9.3-eclipse-temurin-17-alpine

# copy jar from the first stage
COPY --from=builder target/ASR-0.0.1-SNAPSHOT.jar ASR-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "ASR-0.0.1-SNAPSHOT.jar"]
