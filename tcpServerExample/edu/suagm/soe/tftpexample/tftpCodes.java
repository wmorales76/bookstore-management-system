package edu.suagm.soe.tftpexample;

public class tftpCodes {

	//Buffer size
	public static final int BUFFER_SIZE=1024;
	
	
	//Data transfer codes--------------------------------------------
	//OK 
	public static final int OK=1;
	
	//Get
	public static final int GET=2;
	
	//Put
	public static final int PUT=3;
	
	//Close connection
	public static final int CLOSECONNECTION=4;
	
	
	//Error messages----------------------------------------------------
	//File not found
	public static final int FILENOTFOUND=20;
	
	//The intended file already exists
	public static final int EXISTINGFILE=21;
	
	//No valid command
	public static final int WRONGCOMMAND=30;
	
}
