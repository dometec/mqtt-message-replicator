package it.osys.messagereplicator;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements MqttCallback {

	protected static final Logger logger = LoggerFactory.getLogger(Main.class);

	protected static String MQTT_FROM_HOST = System.getenv("MQTT_FROM_HOST");
	protected static String MQTT_FROM_USERNAME = System.getenv("MQTT_FROM_USERNAME");
	protected static String MQTT_FROM_PASSWORD = System.getenv("MQTT_FROM_PASSWORD");
	protected static String MQTT_FROM_CLIENTID = System.getenv("MQTT_FROM_CLIENTID");
	protected static String MQTT_FROM_TOPIC = System.getenv("MQTT_FROM_TOPIC");

	protected static String MQTT_TO_HOST = System.getenv("MQTT_TO_HOST");
	protected static String MQTT_TO_USERNAME = System.getenv("MQTT_TO_USERNAME");
	protected static String MQTT_TO_PASSWORD = System.getenv("MQTT_TO_PASSWORD");
	protected static String MQTT_TO_CLIENTID = System.getenv("MQTT_TO_CLIENTID");

	public static void main(String[] args) throws Exception {
		Main main = new Main();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info("Shutting down...");
				main.shutdown();
			}
		});

	}

	private MqttClient brokerTo;
	private MqttClient brokerFrom;

	private Main() throws Exception {

		logger.info("Start Message Replicator from {} to {}.", MQTT_FROM_HOST, MQTT_TO_HOST);

		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(true);
		options.setConnectionTimeout(60);
		options.setKeepAliveInterval(10);
		options.setAutomaticReconnect(true);
		options.setMaxInflight(200);
		options.setUserName(MQTT_TO_USERNAME);
		if (MQTT_TO_PASSWORD != null)
			options.setPassword(MQTT_TO_PASSWORD.toCharArray());

		logger.info("Connection to {}...", MQTT_TO_HOST);
		brokerTo = new MqttClient(MQTT_TO_HOST, MQTT_TO_CLIENTID, new MemoryPersistence());
		brokerTo.connect(options);

		options = new MqttConnectOptions();
		options.setCleanSession(true);
		options.setConnectionTimeout(60);
		options.setKeepAliveInterval(10);
		options.setAutomaticReconnect(true);
		options.setMaxInflight(200);
		options.setUserName(MQTT_FROM_USERNAME);
		if (MQTT_FROM_PASSWORD != null)
			options.setPassword(MQTT_FROM_PASSWORD.toCharArray());

		logger.info("Connection to {}...", MQTT_FROM_HOST);
		brokerFrom = new MqttClient(MQTT_FROM_HOST, MQTT_FROM_CLIENTID, new MemoryPersistence());
		brokerFrom.setCallback(this);
		brokerFrom.connect(options);
		brokerFrom.subscribe(MQTT_FROM_TOPIC, 0);

		logger.info("Runninng...");
	}

	private void shutdown() {

		try {
			if (brokerFrom != null && brokerFrom.isConnected())
				brokerFrom.disconnect();
		} catch (MqttException e) {
			// ignore
		}

		try {
			if (brokerTo != null && brokerTo.isConnected())
				brokerTo.disconnect();
		} catch (MqttException e) {
			// ignore
		}

	}

	@Override
	public void connectionLost(Throwable arg0) {
		logger.warn("connectionLost", arg0);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		logger.warn("deliveryComplete", arg0);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {

		if (message.isRetained())
			logger.info("Pubblish retained message on {}: {}.", topic, message);
		else
			logger.trace("Publish message on {}.", topic);

		brokerTo.publish(topic, message);

	}

}
