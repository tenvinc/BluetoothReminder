#include <SoftwareSerial.h>

SoftwareSerial mySerial(3, 4); // RX, TX

void setup() {
  // put your setup code here, to run once:
  mySerial.begin(9600);
  Serial.begin(9600);

  // Add your commands here
  sendCommand("AT");
  sendCommand("AT+ADVI0");
  sendCommand("AT+DELO2");
  sendCommand("AT+IBEA1");
  sendCommand("AT+IBE5587FA01");
  sendCommand("AT+MARJ0x0000");
  sendCommand("AT+MINO0x0000");
  sendCommand("AT+PWRM0");
  sendCommand("AT+ADTY3");
  sendCommand("AT+RESET");  
}

void sendCommand(const char * command){
  Serial.print("Command send :");
  Serial.println(command);
  mySerial.println(command);
  //wait some time
  delay(100);
  
  char reply[100];
  int i = 0;
  while (mySerial.available()) {
    reply[i] = mySerial.read();
    i += 1;
  }
  //end the string
  reply[i] = '\0';
  Serial.print(reply);
  Serial.println("Reply successful");
}

void loop() {
}
