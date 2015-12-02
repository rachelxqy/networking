package adNetworking;

//Examples showing how to invoke CRC8.checksum()


import java.net.*;
import java.nio.*;

import utils.Helper;
public class CRC8Checksum 
{
	private byte[] fragment=new byte[Helper.bytelength];

	//constructor
	public CRC8Checksum(byte[] receiveFragement)
	{
		fragment=receiveFragement;
	}



	public boolean checkRight()
	{

		CRC8 crc8 = new CRC8();
		byte c1=crc8.checksum(fragment);

		int right=c1&0x000000ff;

		if(right==0)
			return true;
		else
			return false;

	}
}
