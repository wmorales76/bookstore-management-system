package edu.suagm.soe.tftpexample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**************************************************************************
 * 
 * @author Idalides Vergara
 *         CPEN 457 - Programming Languages
 *         This class is design for handling client connections to the
 *         Trivial FTP server
 *
 *************************************************************************/

public class tftpHandler extends Thread {
	// Data stream and output streams for data transfer
	private DataInputStream clientInputStream;
	private DataOutputStream clientOutputStream;

	// Client socket for maintaing connection with the client
	private Socket clientSocket;

	/***************************************************************************
	 * Constructor
	 * 
	 * @param clientSocket: client socket created when the client connects to
	 *                      the server
	 */

	public tftpHandler(Socket clientSocket) {
		try {
			this.clientSocket = clientSocket;
			clientInputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
			clientOutputStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * Run method
	 * Tasks: Calls the dataTransfer method
	 */
	@Override
	public void run() {
		dataTransfer();
	}

	/*****************************************************************************
	 * Data transfer method
	 * Tasks: Manage connection with the client.
	 * Receive commands from the client
	 * Invoke the appropriate method depending on the received command
	 * 
	 *****************************************************************************/
	public void dataTransfer() {
		try {

			int readCommand;

			do {
				// Wait for command
				System.out.println("waiting for a command");
				readCommand = clientInputStream.readInt();
				System.out.println("Received Command: " + readCommand);

				switch (readCommand) {

					case tftpCodes.ADD_GENRE:
						addGenreCommand();
						break;

					case tftpCodes.ADD_BOOK:
						addBookCommand();
						break;

					case tftpCodes.MODIFY_BOOK:
						modifyBookCommand();
						break;
					case tftpCodes.LIST_GENRES:
						listGenresCommand();
						break;

					case tftpCodes.LIST_BOOKS:
						listBooksCommand();
						break;
					case tftpCodes.LIST_BOOKS_BY_GENRE:
						listBooksByGenreCommand();
						break;

					case tftpCodes.SEARCH_BOOK:
						searchBookCommand();
						break;

					case tftpCodes.BUY_BOOK:
						buyBookCommand();
						break;

					case tftpCodes.FOUND:
						foundCommand();
						break;
					case tftpCodes.ALREADYEXISTS:
						alreadyExistsCommand();
						break;

					case tftpCodes.NOTFOUND:
						notFoundCommand();
						break;

					case tftpCodes.EMPTY:
						emptyCommand();
						break;

					// Exit command
					case tftpCodes.CLOSECONNECTION:
						exitCommand();
						break;

				}
			} while (readCommand != tftpCodes.CLOSECONNECTION);

			// Close connection and socket
			clientInputStream.close();
			clientOutputStream.close();
			clientSocket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addGenreCommand() {
		// TODO Auto-generated method stub

	}


	private void addBookCommand() {
		// TODO Auto-generated method stub

	}

	private void modifyBookCommand() {
		// TODO Auto-generated method stub

	}

	private void listGenresCommand() {
		// TODO Auto-generated method stub

	}

	private void listBooksCommand() {
		// TODO Auto-generated method stub

	}

	private void listBooksByGenreCommand() {
		// TODO Auto-generated method stub

	}

	private void searchBookCommand() {
		// TODO Auto-generated method stub

	}

	private void buyBookCommand() {
		// TODO Auto-generated method stub

	}

	private void foundCommand() {
		// TODO Auto-generated method stub

	}

	private void alreadyExistsCommand() {
		// TODO Auto-generated method stub

	}

	private void notFoundCommand() {
		// TODO Auto-generated method stub

	}

	private void emptyCommand() {
		// TODO Auto-generated method stub

	}

	/***************************************************************************
	 * exitCommand Method
	 * Tasks: Sends OK confirmation code to the client for closing connection
	 * 
	 **************************************************************************/

	private void exitCommand() {
		try {
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();
			System.out.println("The connection has been closed!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * getCommand Method
	 * Tasks: Performs get command
	 * Acknowledges to the client for the received command
	 * Receives the file name
	 * Acknowledges to the client for the received file name
	 * Checks for the correctness of the file name
	 * if not found, send the FILENOTFOUND error code the client
	 * if found, transfer the file to the client
	 * 
	 **************************************************************************/
	private void getCommand() {
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		int totalRead = 0;

		System.out.println("Get Command");

		try {
			int read;

			// Write the OK code: make acknowledgment of GET command
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();

			// Wait for file name
			System.out.println("Waiting for the file name");
			read = clientInputStream.read(buffer);

			// print the file name
			System.out.println("get " + (new String(buffer)));

			// Send confirmation to the client
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();

			// Save current time for computing transmission time
			long startTime = System.currentTimeMillis();

			/*********************************************************
			 * Write here your code for data transfer
			 **********************************************************/

			long endTime = System.currentTimeMillis();
			System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");
			System.out.println("Successful Data transfer");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * putCommand Method
	 * Tasks: Performs put command
	 * Acknowledges to the client for the received command
	 * Receives the file name
	 * Acknowledges to the client for the received file name
	 * Checks for the correctness of the file name
	 * if found, send the EXISTINGFILE error code the client
	 * if found, receive file from the client
	 * 
	 **************************************************************************/
	private void putCommand() {
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		int totalRead = 0;

		System.out.println("Put Command");

		try {
			int read;

			// Write the OK code: make acknowledgment of PUT command
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();

			// Wait for file name
			System.out.println("Waiting for the file name");
			read = clientInputStream.read(buffer);

			// print the file name
			System.out.println("put " + (new String(buffer)));

			// Send confirmation to the client
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();

			// Save current time for computing transmission time
			long startTime = System.currentTimeMillis();

			/*********************************************************
			 * Write here your code for data transfer
			 **********************************************************/

			long endTime = System.currentTimeMillis();
			System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");
			System.out.println("Successful Data transfer");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
