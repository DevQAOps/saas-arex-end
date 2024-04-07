FROM maven:3-openjdk-8-slim as builder
COPY .. /usr/src/app/
WORKDIR /usr/src/app
RUN mvn clean package -pl saas-arex-storage -am -DskipTests=true -Pjar

FROM eclipse-temurin:8-jre
COPY --from=builder /usr/src/app/saas-end-jar/storage.jar storage.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /storage.jar"]
