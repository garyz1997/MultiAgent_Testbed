import RPi.GPIO as GPIO
import time

GPIO.setmode(GPIO.BOARD)
SWITCH = 31
GPIO.setup(SWITCH, GPIO.IN)

#Send signal to pin 21
OUT = 36

#Activate conveyor motor
#OUT = 22

GPIO.setup(OUT,GPIO.OUT)
GPIO.output(OUT,True);
print "Output set to True"
time.sleep(3);
GPIO.output(OUT,False);
print "Output set to False"
print GPIO.input(SWITCH);


#GPIO.cleanup(); #adding this line give undefined behavior at end of program
GPIO.setwarnings(False);
