package plug.com.smartplug;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

/*
websocket Console -> https://api.cloudmqtt.com/sso/cloudmqtt/websocket
tutorial MQTT subscriber -> https://github.com/charany1/MQTT-Subscriber
 */

public class MainActivity extends AppCompatActivity implements MqttCallback {
    private TextView switchStatus, tvVoltage, tvPower, tvCurrent, tvDevice_slave;
    private static final String TAG = "MainActivity";
    private Switch mySwitch;
    private MQTTSample myMQTT = new MQTTSample();
    ToggleButton togglebutton;
    String deviceStatus;
    String voltage;
    String current;
    String power;
    String device_slave;
    String sender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addition(4,3);
        multiplication(3,4);

        switchStatus = (TextView) findViewById(R.id.tvSwitch);
        //mySwitch = (Switch) findViewById(R.id.switch1);
        tvVoltage = (TextView) findViewById(R.id.tvVoltage);
        tvPower = (TextView) findViewById(R.id.tvPower);
        tvCurrent = (TextView) findViewById(R.id.tvCurrent);
        tvDevice_slave = (TextView) findViewById(R.id.tvDevice_slave);
        togglebutton = (ToggleButton) findViewById(R.id.toggleButton1);

        //mySwitch.setChecked(true);
        //How do I know the state of the device on/off?

        /*
        * Android Switch button
        * attach a listener to check for changes in state
         */
//        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,
//                                         boolean isChecked) {
//
//                if (isChecked) {
//                    switchStatus.setText("Switch is currently ON");
//                    myMQTT.sendMessage("{\"command\":\"On\"}");
//
//
//                } else {
//                    switchStatus.setText("Switch is currently OFF");
//                    myMQTT.sendMessage("{\"command\":\"Off\"}");
//                }
//
//            }
//        });


        /*
        * Android ToggleButton button
        *
         */
        togglebutton = (ToggleButton) findViewById(R.id.toggleButton1);
        togglebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (togglebutton.isChecked()) {
                    Toast.makeText(MainActivity.this, "Toggle button is On", Toast.LENGTH_LONG).show();
                    //myMQTT.sendMessage("{\"command\":\"On\"}");
                    myMQTT.sendMessage("{\"command\":\"On\",\"sender\":\"AndroidApp\"}");
                } else {
                    Toast.makeText(MainActivity.this, "Toggle button is Off", Toast.LENGTH_LONG).show();
                    myMQTT.sendMessage("{\"command\":\"Off\",\"sender\":\"AndroidApp\"}");
                }
            }
        });


//
//        //check the current state before we display the screen
//        if (mySwitch.isChecked()) {
//            switchStatus.setText("Switch is currently ON");
//        } else {
//            switchStatus.setText("Switch is currently OFF");
//        }

        /*****************************************
         Below code Subscribes to Topic SmartPlugData
         1. Gets values of Voltage, Power and Current
         2. Updates UI with the corresponding data
         *****************************************/

        //MQTTConnect options : setting version to MQTT 3.1.1
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

        //http://stackoverflow.com/questions/36080656/mqtt-messages-persists-after-unsubscription-and-is-recieved-on-subscribing-again
        //https://gist.github.com/m2mIO-gister/5275324
        options.setCleanSession(true);
        options.setKeepAliveInterval(0);

        //Below code binds MainActivity to Paho Android Service via provided MqttAndroidClient
        // client interface
        String clientId = MqttClient.generateClientId();

        //My values
//        options.setUserName("tgpgjryu");
//        options.setPassword("IWJuk7yzRq00".toCharArray());
//        final MqttAndroidClient client =
//                new MqttAndroidClient(this.getApplicationContext(), "tcp://m12.cloudmqtt.com:12923",
//                        clientId);

        //Habid's values
//        options.setUserName("iajmzgae");
//        options.setPassword("bNl5xzae8mox".toCharArray());
//        final MqttAndroidClient client =
//                new MqttAndroidClient(this.getApplicationContext(), "tcp://m12.cloudmqtt.com:16186",
//                        clientId);

        //Heroku Server
        options.setUserName("cqskjfmy3");
        options.setPassword("8H2lj4mekk0v".toCharArray());
        final MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://m12.cloudmqtt.com:13087",
                        clientId);
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    Toast.makeText(MainActivity.this, "Connection successful", Toast.LENGTH_SHORT).show();
                    client.setCallback(MainActivity.this);
                    //subscribing to topic SmartPlug:
                    final String topic = "SmartPlugData";
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
        // {"deviceStatus":"On","voltage":"22","current":"1.3","power":"3","device_slave":"printer"}
        String JsonResponse = message.toString();
        JSONObject jsonObj = new JSONObject(JsonResponse);

        Toast.makeText(MainActivity.this, "Topic: " + topic + "\nMessage: " + message, Toast.LENGTH_LONG).show();

        /*
         TODO:
         Below code logic can be reduce to fewer lines, I think as it is; it's easier for others to follow :)
         Since device_slave comes from server, from logic below it will make all the values to 0, to solve this we need
         to use SharedPreferences to store the last know values for voltage, current and power (data persistence)
         */

        if (jsonObj.has("deviceStatus")) {
            deviceStatus = jsonObj.getString("deviceStatus").isEmpty() ? "isNull" : jsonObj.getString("deviceStatus");
        } else {
            deviceStatus = "";
        }

        if (jsonObj.has("voltage")) {
            voltage = jsonObj.getString("voltage").isEmpty() ? "isNull" : jsonObj.getString("voltage");
        } else {
            voltage = "0";
        }

        if (jsonObj.has("current")) {
            current = jsonObj.getString("current").isEmpty() ? "isNull" : jsonObj.getString("current");
        } else {
            current = "0";
        }

        if (jsonObj.has("power")) {
            power = jsonObj.getString("power").isEmpty() ? "isNull" : jsonObj.getString("power");
        } else {
            power = "0";
        }

        if (jsonObj.has("device_slave")) {
            device_slave = jsonObj.getString("device_slave").isEmpty() ? "isNull" : jsonObj.getString("device_slave");
        } else {
            device_slave = "";
        }

        if (jsonObj.has("sender")) {
            sender = jsonObj.getString("sender").isEmpty() ? "isNull" : jsonObj.getString("sender");
        } else {
            sender = "";
        }
//
        tvVoltage.setText(voltage + " V");
        tvCurrent.setText(current + " A");
        tvPower.setText(power + " W");
        tvDevice_slave.setText(device_slave + ",  sender:" + sender);

        //Update UI depending on device_status On/Off
        String switchStatusUpdate = deviceStatus;
        if (switchStatusUpdate.equals("On")) {
            togglebutton.setChecked(true);
            //mySwitch.setChecked(true);
            //Toast.makeText(MainActivity.this, "On received", Toast.LENGTH_SHORT).show();
        } else {
            togglebutton.setChecked(false);
            //mySwitch.setChecked(false);
        }
        switchStatus.setText("status: " + switchStatusUpdate);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    //    //TODO: (Issue) After some time the app stops the subscription and is not able to receive any message.
//    //Maybe try to find a way to reconnect every X mins....
//    public void restartActivity() {
//        Intent mIntent = getIntent();
//        finish();
//        startActivity(mIntent);
//        //Log.d("door",message.toString());
//    }
    public static int addition(int a, int b) {
        return a + b;
    }

    public static int multiplication(int a, int b) {
        return a * b;
    }


}


