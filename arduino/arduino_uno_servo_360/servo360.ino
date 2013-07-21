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

// on this servo 1500 is stopped, above 1500 is clockwise, below is counter clockwise
//      myservo.writeMicroseconds(1500); 
//      for (int i=1500; i < 2000; i++)
//      {
//        myservo.writeMicroseconds(i);
//        delay(50);`
//      }
//      
//      while(1)
//      {
//              myservo.writeMicroseconds(2000);
//      }    

//           myservo.write(0);  
//          delay(5000);
//           myservo.write(90);  
//          delay(5000);        

      if (digitalRead(sensePin))
      {
        delay(500);
        myservo.writeM  icroseconds(1600);
        delay(5400);
        myservo.writeMicroseconds(1500);
        delay(100);
      }
      
     
} 

//interrupt got executed when the external pin goes from 0 - > 1
void interruptsequnce(){

  
  


}

