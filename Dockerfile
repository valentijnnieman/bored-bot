FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/bored-bot-1.0.0-SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
CMD exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
