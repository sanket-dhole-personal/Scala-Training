# ===============================
# 1. BUILD STAGE (JDK 17 + Scala 2.13.14 + sbt 1.10.0)
# ===============================
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.15_6_1.11.1_3.3.6 AS build

WORKDIR /app

# Copy project
COPY . /app

# Build Play app
RUN sbt clean compile stage


# ===============================
# 2. RUN STAGE (JDK 17)
# ===============================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy staged app
COPY --from=build /app/target/universal/stage /app

EXPOSE 9001

ENV PLAY_HTTP_SECRET=thisisasecretfortheapplicationandwekeepittosecuretheapplicationletuscheckandithastowork
ENV JAVA_OPTS="-Xms512M -Xmx1024M"

# Correct executable script name
CMD ["sh", "-c", "./bin/hotel-management -Dplay.http.secret.key=${PLAY_HTTP_SECRET} ${JAVA_OPTS}"]
