FROM public.ecr.aws/docker/library/maven:3.9.9-eclipse-temurin-21 as builder
COPY . /usr/src/app/
WORKDIR /usr/src/app
RUN mvn clean package -pl saas-DevOps -am -DskipTests=true -Pjar

FROM public.ecr.aws/docker/library/maven:3.9.9-eclipse-temurin-21
COPY --from=builder /usr/src/app/saas-end-jar/devops.jar devops.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /devops.jar"]