package edu.suagm.tftpExmpleClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**************************************************************************
 * 
 * @author Idalides Vergara
 * CPEN 457 - Programming Languages 
 * This class is the Trivial FTP client
 * Fall 2017 
 *
 *************************************************************************/

public class tftpClient {

	//List of valid commmands on the protocol
	private enum tftpValidCommands	{
	    get, put, exit 
	}
	
	//Current command
	private		 int 				currentCommand;
	
	// Data stream and output streams for data transfer
	private 	DataInputStream 	socketInputStream;
	private 	DataOutputStream 	socketOutputStream;
	
	// Connection parameters
	private 	String 				serverAddressStr;
	private		int 				serverPort;
	
	
	public static void main(String args[])
	{
		//Extract port and IP address from the arguments 
		String serverAddressStr=args[0];
        int serverPort=(new Integer(args[1])).intValue();
        System.out.println("Connecting to " + serverAddressStr + " Through "+serverPort);
        
        //Create the client object
        tftpClient clientObj= new tftpClient(serverAddressStr, serverPort);
        
        //Perform data transfer
        clientObj.dataTransfer();
        
	}
	
	/****************************************************************************
	 * 
	 * Constructor
	 * @param serverAddressStr: Server IP Address in canonical representation W.X.Y.Z
	 * @param serverPort: Server Port
	 * 
	 *****************************************************************************/
	public tftpClient(String serverAddressStr, int serverPort)
	{
		this.serverAddressStr=serverAddressStr;
		this.serverPort = serverPort;
	}
	
	/*****************************************************************************
     * Data transfer method
     * Tasks: Manage connection with the Server.
     * 		Receive commands from the keyboard thru a Scanner object
     * 		Invoke the appropriate method depending on the received command
     * 
     *****************************************************************************/
	private void dataTransfer()
	{
		String arguments="";
        
        //Scanner for reading commands from the keyboard 
        Scanner reader = new Scanner(System.in);
        
		try {
	        
	        //Converting canonical IP address into InetAddress
	        InetAddress  serverAddress= InetAddress.getByName(serverAddressStr);
	        
	        //Create a client socket and connect to the Server
	        Socket socket = new Socket(serverAddress, serverPort);
	        
	        //Create the Data Stream for data transfer
	        socketInputStream =  new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	        socketOutputStream =  new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	        System.out.println("Connected to the server");
	        
	        do{
	        	//Read next command
	        	arguments=readCommands(reader);
	        	
	        	switch(currentCommand)
	        	{
	        		//Get command
	        		case tftpCodes.GET: getCommand(arguments);
	        							break;
	        							
	        		//Put command
	        		case tftpCodes.PUT: putCommand(arguments);
										break;
										
					//Exit command
	        		case tftpCodes.CLOSECONNECTION:
	        							exitCommand();
	        							break;
	        		
	        	    //Wrong command
	        		case tftpCodes.WRONGCOMMAND:  
	        							System.out.println(arguments + " is not a valid command");
	        							break;
	        	}
	        	
	        }while(currentCommand != tftpCodes.CLOSECONNECTION);
	        
	        //Close scanner
	        reader.close();
			reader=null;
			
	        //Close socket and connections
	        socketOutputStream.close();
	        socketInputStream.close();
		    socket.close();
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/***************************************************************************
	 * exitCommand Method
	 * Tasks: Sends the CLOSECONNECTION to the server for closing the connection
	 * 		 Waits for OK confirmation code from the server
	 * 
	 **************************************************************************/
	private void exitCommand()
	{
		try{
			//Send CloseConnection command
			socketOutputStream.writeInt(tftpCodes.CLOSECONNECTION);
			socketOutputStream.flush();
			
			//Wait for OK code
	        int read=socketInputStream.readInt();
	        System.out.println("Goodbye!");
	        
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/***************************************************************************
	 * getCommand Method
	 * Tasks: Performs get command
	 * 		  Sends the GET code to the server 
	 * 		  Waits for OK confirmation code from the server
	 * 		  Sends the file name to the server
	 * 		  Waits for OK confirmation code from the server
	 * 				if received, transfers the file to the server
	 * 				if FILENOTFOUND error, shows it to the user  
	 * 
	 **************************************************************************/
	private void getCommand(String fileName)
	{
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
	     
		 try{
			 int read;
			 long totalRead=0;
			 
	        //Send the get command
	        System.out.println("Sending command: GET");
	        socketOutputStream.writeInt(tftpCodes.GET);
	        socketOutputStream.flush();
	        System.out.println("Sent command: " + tftpCodes.GET);
	        
	        //Wait for OK code
	        read=socketInputStream.readInt();
	        
	        if (read==tftpCodes.OK)
	        {
	        	//Send the file name
	        	System.out.println("Sending the file name");
	        	socketOutputStream.write(fileName.getBytes());
		        socketOutputStream.flush();

		        //Wait for OK code
		        System.out.println("Waiting for confirmation");
		        read=socketInputStream.readInt();
		        
		        //Save current time for computing transmission time
	        	long startTime = System.currentTimeMillis();
	        	
	        	/*********************************************************
		       		Write here your code for data transfer
		        **********************************************************/
	        	
	        	long endTime = System.currentTimeMillis();
		        System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");
	        	System.out.println("Successful Data transfer");
	        }
		 }catch(Exception e)
	        {
	        	e.printStackTrace();
	        	
	        }
	}
	
	/***************************************************************************
	 * putCommand Method
	 * Tasks: Performs put command
	 * 		  Sends the PUT code to the server 
	 * 		  Waits for OK confirmation code from the server
	 * 		  Sends the file name to the server
	 * 		  Waits for OK confirmation code from the server
	 * 				if received, transfers the file to the server
	 * 				if EXISTINGFILE error, shows it to the user  
	 * 
	 **************************************************************************/
	private void putCommand(String fileName)
	{
	     
	     byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
	     
		 try{
			 int read;
			 long totalRead=0;
			 
	        //Send the get command
	        System.out.println("Sending command: PUT");
	        socketOutputStream.writeInt(tftpCodes.PUT);
	        socketOutputStream.flush();
	        System.out.println("Sent command: " + tftpCodes.PUT);
	        
	        //Wait for OK code
	        read=socketInputStream.readInt();
	        
	        if (read==tftpCodes.OK)
	        {
	        	//Send the file name
	        	System.out.println("Sending the file name");
	        	socketOutputStream.write(fileName.getBytes());
		        socketOutputStream.flush();

		        //Wait for OK code
		        System.out.println("Waiting for confirmation");
		        read=socketInputStream.readInt();
		        
		      //Save current time for computing transmission time
	        	long startTime = System.currentTimeMillis();
	        	
	        	/*********************************************************
		       		Write here your code for data transfer
		        **********************************************************/
	        	
	        	long endTime = System.currentTimeMillis();
		        System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");
	        	System.out.println("Successful Data transfer");
	        }
		 }catch(Exception e)
	        {
	        	e.printStackTrace();
	        	
	        }
	}
	
	private String readCommands(Scanner reader)
	{
		//Print the prompt  
		System.out.print(">");
		
		//Linked list for storing tokens in the sentence
		LinkedList<String> commandList = new LinkedList<String>();
		
		//Read the next command
		String sText = reader.nextLine();
		String results="";
		
		//Parse the sentence
		StringTokenizer st = new StringTokenizer(sText);
		while (st.hasMoreTokens()) {
	         commandList.add(st.nextToken());
	     }
		
		//Verify the command
		try{
			switch (tftpValidCommands.valueOf(commandList.get(0)))
			{
				//Get command
				case get: 	if (commandList.size()>1)
							{
								currentCommand=tftpCodes.GET;
								results=commandList.get(1);
							}else{
								currentCommand=tftpCodes.WRONGCOMMAND;
							}
						  	break;
						  	
				case put: 	if (commandList.size()>1)
							{
								currentCommand=tftpCodes.PUT;
								results=commandList.get(1);
							}else{
								currentCommand=tftpCodes.WRONGCOMMAND;
							}
						  	break;
				  		  	
				case exit: 	currentCommand=tftpCodes.CLOSECONNECTION;
							break;
				
				default: 	currentCommand=tftpCodes.WRONGCOMMAND;
							results=sText;
							break;
			}
			} catch(Exception e)
			{
				currentCommand=tftpCodes.WRONGCOMMAND;
				results=sText;
			} 
			
		return results;
	}
	
}
