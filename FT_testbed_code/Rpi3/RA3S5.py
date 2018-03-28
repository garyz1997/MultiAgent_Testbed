import RPi.GPIO as GPIO
import time

GPIO.setmode(GPIO.BOARD)
SWITCH = 29#IX1.1
GPIO.setup(SWITCH, GPIO.IN)

#Send signal to pin 21 IX1.1
OUT = 32#QX0.7

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
