import RPi.GPIO as GPIO
import time

GPIO.setmode(GPIO.BOARD)
SWITCH = 21
GPIO.setup(SWITCH, GPIO.IN)

#Send signal to pin 21 IX0.7
OUT = 26#QX0.6

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
