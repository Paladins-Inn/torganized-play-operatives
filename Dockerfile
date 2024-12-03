FROM quay.io/kaiserpfalzedv/java-runner:21-latest AS runner

COPY --chown=root:root --chmod=0555 ./target/app.jar /deployments