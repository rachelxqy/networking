

import java.net.*;
import java.nio.*;
public class DecapsulationACK
{

   private byte[] fragment=new byte[65525];
  
   private int dataOrAck; // data=0, ack=1;
   private int pOrnACK;//negtive=0, positive=1:
   private int fileType; //txt=0, PDF=1; 
   

   private int sequenceNumber;  
   
   private byte checksum ;
  
   private byte[] filename=new byte[60];   

   
   
  //constructor
   public DecapsulationACK (byte[] fragment1)
   {
      this.fragment=fragment1;    
   }
   
  //==============================================================================================

  //get DataOrAck, transferType, fileType
   public int getDataOrAck()
   {
      dataOrAck=fragment[0]>>6;
      return dataOrAck;
   }
   
   public int getPOrNACK()
   {
      pOrnACK=(fragment[0]>>4)&0x03;
      return pOrnACK;
   }
   
   public int getFileType()
   {
      fileType=fragment[0]&0x0F;
      return fileType;
   }
   

  
  //get sequence number 
   public int getSeqNumber()
   {
      byte[] s1=new byte[4];
      s1[0]=fragment[1];
      s1[1]=fragment[2];
      s1[2]=fragment[3];
      s1[3]=fragment[4];
      sequenceNumber= byteToInt(s1);
      
      return sequenceNumber;
   } 
   


   
   
   //****
   //get file name
   public String getFileName()
   {
      int k=0;
      for(int i=59;i>=0;i--)
      {
         if(fragment[5+i]==(byte)0x00)
            k++;
      }
            
      byte[] name=new byte[60-k];
      for(int m=0;m<(60-k);m++)
         name[m]=fragment[m+5];
      String fName=new String(name);
      
      return fName;
   }
   


   
    //==============================================================================================
   
   //value type change method
   private int byteToInt(byte[] b)
   {  
   
      int mask=0xff;  
      int temp=0;  
      int n=0;  
      for(int i=0;i<4;i++)
      {  
         n<<=8;  
         temp=b[i]&mask;  
         n|=temp;  
      }  
      return n;  
   }
   
   private double byteToDouble(byte[] byteArray)
   {
      long bits = 0L;
      int k=7;
      for (int i = 0; i<8; i++) 
      {
         bits |= (byteArray[i] & 0xFFL) << (8 * k);
         k--;
      }
      return Double.longBitsToDouble(bits);
   }
    
   


    

      
   
   
}//the end of class