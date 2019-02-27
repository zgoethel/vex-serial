#include <SoftwareSerial.h>

SoftwareSerial cortex(8, 7);

void setup()
{
  Serial.begin(38400);
  cortex.begin(38400);
}

void loop()
{
  while (Serial.available())
  {
    byte rx = Serial.read();
    cortex.write(rx);
  }

  while (cortex.available())
  {
    byte rx = cortex.read();
    Serial.write(rx);
  }
}
