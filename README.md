# News-m

A Kotlin application that fetches news and posts updates to Telegram.

## Local development

1. Copy `.env.example` to `.env` and supply the required values.
2. Run tests and build:
   ```bash
   ./gradlew test
   ```
3. Start the application:
   ```bash
   ./gradlew run
   ```

## Docker

1. Build the fat jar and image:
   ```bash
   ./gradlew build
   docker build -t news-m .
   ```
2. Run the container with your environment variables:
   ```bash
   docker run --env-file .env news-m
   ```

The image contains a minimal JRE produced with `jlink` and runs the packaged jar with `java -jar app.jar`.
