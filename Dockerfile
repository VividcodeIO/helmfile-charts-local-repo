FROM maven:3.6.3-adoptopenjdk-8 AS builder
RUN mkdir /build
ADD src /build/src
COPY pom.xml /build
RUN cd /build && mvn -B -ntp package

FROM openjdk:8-jre-buster

ENV HELM_VERSION="v3.3.4"
RUN wget -q https://get.helm.sh/helm-${HELM_VERSION}-linux-amd64.tar.gz -O - | tar -xzO linux-amd64/helm > /usr/local/bin/helm \
        && chmod +x /usr/local/bin/helm

COPY --from=builder /build/target/helmfile-charts-local-repo-1.0.0-SNAPSHOT-jar-with-dependencies.jar /app.jar
ADD run.sh /run.sh
RUN chmod +x /run.sh
CMD ["java", "-jar", "/app.jar"]