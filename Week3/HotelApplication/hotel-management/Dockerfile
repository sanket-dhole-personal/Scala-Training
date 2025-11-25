# ===============================
# 1. BUILD STAGE (JDK 17 + SBT 1.10.0)
# ===============================
FROM hseeberger/scala-sbt:17.0.5_1.8.0_2.13.10 AS build
# This image contains: JDK 17 + Scala + sbt

# Override sbt version to match build.properties
ENV SBT_VERSION=1.10.0

WORKDIR /app

# Copy project files
COPY . /app

# Build Play 3 project and generate production files
RUN sbt clean compile stage


# ===============================
# 2. RUN STAGE (JRE 17)
# ===============================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy only staged production output (small size)
COPY --from=build /app/target/universal/stage /app

# Play server default port (change if required)
EXPOSE 9001

# Remember to externalize this for production (env file)
ENV PLAY_HTTP_SECRET=thisisasecretfortheapplicationandwekeepittosecuretheapplicationletuscheckandithastowork

# JVM runtime settings (editable)
ENV JAVA_OPTS="-Xms512M -Xmx1024M"

# App Execution
CMD ["sh", "-c", "./bin/hotel-management -Dplay.http.secret.key=${PLAY_HTTP_SECRET} ${JAVA_OPTS}"]
