/*
	Arduino.VN - Điều khiển bật tắt 8 đèn led qua wifi, sử dụng esp8266 và Arduino
*/
#include <SerialCommand.h>

bool DEBUG = true; // Để true sẽ in ra kết quả của các lệnh AT với esp8266
byte timeOut = 60; // Timeout cho Serial
byte ledPin[] = {2, 3, 4, 5, 6, 7, 8, 9}; // Các chân led
SerialCommand sCmd;
void setup()
{
	Serial.begin(9600);
  	Serial3.begin(115200);
  	Serial3.setTimeout(timeOut);
  	// PinMode các chân led
  	for(byte i = 0; i <8; i++){
  		pinMode(ledPin[i], OUTPUT);
  	}
  	setupEep8266();
  	//sCmd.addCommand("DTG", changeStatus);
  	//sCmd.addCommand("GET", getStatus);
}

void loop()
{
	sCmd.readSerial();
}

void changeStatus(){
	char *arg;
	arg = sCmd.next();
	if(arg != NULL){
		int status = atoi(arg);
		int size = sizeof(ledPin);
		for(byte i = 0; i<size; i++){
			byte value = bitRead(status, i);
			digitalWrite(ledPin[i], value);
		}
	}
}
void getStatus(){
	int size = sizeof(ledPin);
	byte status = 0;
	for(byte i = 0; i<size; i++){
		byte value = digitalRead(ledPin[i]);
		status += value*pow(2,i);
	}
	sendMessenge("STATUS "+status);
}
void setupEep8266(){
	//sendData("AT+RESTORE",1000,DEBUG); // Khôi phục cài đặt
	//sendData("AT+RST",1000,DEBUG); // RESET ESP8266
	//sendData("AT+CWMODE=2",1000,DEBUG); // Chọn chế độ trạm phát wifi
	//sendData("AT+CWSAP=\"DuongTV\",\"khongcomang\",1,3",1000,DEBUG); Cài đặt cho mạng wifi
	sendData("AT+CIPMUX=1",1000,DEBUG); // Bật chế độ đa kết nối
	//sendData("AT+CIPSTA=\"192.168.4.193\"",1000,DEBUG); //Set ip tĩnh
	sendData("AT+CIFSR",1000,DEBUG); // Hiển thị ip
	sendData("AT+CIPSERVER=1,80",1000,DEBUG); // Khởi động Server ở port 80
}

void sendMessenge(String messenge){
  String cipSend = "AT+CIPSEND=0,";
  cipSend += messenge.length();
  sendData(cipSend,timeOut,false);
  sendData(messenge, timeOut, false);
}

String sendData(String command, const int timeout, boolean debug)
{
    String response = "";
    Serial3.println(command);
    long int time = millis();
    
    while( (time+timeout) > millis()){
      while(Serial3.available()){
        char c = Serial3.read();
        response+=c;
      }  
    }
    
    if(debug){
      Serial.print(response);
    }
    return response;
}