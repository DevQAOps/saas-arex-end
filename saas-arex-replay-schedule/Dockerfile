FROM maven:3-openjdk-8-slim as builder
COPY . /usr/src/app/
WORKDIR /usr/src/app
RUN mvn clean package -pl saas-arex-replay-schedule -am -DskipTests=true -Pjar

FROM eclipse-temurin:8-jre
COPY --from=builder /usr/src/app/saas-end-jar/schedule.jar schedule.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /schedule.jar"]
