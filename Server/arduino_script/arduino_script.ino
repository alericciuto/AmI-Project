#include <SoftwareSerial.h>
int valTaken;

void setup() {
  Serial.begin(9600);
  pinMode(A0, INPUT);
}

void loop() {
  delay(100);
  valTaken = analogRead(A0);
  //Il sensore il pressione non é per niente accurato in opposizione a quanto inizialmente considerato. 
  //Riesce solo a distinguere se é applicata pressione o meno.
  //Questo é il motivo per cui mandiamo al raspberry o 0 o 1 senza fare particolari rimodulazione dei dat
  if (valTaken > 500){
    Serial.println(1);
  } else {
    Serial.println(0);
  }
  Serial.flush();
}
