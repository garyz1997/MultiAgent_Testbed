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
    tagValue=str(opc[tagName])
    if tagValue=="True":
        tagValue="1"
    if tagValue=="False":
        tagValue="0"
    print(tagValue)
    opc.close()


