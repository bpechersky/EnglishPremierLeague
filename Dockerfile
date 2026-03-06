FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY src/ src/
RUN mkdir -p out dist && \
    find src/main/java -name "*.java" > sources.txt && \
    javac -d out @sources.txt && \
    cp -r src/main/resources/static out/static && \
    jar --create --file dist/premier-league.jar --main-class com.pl.Main -C out .

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/dist/premier-league.jar .
# Data directory for persistence (mount a volume here on Railway)
RUN mkdir -p /app/data
EXPOSE 8080
CMD ["java", "-jar", "premier-league.jar"]
