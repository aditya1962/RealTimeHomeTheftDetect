<p align="center"><img src="https://github.com/aditya1962/RealTimeHomeTheftDetect/blob/main/public/images/logo.png" alt="logo"/></p>

<div align="center">
    
![Dependencies](https://img.shields.io/badge/dependencies-up%20to%20date-brightgreen.svg)
[![Android](https://img.shields.io/badge/android-23-blue.svg)](https://img.shields.io/badge/react-16.8%2B-blue.svg)
[![GitHub Issues](https://img.shields.io/github/issues/aditya1962/RealTimeHomeTheftDetect.svg)](https://github.com/aditya1962/RealTimeHomeTheftDetect/issues)
[![GitHub license](https://img.shields.io/github/license/aditya1962/RealTimeHomeTheftDetect)](https://github.com/aditya1962/RealTimeHomeTheftDetect/blob/master/LICENSE)

</div>

<p align="left"><b>RealTimeHomeTheftDetect</b> is the repository which hosts the source code of the Android and Arduino code used in the research.</p>

# System Architecture

The system operates as a detect–identify–notify– respond cycle. First, in the detection stage, a PIR sensor registering motion causes the Arduino to raise a theft event and transmit it over Bluetooth to the home-end Android application. Second, in the identification stage, the home-end application activates the camera and performs on-device face detection to determine whether it is a human; images are processed locally and are not uploaded. Third, in the notification stage, the event details—including the identification result—are written to the Firebase realtime database, which immediately notifies the application, providing the real-time, community-level alerting. Finally, in the response stage, the owner reviews the alert and issues an arming command from the application; the command travels back through Firebase to the homeend application and over Bluetooth to the Arduino, which drives the two-way relay to trigger the magnet lock. This human response ensures that a physical countermeasure is taken only when the owner confirms a genuine threat, reducing the risk of acting on a false alarm. The system architecture is shown in the figure below.

<p align="center"><img src="https://github.com/aditya1962/RealTimeHomeTheftDetect/blob/main/public/images/figure-1.png" alt="logo"/></p>
