# MQTT-Message-Replicator

A simple tool that republishes messages from an MQTT Broket to another one.

Usage:

docker run \ 
	-e MQTT_FROM_HOST=tcp://localhost:1883 \
	-e MQTT_FROM_USERNAME=xxxx
	-e MQTT_FROM_CLIENTID=xxxx \
	-e MQTT_FROM_TOPIC=# \
	-e MQTT_TO_QOS=1 
	-e MQTT_TO_HOST=tcp://localhost:1884 \
    -e MQTT_TO_USERNAME=replicator \
    -e MQTT_TO_CLIENTID=replicator \ 
	dometec/mqtt-message-replicator:1.0.2


Build and publish

$ mvn clean package 
$ docker build . -t dometec/mqtt-message-replicator:1.0.2
$ docker push dometec/mqtt-message-replicator:1.0.2