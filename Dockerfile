# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk AS jre-build
RUN $JAVA_HOME/bin/jlink \
    --add-modules java.se \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /opt/jre

FROM debian:bookworm-slim
WORKDIR /app
COPY --from=jre-build /opt/jre /opt/jre
ENV PATH="/opt/jre/bin:${PATH}"
COPY build/libs/*-all.jar app.jar
CMD ["java", "-jar", "app.jar"]
