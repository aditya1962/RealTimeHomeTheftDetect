<p align="center"><img src="https://github.com/aditya1962/RealTimeHomeTheftDetect/blob/main/public/images/logo.png" alt="logo"/></p>

<div align="center">
    
![Dependencies](https://img.shields.io/badge/dependencies-up%20to%20date-brightgreen.svg)
[![Android](https://img.shields.io/badge/android-23-blue.svg)](https://img.shields.io/badge/react-16.8%2B-blue.svg)
[![GitHub Issues](https://img.shields.io/github/issues/aditya1962/RealTimeHomeTheftDetect.svg)](https://github.com/aditya1962/RealTimeHomeTheftDetect/issues)
[![GitHub license](https://img.shields.io/github/license/aditya1962/RealTimeHomeTheftDetect)](https://github.com/aditya1962/RealTimeHomeTheftDetect/blob/master/LICENSE)

</div>

<p align="left"><b>RealTimeHomeTheftDetect</b> is the repository which hosts the source code of the Android and Arduino code used in the [research](https://doi.org/10.31224/8248).</p>

# System Architecture

The system operates as a detect–identify–notify– respond cycle. First, in the detection stage, a PIR sensor registering motion causes the Arduino to raise a theft event and transmit it over Bluetooth to the home-end Android application. Second, in the identification stage, the home-end application activates the camera and performs on-device face detection to determine whether it is a human; images are processed locally and are not uploaded. Third, in the notification stage, the event details—including the identification result—are written to the Firebase realtime database, which immediately notifies the application, providing the real-time, community-level alerting. Finally, in the response stage, the owner reviews the alert and issues an arming command from the application; the command travels back through Firebase to the homeend application and over Bluetooth to the Arduino, which drives the two-way relay to trigger the magnet lock. This human response ensures that a physical countermeasure is taken only when the owner confirms a genuine threat, reducing the risk of acting on a false alarm. The system architecture is shown in the figure below.

<p align="center"><img src="https://github.com/aditya1962/RealTimeHomeTheftDetect/blob/main/public/images/figure-1.png" alt="logo"/></p>

# Hardware components of the system

| Component                | Usage                                                                                |
|--------------------------|--------------------------------------------------------------------------------------|
| Arduino Uno              | Embedded controller; reads the PIR sensors and drives the actuation logic.           |
| PIR motion sensor        | Detects movement in the monitored area and triggers the detection logic.             |
| Two-way relay switch     | Engages or releases the magnet lock that secures the premises on the user's command. |
| HC-05 Bluetooth module   | Wireless serial link between the Arduino and the home-end Android phone.             |
| 2 Android phones (with the Android application         | Captures the image for face detection and runs the home-end application.             |

# Software components of the system

| Component                                      | Usage                                                                                                                                                                                                                                                                                                |
|------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Arduino IDE                                    | Development environment used to write and upload the C firmware to the Arduino.                                                                                                                                                                                                                      |
| C language                                     | Language of the Arduino firmware: sensor reading, relay actuation and Bluetooth I/O.                                                                                                                                                                                                                 |
| Google Mobile Vision API                       | Performs on-device face detection on the captured image; images are processed on the phone and never leave the device.                                                                                                                                                                               |
| Firebase (real-time database and web services) | Cloud back-end for real-time data exchange, storage and push notifications.                                                                                                                                                                                                                          |
| Android application                            | The same application runs at both the home end and the home-owner end: it activates the camera and runs face detection, exchanges data with Firebase and forwards commands to the Arduino, and receives notifications, issues the arming command and the user may notifies neighbors and the police. |

# Setting up software

- Create a Firebase database using the [console](https://console.firebase.google.com/). Then get its database URI. 
- Clone this repo and open the [TheftDetection](https://github.com/aditya1962/RealTimeHomeTheftDetect/tree/main/TheftDetection) code using Android Studio
- Download the ```google-services.json``` file from the database created and replace it in [path](TheftDetection/app/google-services.json)
- Change the database URI in local.properties file to the URI of the database created
- Build the project and install it in both Android phones 

# Setting up hardware

- The hardware mentioned above need to be setup as in the below Fritzing diagram.

<p align="center"><img src="https://github.com/aditya1962/RealTimeHomeTheftDetect/blob/main/public/images/Fritzing%20hardware%20diagram.png" alt="FritzingDiagram"/></p>

- Then connect the Android phone which has installed the Android application (home end) to Wi-Fi. Upload the [Arduino code](https://github.com/aditya1962/RealTimeHomeTheftDetect/blob/main/pir_sensor.ino) to the Arduino board using Arduino IDE and power on the Arduino Uno board.

