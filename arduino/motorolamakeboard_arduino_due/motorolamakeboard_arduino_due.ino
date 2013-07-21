/* Ping))) Sensor
  
   This sketch reads a PING))) ultrasonic rangefinder and returns the
   distance to the closest object in range. To do this, it sends a pulse
   to the sensor to initiate a reading, then listens for a pulse 
   to return.  The length of the returning pulse is proportional to 
   the distance of the object from the sensor.
     
   The circuit:
	* +V connection of the PING))) attached to +5V
	* GND connection of the PING))) attached to groundrunssssssss
	* SIG connection of the PING))) attached to digital pin 7

   http://www.arduino.cc/en/Tutorial/Ping
   
   created 3 Nov 2008
   by David A. Mellis
   modified 30 Aug 2011
   by Tom Igoe
 
   This example code is in the public domain.

 */

// this constant won't change.  It's the pin number
// of the sensor's output:
const int pingPin = 7;
const int syncpin= 22;
const int startbutton = 26; 
//const int stopbutton = 27;
int systemstatus = 0; // 0 idle, 1 start and running, 2 stop sequence
int runcount = 0;

void setup() {
    pinMode(syncpin, OUTPUT);
    digitalWrite(syncpin, LOW);
    pinMode(startbutton, INPUT_PULLUP);
//    pinMode(stopbutton, INPUT);
  
  
  
  // initialize serial communication:
  
  Serial.begin(9600);
}

void loop()
{
  
  // establish variables for duration of the ping, 
  // and the distance result in inches and centimeters:
  long duration, inches, cm;


  if (systemstatus == 0)
  {
    runcount = 0; 
    if (digitalRead(startbutton)==0)
    {
      systemstatus = 1;
      //Start Operation
      Serial.print(-1.0);
      Serial.println();
      delay(100);
    }
    else
    {
      systemstatus = 0;
    }
  }
  else if (systemstatus == 2)
  {
       systemstatus = 0;
       runcount = 0; 
       Serial.print(-2.0);
       Serial.println();
       delay(100);
  }
  else if (systemstatus == 1)
  {

    
    //ROTATE SEQUENCE
    digitalWrite(syncpin, HIGH);
    int temptimer = millis();
    //  int totaltimer = 500 ;
    int temptimer2=0;
    delay(500);
    digitalWrite(syncpin, LOW);
    
    for (int i = 0; temptimer2-temptimer< 6000; i ++) {
            // The PING))) is triggered by a HIGH pulse of 2 or more microseconds.
        // Give a short LOW pulse beforehand to ensure a clean HIGH pulse:
        pinMode(pingPin, OUTPUT);
        digitalWrite(pingPin, LOW);
        delayMicroseconds(2);
        digitalWrite(pingPin, HIGH);
        delayMicroseconds(5);
        digitalWrite(pingPin, LOW);
      
        // The same pin is used to read the signal from the PING))): a HIGH
        // pulse whose duration is the time (in microseconds) from the sending
        // of the ping to the reception of its echo off of an object.
        pinMode(pingPin, INPUT);
        duration = pulseIn(pingPin, HIGH);
      //
  
         Serial.print(duration);
         Serial.println();
         delay(100);
         
         temptimer2= millis();
    }
            //send signal indicating height change.
        Serial.print(-3.0);
        Serial.println();
        delay(100);

        
    
    runcount = runcount +1;
    
    if (runcount >= 5)
    {
      systemstatus = 2;
    }


  }
  
  
 
  
}

long microsecondsToInches(long microseconds)
{
  // According to Parallax's datasheet for the PING))), there are
  // 73.746 microseconds per inch (i.e. sound travels at 1130 feet per
  // second).  This gives the distance travelled by the ping, outbound
  // and return, so we divide by 2 to get the distance of the obstacle.
  // See: http://www.parallax.com/dl/docs/prod/acc/28015-PING-v1.3.pdf
  return microseconds / 74.0 / 2.0;
}

long microsecondsToCentimeters(long microseconds)
{
  // The speed of sound is 340 m/s or 29 microseconds per centimeter.
  // The ping travels out and back, so to find the distance of the
  // object we take half of the distance travelled.
  return microseconds / 29.0 / 2.0;
}
