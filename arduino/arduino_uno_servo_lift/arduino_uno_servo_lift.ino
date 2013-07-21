#include <Servo.h> 


Servo myservo;
int sensePin = 2;

void setup() 
{ 
  pinMode(sensePin, INPUT);
  myservo.attach(9); //set up the servo as usual
//  myservo.writeMicroseconds(1700); //for watching the speeds in the serial monitor
//myservo.write(90); 


  //attach external interrurpt
//  attachInterrupt(0, interruptsequnce, RISING);
} 

void loop() {

      if (digitalRead(sensePin))
      {
        //change from 2000->1000
        myservo.writeMicroseconds(1750);
        delay(500);
        myservo.writeMicroseconds(1500);
        delay(5000);
      }        
                              
     
} 

//interrupt got executed when the external pin goes from 0 - > 1
void interruptsequnce(){

  
  


}

