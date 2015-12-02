package adNetworking;



//import java.net.*;
import java.nio.*;

import utils.Helper;
public class Encapsulation
{
   private int dataOrAck; // data=0, ack=1;
   private int transferType;//download=0 (client to server), upLoad=1 (server to client):
   private int fileType; //txt=0, PDF=1; 
   
   private int totalDataLength; //after convert to byte, need byte[4]
   private int sequenceNumber;  //after convert to byte, need byte[4] 
   
   //private byte checksum = (byte)0x00;//after checksum, replace checksum result to this byte
   
   private int rate;//after convert to byte, need byte[4]
   private double timeout;//after convert to byte, need byte[4]
   
   private byte[] filename=new byte[60];//filename is 60 byte long   
   private byte[] data;
   private static int byteleng = Helper.bytelength;
   
   //constructor
   public Encapsulation (int dataOrAck, int transferType, int fileType,
                         int totalDataLength,int sequenceNumber, 
                         int rate,double timeout,
                         byte[]filename,
                         byte[]data)
   {
      if(dataOrAck==0||dataOrAck==1)   
         this.dataOrAck=dataOrAck;
      
      if(transferType==0||transferType==1)
         this.transferType=transferType;
         
      if(fileType==0||fileType==1)
         this.fileType=fileType;
       
      this.totalDataLength=totalDataLength;
      this.sequenceNumber=sequenceNumber;   
      
      this.rate=rate;
      this.timeout=timeout;
      
      for(int j=0;j<filename.length;j++)
         this.filename[j]=filename[j];
   
      this.data=data;    
   }


  //++++++++++++++++++++++++++++++++++++++++++++++++++++++=
   public byte[] getFragment()
   {
      //total fragment long
      byte[] fragment=new byte[byteleng];
   
      
      //fragment[1]: include data/ACk, transferType,fileType
      byte dOrA =(byte)dataOrAck;
      dOrA=(byte)(dOrA<<6);
      byte tType=(byte)transferType;
      tType=(byte)(tType<<4);
      byte fType=(byte)fileType;
      
      fragment[0]=(byte)((dOrA^tType)^fType);
      //convert totalDataLength to byte[4]
      byte convertLength[]=getByteArrayFromInteger(totalDataLength);
      fragment[1]=convertLength[0];
      fragment[2]=convertLength[1];
      fragment[3]=convertLength[2];
      fragment[4]=convertLength[3];
    
      
      //convert sequenceNumber to byte[4]
      byte convertSeqNo[]=getByteArrayFromInteger(sequenceNumber);
      fragment[5]=convertSeqNo[0];
      fragment[6]=convertSeqNo[1];
      fragment[7]=convertSeqNo[2];
      fragment[8]=convertSeqNo[3];
      
   
      //convert rate to byte[4]
      byte convertRate[]=getByteArrayFromInteger(rate);
      fragment[9]=convertRate[0];
      fragment[10]=convertRate[1];
      fragment[11]=convertRate[2];
      fragment[12]=convertRate[3];
      
      
      //convert timeout to byte[8]
      byte convertTimeout[]=getByteArrayFromDouble(timeout);
      fragment[13]=convertTimeout[0];
      fragment[14]=convertTimeout[1];
      fragment[15]=convertTimeout[2];
      fragment[16]=convertTimeout[3];
      fragment[17]=convertTimeout[4];
      fragment[18]=convertTimeout[5];
      fragment[19]=convertTimeout[6];
      fragment[20]=convertTimeout[7];
         
      /*
        *filename is 60 byte long.
        first filename fragment is fragment[21];
        last filename fragment is fragment[80];
      */
      for(int p=0;p<filename.length;p++)
         fragment[p+21]=filename[p];
      /*
        *total fragment is byteleng.
        *total head fragment is 82.
        *data fragment is byteleng-82= 65443 byte.(This case means all data fragments are used)
        first data fragment is fragment[81];
        last data fragment is fragment[65523];
      */
      for(int i=0;i<data.length;i++)
         fragment[i+Helper.headlength-1]=data[i];
      //fragment[65506] store checksum;
      CRC8 crc8 = new CRC8();
      byte crc= crc8.checksum(fragment);
      fragment[byteleng-1]=crc;
      //System.out.println("fragment[65506]: "+Integer.toBinaryString(fragment[65506]&0x000000ff));
      return fragment;
   }
   
   
   
    //==============================================================================================
   private byte[] getByteArrayFromInteger(int intValue) 
   {
      ByteBuffer wrapped = ByteBuffer.allocate(4);
      wrapped.putInt(intValue);
      byte[] byteArray = wrapped.array();
      return byteArray;
   
   }
   
   private byte[] getByteArrayFromDouble(double doubleValue) 
   {
      byte[] byteArray = new byte[8];
      long lng = Double.doubleToLongBits(doubleValue);
      for(int i = 0; i < 8; i++) 
         byteArray[i] = (byte)((lng >> ((7 - i) * 8)) & 0xff);
   
      return byteArray;
   }

   
   
}//the end of class
