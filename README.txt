Build and publish

$ mvn clean package 
$ docker build . -t dometec/mqtt-message-replicator:1.0.0
$ docket push dometec/mqtt-message-replicator:1.0.0