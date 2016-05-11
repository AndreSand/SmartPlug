package plug.com.smartplug;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView switchStatus;
    private Switch mySwitch;
    private MQTTSample myMQTT = new MQTTSample();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchStatus = (TextView) findViewById(R.id.tvSwitch);
        mySwitch = (Switch) findViewById(R.id.switch1);
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
    }

}

