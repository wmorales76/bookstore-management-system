package edu.suagm.soe.tftpexample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;
import library.Author;
//import the bst
import library.BinarySearchTree;

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

	// shared binary search tree
	private static final BinarySearchTree bst = new BinarySearchTree();

	/***********************************************************************
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

	private synchronized void addGenreCommand() {
		// read the genre from the client
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		int totalRead = 0;

		System.out.println("Add Genre Command");

		try {
			int read;

			// Write the OK code: make acknowledgment of ADD GENRE command
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();

			// Wait for genre name
			System.out.println("Waiting for the genre name");
			read = clientInputStream.read(buffer);
			String genre = new String(buffer).trim();
			// Save current time for computing transmission time
			long startTime = System.currentTimeMillis();
			// print the genre name
			System.out.println("add genre " + genre);

			// add the genre to the binary search tree
			// syncronized the access to the tree
			synchronized (bst) {
				bst.insertGenre(genre);
				System.out.println("Genre added: " + genre);
			}
			// Send confirmation to the client
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();

			long endTime = System.currentTimeMillis();
			System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");
			System.out.println("Successful Data transfer");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void addBookCommand() {
		// receive the book info from the client
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		int totalRead = 0;
		System.out.println("Add Book Command");

		try {
			int read;

			// Write the OK code: make acknowledgment of ADD BOOK command
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();

			// Wait for book info
			System.out.println("Waiting for the book info");
			read = clientInputStream.read(buffer);
			String bookInfo = new String(buffer).trim();
			// Save current time for computing transmission time
			long startTime = System.currentTimeMillis();
			// print the book info
			System.out.println("add book \n" + bookInfo);

			// add the book to the binary search tree
			// syncronized the access to the tree
			//String bookInfo = title + "|" + genre + "|" + plot + "|" + String.join(",", authors) + "|"
			//+ year + "|" + price + "|" + quantity;
			
			String[] bookInfoArray = bookInfo.split("\\|");
			String title = bookInfoArray[0];
			String genre = bookInfoArray[1];
			String plot = bookInfoArray[2];
			String[] authors = bookInfoArray[3].split(",");
			int year = Integer.parseInt(bookInfoArray[4]);
			double price = Double.parseDouble(bookInfoArray[5]);
			int quantity = Integer.parseInt(bookInfoArray[6]);

			Author[] authorsArray = new Author[authors.length];
			for (int i = 0; i < authors.length; i++) {
				String[] authorInfo = authors[i].split(" ");
				authorsArray[i] = new Author(authorInfo[0], authorInfo[1]);
			}

			synchronized (bst) {
				bst.insertBook(title, genre, plot, year, price, quantity);
				System.out.println("Book added: " + title);
			}
			
			
			
			// Send confirmation to the client
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();

			long endTime = System.currentTimeMillis();
			System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");
			System.out.println("Successful Data transfer");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void modifyBookCommand() {
		// TODO Auto-generated method stub

	}

	private synchronized void listGenresCommand() {
		try {
			synchronized(bst) {
				// Fetch genres from the binary search tree and prepare to send them
				String genres = bst.getGenres(); // Assuming bst.getGenres() returns a formatted string of genres
				
				System.out.println("GENRESSSSSSSSSSSSSSSSSSSSSSSSS");
				System.out.println(genres);
				byte[] buffer = genres.getBytes();
				
				// Send the genres to the client
				clientOutputStream.write(buffer);
				clientOutputStream.flush();
				System.out.println("List Genres Command: Genres sent to the client.");
			}
	
			// Await client's confirmation that genres have been received
			int clientResponse = clientInputStream.readInt();
			if (clientResponse == tftpCodes.OK) {
				System.out.println("Client confirmed receipt of genres.");
			} else {
				System.out.println("Client failed to confirm receipt of genres.");
			}
		} catch (Exception e) {
			System.err.println("Error during listGenresCommand: " + e.getMessage());
			e.printStackTrace();
		}
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

}
