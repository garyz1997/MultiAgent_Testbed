import sys
import OpenOPC

if len(sys.argv) == 1:
    print('Error: No arguments given')
if len(sys.argv) > 2:
    print('Error: More than one argument')
else:
    tagName=str(sys.argv[1])
    opc=OpenOPC.client()
    opc.connect('Kepware.KEPServerEX.V6')
    print(opc[tagName])
    opc.close()


