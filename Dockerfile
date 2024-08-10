FROM docker.io/maven:3.9.8-amazoncorretto-21 AS builder

WORKDIR /usr/src/app

COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app

RUN mvn --batch-mode --no-transfer-progress --file /usr/src/app/pom.xml clean package \
    && cp target/app.jar /

FROM quay.io/kaiserpfalzedv/java-runner:21-latest AS runner

COPY --from=builder --chown=root:root --chmod=0555 /app.jar /deployments