#include <SoftwareSerial.h>
int valTaken;

void setup() {
  Serial.begin(9600);
  pinMode(A0, INPUT);
}

void loop() {
  delay(200);
  int ValMax = 1;
   int ValMin = 0;
  valTaken = analogRead(A0);
  //Il sensore il pressione non é per niente accurato in opposizione a quanto inizialmente considerato. 
  //Riesce solo a distinguere se é applicata pressione o meno.
  //Questo é il motivo per cui mandiamo al raspberry o 0 o 1 senza fare particolari rimodulazione dei dati;
  if (valTaken >500){
    serial.write(char(ValMax));
  } else {
    serial.write(char(ValMin));
  }
}
