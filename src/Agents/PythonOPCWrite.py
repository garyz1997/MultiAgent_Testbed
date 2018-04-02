import sys
import OpenOPC

if len(sys.argv) == 1:
    print('Error: No arguments given')
if len(sys.argv) == 2:
    print('Error: Only one argument given. Need tag name and value')
else:
    tagName=str(sys.argv[1])
    value=str(sys.argv[2])
    opc=OpenOPC.client()
    opc.connect('Kepware.KEPServerEX.V6')
    opc[tagName]=value
    
