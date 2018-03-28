import RPi.GPIO as GPIO
import time

GPIO.setmode(GPIO.BOARD)
SWITCH = 31#IX1.2
GPIO.setup(SWITCH, GPIO.IN)

#Send signal to pin 2X
OUT = 36#QX1.0

#Activate conveyor motor

GPIO.setup(OUT,GPIO.OUT)
GPIO.output(OUT,True);
print "Output set to True"
time.sleep(.1);
GPIO.output(OUT,False);
print "Output set to False"
print GPIO.input(SWITCH);


#GPIO.cleanup(); #adding this line give undefined behavior at end of program
GPIO.setwarnings(False);
