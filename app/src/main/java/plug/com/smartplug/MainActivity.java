package plug.com.smartplug;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/*
websocket Console -> https://api.cloudmqtt.com/sso/cloudmqtt/websocket
tutorial MQTT subscriber -> https://github.com/charany1/MQTT-Subscriber
JSON reponse {"voltage":"<>","current":"<>","power":"<>"}
 */

public class MainActivity extends AppCompatActivity implements MqttCallback {
    private TextView switchStatus, tvVoltage, tvPower, tvCurrent;
    private static final String TAG = "MainActivity";
    private Switch mySwitch;
    private MQTTSample myMQTT = new MQTTSample();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchStatus = (TextView) findViewById(R.id.tvSwitch);
        mySwitch = (Switch) findViewById(R.id.switch1);
        tvVoltage = (TextView) findViewById(R.id.tvVoltage);
        tvPower = (TextView) findViewById(R.id.tvPower);
        tvCurrent = (TextView) findViewById(R.id.tvCurrent);
        //mySwitch.setChecked(true);
        //How do I know the state of the device on/off?

        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    switchStatus.setText("Switch is currently ON");
                    myMQTT.sendMessage("{\"command\":\"On\"}");


                } else {
                    switchStatus.setText("Switch is currently OFF");
                    myMQTT.sendMessage("{\"command\":\"Off\"}");
                }

            }
        });

        //check the current state before we display the screen
        if (mySwitch.isChecked()) {
            switchStatus.setText("Switch is currently ON");
        } else {
            switchStatus.setText("Switch is currently OFF");
        }

        //*****************************************
        //Below implementation odf Subscriber
        //*****************************************

        //MQTTConnect options : setting version to MQTT 3.1.1
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName("tgpgjryu");
        options.setPassword("IWJuk7yzRq00".toCharArray());

        //Below code binds MainActivity to Paho Android Service via provided MqttAndroidClient
        // client interface
        //Todo : Check why it wasn't connecting to test.mosquitto.org. Isn't that a public broker.
        //Todo : .check why client.subscribe was throwing NullPointerException  even on doing subToken.waitForCompletion()  for Async                  connection estabishment. and why it worked on subscribing from within client.connect’s onSuccess(). SO
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://m12.cloudmqtt.com:12923",
                        clientId);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    Toast.makeText(MainActivity.this, "Connection successful", Toast.LENGTH_SHORT).show();
                    //Subscribing to a topic door/status on broker.hivemq.com
                    client.setCallback(MainActivity.this);
                    //subscribing to topic voltage:
                    final String topic = "voltage";
                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(topic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // successfully subscribed
                                Toast.makeText(MainActivity.this, "Successfully subscribed to: " + topic, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards
                                Toast.makeText(MainActivity.this, "Couldn't subscribe to: " + topic, Toast.LENGTH_SHORT).show();

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

//Message will be below, we will need to parse the Json response for each field.
// {"voltage":"<>","current":"<>","power":"<>"}

        /*
        * To test ,publish  "open"/"close" at topic you subscibed app to in above .
        * */
//        Log.d("door",message.toString());
//
        Toast.makeText(MainActivity.this, "Topic: " + topic + "\nMessage: " + message, Toast.LENGTH_LONG).show();
        tvVoltage.setText(message.toString());
        tvCurrent.setText(message.toString());
        tvPower.setText(message.toString());

        //restartActivity();

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public void restartActivity() {
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
    }


}


