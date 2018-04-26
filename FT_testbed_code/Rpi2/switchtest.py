import RPi.GPIO as GPIO
import time

GPIO.setmode(GPIO.BOARD)
SWITCH = 37
GPIO.setup(SWITCH, GPIO.IN)

print GPIO.input(SWITCH);


#GPIO.cleanup(); #adding this line give undefined behavior at end of program
GPIO.setwarnings(False);
