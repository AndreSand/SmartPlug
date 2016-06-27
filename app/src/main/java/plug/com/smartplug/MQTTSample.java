package plug.com.smartplug;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * MQTT documentation: https://www.cloudmqtt.com/docs-java.html
 * TODO implement mBinder http://dalelane.co.uk/blog/?p=1599
 * Created by andres on 5/1/16.
 */

public class MQTTSample {
    public static void main(String[] args) {
//        MQTTSample updEmple = new MQTTSample();
//        updEmple.sendMessage("hello");
    }

    public void sendMessage(String s) {
        // String topic = "CloudMQTT";
        String topic = "SmartPlug";

        String content = s;
        int qos = 1;
        String broker = "tcp://m12.cloudmqtt.com:12923";  //My endpoint
        //String broker = "tcp://m12.cloudmqtt.com:16186"; //Habid

        //MQTT client id to use for the device. "" will generate a client id automatically
        String clientId = "ClientId";

        MemoryPersistence persistence = new MemoryPersistence();
        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
            mqttClient.setCallback(new MqttCallback() {
                public void messageArrived(String topic, MqttMessage msg)
                        throws Exception {
                    System.out.println("Recived:" + topic);
                    System.out.println("Recived:" + new String(msg.getPayload()));
                }

                public void deliveryComplete(IMqttDeliveryToken arg0) {
                    System.out.println("Delivary complete");
                }

                public void connectionLost(Throwable arg0) {
                    // TODO Auto-generated method stub
                }
            });

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName("tgpgjryu");
            connOpts.setPassword("IWJuk7yzRq00".toCharArray());

            //Habid values
            //connOpts.setUserName("iajmzgae");//Habid
            //connOpts.setPassword(new char[]{'b', 'N', 'l', '5', 'x', 'z', 'a', 'e', '8', 'm', 'o', 'x'}); //Habid
            mqttClient.connect(connOpts);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            System.out.println("Publish message: " + message);
            mqttClient.subscribe(topic, qos);
            mqttClient.publish(topic, message);
            mqttClient.disconnect();
            //System.exit(0);
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}