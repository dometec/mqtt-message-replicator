FROM openjdk:8-jdk-alpine

LABEL Domenico Briganti <dometec@gmail.com>

COPY target/mqtt-message-replicator-*-jar-with-dependencies.jar mqtt-message-replicator.jar

CMD ["java", "-jar", "mqtt-message-replicator.jar"]