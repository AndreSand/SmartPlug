SmartPlug Project
-

>Android application turns on/off appliance, also gets: voltage, current and power data from appliance and displays values in the app.

>SmartPlug device design and develop by [hardware team](https://github.com/hsrascon/SmartPlug/blob/master/Firmware/WL1/ArduinoCode/SmartPlugWL1_V04.ino). Todo add image of device.


###Device subscribes to Topic (app driven)

1. Device subscribes to topic: "**SmartPlug**"
2. App sends below messages to turn on/off appliance

    Send On command to device:
`myMQTT.sendMessage("{\"command\":\"On\"}");`

    Send Off command to device:
`myMQTT.sendMessage("{\"command\":\"Off\"}");`

3. Device parses message and turns on/off appliance


###App subscribes to Topic (device driven)
1. Android app subscribes to topic: "**SmartPlugData**"
2. Receives message:
 `{"deviceStatus":"On","voltage":"22","current":"1.3","power":"3"}`
3. App parses message and displays values in the app


###Validate
Using Chrome plugin [MQTTLens](https://chrome.google.com/webstore/detail/mqttlens/hemojaaeigabkbcookmlgmdigohjobjm?hl=en) to test Android app functionality,
it lets you:

    - Create a topic and send messages
    - Subscribe to topic and receive messages


##Demo 
<img src='SmartPlug1.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />


###Run code

1. Install [Android Studio SDK](https://developer.android.com/studio/install.html)
2. Pull code
3. Create [CloudMQTT](https://api.cloudmqtt.com/sso/cloudmqtt/websocket) account. Change MQTT values with your values. Make value changes in MainActivity and MQTTSample. If it doesn't work ping me.
4. Have fun


###Notes



GIF created with [LiceCap](http://www.cockos.com/licecap/)

[README.md preview](http://tmpvar.com/markdown.html)