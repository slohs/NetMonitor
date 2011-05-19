import serial
import array
import datetime
import platform
import sys
import time
import atexit

#
# constant region
#

# maximum packet size transmitted by usb
MAX_USB_SIZE = 32

#usb marker
USB_MARKER = 0xFF

#position of command byte in usb packet
USB_CMD_POS = 1

#position of length byte in usb packet
USB_SIZE_POS = 2

#position of info bytes in usb packet
CUSTOM_USB_INFO_POS = 3

#first data byte in usb packet
CUSTOM_USB_FIRST_DATA_POS = 4

#usb overhead
PACKET_OVERHEAD_BYTES = 4

# information about the data transported via usb
CUSTOM_USB_INFO_BYTES = 1

#the real payload of usb
CUSTOM_USB_PAYLOAD = MAX_USB_SIZE - PACKET_OVERHEAD_BYTES


#flags in info byte
LASTPACKET = 0
SINGLEPACKET = 1
ADDITIONALPACKET = 2

done = 0

NODEIDRANGEMIN = 0;
NODEIDRANGEMAX = 50;
RSSI_offset = 0;
accel_offset = 0;

round = 0;

#prev_x = 0
#prev_y = 0
#prev_z = 0


#
# global function region
#
def startAccessPoint():
    return array.array('B', [USB_MARKER, 0x07, 0x03]).tostring()# startMarker, Command, PacketSize

def stopAccessPoint():
    return array.array('B', [USB_MARKER, 0x09, 0x03]).tostring()

def switchChannel(channel):
    return array.array('B', [USB_MARKER, 0x074, 0x04, channel]).tostring()# startMarker, Command, PacketSize, channelNo

def startTX():
    return array.array('B', [USB_MARKER, 0x75, 0x03]).tostring()# startMarker, Command, PacketSize

def stopTX():
    return array.array('B', [USB_MARKER, 0x76, 0x03]).tostring()# startMarker, Command, PacketSize
    
def changeTXPower(power):
    return array.array('B', [USB_MARKER, 0x77, 0x04, power]).tostring()# startMarker, Command, PacketSize, power

def sendData(data):
    x = array.array('B', [USB_MARKER, 0x78, 0x03 + len(data)])# startMarker, Command, PacketSize, data (n bytes)
    for i in range(len(data)):
        x.append(data[i])
#    i = 0
#    y = x.tostring()
#    for i in range(len(y)):
#        print (y[i], ", ")
    return x.tostring()


def startOwnProtocol():
    return array.array('B', [0xFF, 0x7A, 0x03]).tostring()# startMarker, Command, PacketSize, 

def stopOwnProtocol():
    return array.array('B', [0xFF, 0x7B, 0x03]).tostring()# startMarker, Command, PacketSize, 

def getData():
    return array.array('B', [0xFF, 0x79, 0x03]).tostring()# startMarker, Command, PacketSize, 


#
# some special function to interpret usb packet
#
def getCommand(data):
    return data[USB_CMD_POS]

def getSize(data):
    return data[USB_SIZE_POS]

def getInfoByte(data):
    return data[CUSTOM_USB_INFO_POS]

def getFirstDataByte(data):
    return data[CUSTOM_USB_FIRST_DATA_POS]

def printData(start, length, data):
    for i in range(start, start + length):
        print("byte: ", i, "; data: ", data[i])
        
def writeData(start, length, data, fileP):
    for i in range(start, start + length):
        fileP.write("byte: " + str(i) + "; data: "+ str(data[i]) + "\n")      
       
# @param dstPos: start position in source array 
# @param srcPos: start position in source array
# @param len: amount of entries to copy
# @param dst: destination array
# @param src: source array
def copyArray(dstPos, srcPos, len, dst, src):
    for i in range (0, len):
        dst[i + dstPos] = src[i + srcPos]



def checkCommand(text, ser):
    time.sleep(0.01)
#    erg = ser.readall()
    erg = ser.read(100)
    if len(erg) >= 2:
        if erg[1] == 0x06:
            print (text, " ok: ", len(erg))
        else:
            print (text, " failed", "error code: ", erg[1])
    else:
        print (text, " failed", "length: ", len(erg))

def destPort(ser):
    global done
    ser.write(startAccessPoint())
    tester = ser.read(3)  
    print ("tester: ", tester)
    if len(tester)  == 0 :
        done = 0
    else:
        done = 1

def cleanup():
    print("called")


#transforms the RSSI register value to RSSI dbm value
def transformRSSI(RSSI):
    global RSSI_offset;
    
    if (RSSI >= 128):
        RSSI = ( RSSI - 256 ) / 2 - RSSI_offset;
    elif (RSSI < 128):
        RSSI =  RSSI / 2 - RSSI_offset;

    return RSSI;

def transformACCEL(accel_value):
    global accel_offset;
    
    if (( accel_value & 0x8000 ) != 0):
        accel_value = 0xFFFF - accel_value - 1 ;
        accel_mg = accel_value * (-1) - accel_offset;
    else:
        accel_mg = accel_value - accel_offset;
        
    return accel_mg;

def readoutDeliveryNormalPacket(data):
    global path;
    global NODEIDRANGEMIN;
    global NODEIDRANGEMAX;
    global round;
    
    print ("==== Data from Sink: Normal ==== ", data[4], "Bytes")
    
    if (getSize(data) != 28):
        print("**************** ERROR: package length **********************");
        return;
    
    parent = data[14];
    level = ( data[16] << 8 ) + data[15];
    sourceId = data[17];
    
    # possiblity check of data
    if (( sourceId < NODEIDRANGEMIN ) or ( sourceId > NODEIDRANGEMAX ) or ( parent < NODEIDRANGEMIN ) or (( parent > NODEIDRANGEMAX ) and ( parent != 255 ))):
        print ("******************** ERROR: node Ids ***********************");
        return;
    if (( level < 0 ) or ( level > 15 )):
        print ("******************** ERROR: level **************************");
        return;
    
    temperature = ( data[19] << 8 ) + data[18];
#    temp = (temperature - 2732)/10.0;
#    print("temperature=", temp, "°C");
    if (( temperature > 4750) or ( temperature < 2500 )):
        print ("******************** ERROR: temperature **************************");
        return;
    
    accel_x = ( data[21] << 8) + data[20];
    accel_x = transformACCEL(accel_x);
    
    accel_y = ( data[23] << 8) + data[22];
    accel_y = transformACCEL(accel_y);    
    
    accel_z = ( data[25] << 8) + data[24];
    accel_z = transformACCEL(accel_z);
    
    if (( accel_x < -2200 ) or ( accel_x > 2200 ) or ( accel_y < -2200 ) or ( accel_y > 2200 ) or ( accel_z < -2200 ) or ( accel_z > 2200 )):
        print ("******************** ERROR: acceleration values ************");
        return;

    # readout quality samples
    RSSI = transformRSSI( data[ getSize(data) - 2 ] );
    LQI = data[ getSize(data) - 1 ] & 0x7F;
    
    file = open(path, 'a')  
    file.write("CompleteData;")
    file.write(str( sourceId ))
    file.write(";")
    file.write(str( parent ))
    file.write(";")
    file.write(str( level ))
    file.write(";")
    #read data
    file.write(str( accel_x ))
    file.write(";")
    file.write(str( accel_y ))
    file.write(";")
    file.write(str( accel_z ))
    file.write(";")
    file.write(str( temperature )) 
    file.write(";")
    file.write(str( RSSI )) # RSSI value
    file.write(";")
    file.write(str( LQI )) # link quality
    file.write(";")
    file.write(str( round )) # round timeStamp
    file.write("\n")
    file.close()
    
    round = round + 1;
    
#    print(" node:", sourceId, " (", level, ") --> ", parent)
#    print(" accel: ", accel_x, ", ", accel_y, ", ", accel_z)
#    print(" temp: ", temperature);

def readoutDeliveryShortPacket(data):
    global path
    
    print ("==== Data from Sink: Temp ==== ", data[4], "Bytes")
#    print ("length=", data[4], " tos=", data[5]);
#    print ("head=", data[6], " appDest=", data[7], " tos=", data[8], " length=", data[9], " nodeId", data[10]);
#    print ("dest=", data[11], " SendType=", data[12], " PacketType=", data[13]);
#    print ("+++++");
    
    parent = data[13];
    level = ( data[15] << 8 ) + data[14];
    sourceId = data[16];
    temperature = ( data[18] << 8 ) + data[17];
    
    file = open(path, 'a')  
    file.write("TempData;")
    file.write(str( sourceId)) # source ID
    file.write(";")
    file.write(str( parent )) # parent id
    file.write(";")
    file.write(str( level )) # level
    file.write(";")
    file.write(str( temperature )) # temp
    file.write("\n")
    file.close()
    
#    print(" node:", sourceId, " (", level, ") --> ", parent)
#    print(" temp: ", temperature);
    
def readoutDeliveryAlertPacket(data):
    global path
    
    print ("==== Data from Sink: ALERT==== ", data[4], "Bytes")

    packetType = data[12]; # flag for alert
    parent = data[13];
    level = ( data[15] << 8 ) + data[14];
    sourceId = data[16];
    temperature = ( data[18] << 8 ) + data[17];

    file = open(path, 'a')  
    file.write("AlertData;")
    file.write(str( soruceId )) # source ID
    file.write(";")
    file.write(str( parent )) # parent id
    file.write(";")
    file.write(str( level )) # level
    file.write(";")
    file.write(str( temperature )) # temp
    file.write("\n")
    file.close()
        
#    print(" node:", sourceId, " (", level, ") --> ", parent);
#    print(" temp: ", temperature);

def readoutNeighborStateMessage(data):
    global path;
    global NODEIDRANGEMIN;
    global NODEIDRANGEMAX;
    global round;
    
    print ("==== State Message ==== ", data[4], "Bytes")
    
    if (getSize(data) != 26):
        print ("******************** ERROR: package length *****************");
        return;

    # readout data    
    sourceId = data[9];
    parent = data[22];
    level = ( data[21] << 8 ) + data[20];
    
    # possiblity check of data
    if (( sourceId < NODEIDRANGEMIN ) or ( sourceId > NODEIDRANGEMAX ) or ( parent < NODEIDRANGEMIN ) or (( parent > NODEIDRANGEMAX ) and ( parent != 255 ))):
        print ("******************** ERROR: node Ids ***********************");
        return;
    if (( level < 0 ) or ( level > 15 )):
        print ("******************** ERROR: level **************************");
        return;
    
    # readout quality samples
    RSSI = transformRSSI( data[ getSize(data) - 2 ] );
    LQI = data[ getSize(data) - 1 ] & 0x7F;
    
    # write data to file    
    file = open(path, 'a')  
    file.write("StateData;")
    file.write(str( sourceId )) # source ID
    file.write(";")
    file.write(str( parent )) # parent id
    file.write(";")
    file.write(str( level )) # level
    file.write(";")
    file.write(str( RSSI )) # RSSI value
    file.write(";")
    file.write(str( LQI )) # link quality
    file.write(";")
    file.write(str( round )) # round timeStamp
    file.write("\n")
    file.close()
    
    round = round + 1;

def readoutNeighborDataMessage(data):
    global path;
    global NODEIDRANGEMIN;
    global NODEIDRANGEMAX;
    global round;
    
    print ("==== Neighbor Data Message ==== ", data[4], "Bytes", "from 18..", getSize(data) - 2);

    if (getSize(data) < 18):
        print ("********* ERROR: package length ****************************");
        return;
    
    # readout state data
    parent = data[14];
    level = ( data[16] << 8 ) + data[15];
    sourceId = data[17];
    
    # possiblity check of data
    if (( sourceId < NODEIDRANGEMIN ) or ( sourceId > NODEIDRANGEMAX ) or ( parent < NODEIDRANGEMIN ) or (( parent > NODEIDRANGEMAX ) and ( parent != 255 ))):
        print ("******************** ERROR: node Ids ***********************");
        return;
    if (( level < 0 ) or ( level > 15 )):
        print ("******************** ERROR: level **************************");
        return;
    
    # readout quality samples
    RSSI = transformRSSI( data[ getSize(data) - 2 ] );
    LQI = data[ getSize(data) - 1 ] & 0x7F;
    
    file = open(path, 'a')  
    file.write("NeighborData;")
    file.write(str( sourceId )) # source ID
    file.write(";")
    file.write(str( parent )) # parent id
    file.write(";")
    file.write(str( level )) # level
    file.write(";")
    file.write(str( RSSI )) # RSSI value
    file.write(";")
    file.write(str( LQI )) # link quality
    file.write(";")
    file.write(str( round )) # round timeStamp
    
    round = round + 1;
    
    length = getSize(data) - 2;
    pos = 18;
        
    while ( pos < length ):    
        neighborId = data[pos];
        
        file.write(";");
        file.write(str( neighborId ));        
        
        pos = pos + 1;
        
    file.write("\n")
    file.close()

def start_main(count):
    global done;
##start device communication            
    if platform.system() == "Linux":
        print ("try on Linux-System")
        try:
            ser = serial.Serial("/dev/ttyACM0",115200,timeout=0.0025)
#            done = 1
            destPort(ser)
        except serial.serialutil.SerialException:
            pass
        if done == 0:
            try:
                ser = serial.Serial("/dev/ttyACM1",115200,timeout=0.0025)
                #done = 1
                destPort(ser)
            except serial.serialutil.SerialException:
                pass  
        if done == 0:
            try:
                ser = serial.Serial("/dev/ttyACM2",115200,timeout=0.0025)
                #done = 1
                destPort(ser)
            except serial.serialutil.SerialException:
                pass
        if done == 0:
            try:
                ser = serial.Serial("/dev/ttyACM3",115200,timeout=0.0025)
                #done = 1
                destPort(ser)
            except serial.serialutil.SerialException:
                pass  
    
    #Start access point
    if done == 0:
        print ("connection was not successful")
        #file.write("connection was not successful\n")
        return count
    else:
        print ("successfully connected to access point")
        #file.write("connection was not successful\n")
        ser.write(startOwnProtocol())
        checkCommand("startOwnProtocol", ser)
#        ser.write(switchChannel(2))
        print ("switched to channel 2")
       # ser.write(switchChannel(1))
        time.sleep(2)
        data_buffer = []
        for i in range(256):
            data_buffer.append(0)
    
        pos = 0
        addPacket = 0
        while (1==1):
#        for i in range(count):
            try:       
                time.sleep(0.010)
                ser.write(getData())
                data = ser.read(100)
                if len(data) > 4:
                    info = getInfoByte(data)
                    if(info == 0):      # last packet
                        copyArray(pos, 0 + CUSTOM_USB_FIRST_DATA_POS, getSize(data) - PACKET_OVERHEAD_BYTES, data_buffer, data)
                        pos = pos + (getSize(data) - PACKET_OVERHEAD_BYTES)
                        pos = 0
                        addPacket = 0
                    elif(info == 1):    # single packet
                        # if message Type is only Application message
                        if ( ( data[ getSize(data) - 1 ] & 0xF0 ) >= 1 ): # check the crc flag
                            if ( (data[6] == 4) and ( getSize(data) >= 13 ) ):
                                if (( data[12] == 2 ) or ( data[12] == 4 )):
                                    print("data ist DATA or FORWARD ---> ignore throw away.");
                                else:
                                    # run Evaluation
                                    if ( data[13] == 1 ):
                                        readoutDeliveryNormalPacket(data);
                                    elif ( data[13] == 2 ):
                                        print("temp data");
                                    elif ( data[13] == 4 ):
                                        print("alert data");
                                    elif ( data[13] == 16):
                                        readoutNeighborDataMessage(data);
                                    else:
                                        print("unknown data");
                                        print("length=", data[4], " tos=", data[5]);
                                        print("Head=", data[6], " appDest=", data[7], " tos=", data[8], " length=", data[9], " sender=", data[10]);
                                        print("Dest=", data[11], "SendType=", data[12], "PacketType=", data[13]);
                            #if message is a State and Neighbor Message
                            elif (( data[6] == 3 ) and ( getSize(data) >= 13 )):
                                readoutNeighborStateMessage(data);
                            else:
                                print("INVALID PACKET: ", getSize(data), "B   RSSI=", transformRSSI(data[getSize(data) - 2]), "   LQI=", data[getSize(data) - 1] & 0x7F );
                        else:
                            print("################ CORRUPT CRC PACKET ----> throw Package away ####################")
                                                
                    elif(info == 2):    # additional packet
                        #print("additional packet")
#                        file.write("additional packet\n")
                        copyArray(pos, 0 + CUSTOM_USB_FIRST_DATA_POS, getSize(data) - PACKET_OVERHEAD_BYTES, data_buffer, data)                
                        pos = pos + (getSize(data) - PACKET_OVERHEAD_BYTES)
                        addPacket = 1
                    else:
                        #print("unknown info byte - read next USB packet")
#                        file.write("unknown info byte - read next USB packet\n")
                        continue
#                else:
 #                   print("\tno payload received")
#                    file.write("\tno payload received\n")
                #print("\n")
#                file.write("\n")
            except serial.serialutil.SerialException:
                done = 0
                time.sleep(1)
                ser.close()
                return count - i
        ser.read(100)
        ser.close()
    return 0
  
#
# the script starts here
#

#path="/home/slohs/work/realworlddata/data.dat"
path=str(sys.argv[1])

file = open(path, 'w')
file.close()

failedConnection = 0
ret = 1000000
for i in range(1000000):
    ret = start_main(ret)
    if ret != 0:
        failedConnection = failedConnection + 1
#        print("###############################")
#        print("Connection timeout after ", ret," read cycles -> try again no: ", failedConnection)
#        print("###############################")
        file.write("###############################\n")
        file.write("Connection timeout after " +str(ret)+" read cycles -> try again no: "+ str(failedConnection)+"\n")
        file.write("###############################\n")
        time.sleep(2)
    else:
#        print("###############################")
#        print("reading was successful; restarted connections: ", failedConnection)
#        print("###############################")
        file.write("###############################\n")
        file.write("reading was successful; restarted connections: " + str(failedConnection)+"\n")
        file.write("###############################\n")
        break
    
