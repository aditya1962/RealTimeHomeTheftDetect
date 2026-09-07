#include <SoftwareSerial.h>

#define RELAY1  7 

SoftwareSerial mySerial(10, 11); // RX, TX


//the time we give the sensor to calibrate (10-60 secs according to the datasheet)
int calibrationTime = 30;       
 
//the time when the sensor outputs a low impulse
long unsigned int lowIn;        
 
//the amount of milliseconds the sensor has to be low
//before we assume all motion has stopped
long unsigned int pause = 5000; 
 
boolean lockLow = true;
boolean takeLowTime; 
 
int pirPin = 3;    //the digital pin connected to the PIR sensor's output
int ledPin = 13;
 
String string = "";
 
/////////////////////////////
//SETUP
void setup(){
  Serial.begin(9600);
  pinMode(pirPin, INPUT);
  pinMode(ledPin, OUTPUT);
  digitalWrite(pirPin, LOW);
 
  //give the sensor some time to calibrate
  Serial.print("calibrating sensor ");
    for(int i = 0; i < calibrationTime; i++){
      Serial.print(".");
      delay(1000);
      }
    Serial.println(" done");
    Serial.println("SENSOR ACTIVE");
    delay(50);
  }
 
////////////////////////////
//LOOP
void loop(){

boolean valid = false;
 
     if(digitalRead(pirPin) == HIGH){
       digitalWrite(ledPin, HIGH);   //the led visualizes the sensors output pin state
       if(lockLow){ 
         //makes sure we wait for a transition to LOW before any further output is made:
         lockLow = false;           
         Serial.println("---");
         Serial.print("motion detected at ");
         Serial.print(millis()/1000);
         Serial.println(" sec");
         valid = true;
         delay(50);
         }        
         takeLowTime = true;
       }
 
     if(digitalRead(pirPin) == LOW){      
       digitalWrite(ledPin, LOW);  //the led visualizes the sensors output pin state
 
       if(takeLowTime){
        lowIn = millis();          //save the time of the transition from high to LOW
        takeLowTime = false;       //make sure this is only done at the start of a LOW phase
        }
       //if the sensor is low for more than the given pause,
       //we assume that no more motion is going to happen
       if(!lockLow && millis() - lowIn > pause){ 
           //makes sure this block of code is only executed again after
           //a new motion sequence has been detected
           lockLow = true;                       
           Serial.print("motion ended at ");      //output
           Serial.print((millis() - pause)/1000);
           Serial.println(" sec");
           delay(50);
           }
       }

    if(valid == true)
    {
      sendData();
      

      //The following variable r will receive whether the magnet should be armed or unarmed. 
      while(1){
      String r = receiveData();
      
      //If magnet is armed, that means the user knows that a theft has occured. In that cause
      //continue to receive data to unarm

      //If unarm is selected, that means there is no theft. Then when the next theft occurs, Arduino 
      //should send the notification on theft. Therefore, if unarm is selected, exit the loop.

      if(r =="Unarmed")
      {
       break;
      }
      
    }
    }
    valid = false;
       
  }

  void sendData()
  {  

     mySerial.begin(9600);

     //This is where data is being sent
     mySerial.write("Theft");

     mySerial.end();
       
       
  }

  String appendString(String str)
 {
   string+= str;
   Serial.println(string);
   return string;
 }

 String receiveData()
{

  while(1){
  
    mySerial.begin(9600);
  
    String data = "";

    String returnValue = "";
  
    while(mySerial.available()==0){}

    char character;
      
    if (mySerial.available())
    {
      character = mySerial.read();

      data = appendString(String(character));
   
    }

    Serial.println(data);

    if(data == "Arm")
    {
      Serial.println("Preparing to arm the magnet");

      //Arm Magnet

      digitalWrite(RELAY1,0);   // digitalWrite(RELAY1,LOW);       
   
      Serial.println("Magnet armed"); 

      mySerial.end();

      string = "";

      returnValue = "Armed";
 
    } 

    if(data == "Disarm")
    {
       Serial.println("Preparing to disarm the magnet");

      //Disarm Magnet

      digitalWrite(RELAY1,1);   // digitalWrite(RELAY1,HIGH);       
   
      Serial.println("Magnet disarmed"); 

      mySerial.end();

      string = "";

      returnValue = "Unarmed";
          
   } 

    return returnValue;
  }

}

