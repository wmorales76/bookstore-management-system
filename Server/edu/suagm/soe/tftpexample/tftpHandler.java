package edu.suagm.soe.tftpexample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
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
			System.out.println("Successful Data transfer");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private synchronized void addBookCommand() {
		// receive the book info from the client
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		System.out.println("Add Book Command");

		try {
			int read;

			// Write the OK code: make acknowledgment of ADD BOOK command
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();

			// Wait for book info
			System.out.println("Waiting for the book info");
			read = clientInputStream.read(buffer);
			String bookInfo = new String(buffer,0,read).trim();
			// Save current time for computing transmission time
			// print the book info
			System.out.println("add book \n" + bookInfo);

			// add the book to the binary search tree
			// syncronized the access to the tree
			//String bookInfo = title + "|" + genre + "|" + plot + "|" + String.join(",", authors) + "|"
			//+ year + "|" + price + "|" + quantity;
			

			// Split the book info into its components
			String[] bookInfoArray = bookInfo.split("\\|");
			String title = bookInfoArray[0];
			String genre = bookInfoArray[1];
			String plot = bookInfoArray[2];
			String[] authors = bookInfoArray[3].split(",");
			String year = bookInfoArray[4];
			double price = Double.parseDouble(bookInfoArray[5]);
			int quantity = Integer.parseInt(bookInfoArray[6]);


			//add book to a booklist
			bst.addBooktoBST(genre, title, plot, authors, year,price, quantity);
			String book = bst.getBookByTitle(title);
			System.out.println("Book added: " + book);
			// Send confirmation to the client
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();
			System.out.println("Successful Data transfer");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void modifyBookCommand() {
		
		System.out.println("Modify Book Command");
		
		//send ok to the client
		try {
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//read the book title from the client
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		try {
			int read;
			// Wait for book title
			System.out.println("Waiting for the book title");
			read = clientInputStream.read(buffer);
			String title = new String(buffer, 0, read).trim();
			
			//use the method to get all the info from the book as string
			String bookInfo = getBookInfo(title);

			//send the book info to the client
			clientOutputStream.write(bookInfo.getBytes());
			clientOutputStream.flush();
			System.out.println("Book info sent to the client.");

			// Await client's confirmation that book info has been received
			int clientResponse = clientInputStream.readInt();
			if (clientResponse == tftpCodes.OK) {
				System.out.println("Client confirmed receipt of book info.");
				//wait for the new book info
				read = clientInputStream.read(buffer);
				String newBookInfo = new String(buffer, 0, read).trim();
				// Split the book info into its components price | quantity
				String[] bookInfoArray = newBookInfo.split("\\|");
				double price = Double.parseDouble(bookInfoArray[0]);
				int quantity = Integer.parseInt(bookInfoArray[1]);
				//modify the book
				boolean success = modifyBook(title, price, quantity);
				if (success) {
					System.out.println("Book modified: " + title);
					// Send confirmation to the client
					clientOutputStream.writeInt(tftpCodes.OK);
					clientOutputStream.flush();
					System.out.println("Successful Data transfer");
				} else {
					System.out.println("Failed to modify book: " + title);
				}
			} else {
				System.out.println("Client failed to confirm receipt of book info.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	private synchronized String getBookInfo(String title) {
		// Fetch the book from the binary search tree
		String book = bst.getBookByTitle(title); // Assuming bst.getBookByTitle(title) returns a formatted string of book info
		return book;
	}

	private synchronized boolean modifyBook(String title, double price, int quantity) {
		// Modify the book in the binary search tree
		boolean result = bst.modifyBook(title, price, quantity);
		return result;
	}

	private synchronized void listGenresCommand() {
		try {
			synchronized(bst) {
				// Fetch genres from the binary search tree and prepare to send them
				String genres = bst.getGenres(); // Assuming bst.getGenres() returns a formatted string of genres
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
		//get all the genres and display below each all the book that belong to that genre with all the information

		try {
			synchronized(bst) {
				// Fetch genres from the binary search tree and prepare to send them
				String books = bst.getBooks(); // Assuming bst.getGenres() returns a formatted string of genres
				System.out.println(books);
				byte[] buffer = books.getBytes();
				
				// Send the genres to the client
				clientOutputStream.write(buffer);
				clientOutputStream.flush();
				System.out.println("List Books Command: Books sent to the client.");
			}
	
			// Await client's confirmation that genres have been received
			int clientResponse = clientInputStream.readInt();
			if (clientResponse == tftpCodes.OK) {
				System.out.println("Client confirmed receipt of books.");
			} else {
				System.out.println("Client failed to confirm receipt of books.");
			}
		} catch (Exception e) {
			System.err.println("Error during listGenresCommand: " + e.getMessage());
			e.printStackTrace();
		}



	}

	private void listBooksByGenreCommand() {
		//get the genre from the client
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		System.out.println("List Books By Genre Command");

		try {
			int read;

			// Write the OK code: make acknowledgment of ADD BOOK command
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();

			// Wait for genre name
			System.out.println("Waiting for the genre name");
			read = clientInputStream.read(buffer);
			String genre = new String(buffer,0,read).trim();
			// print the genre name
			System.out.println("List books by genre " + genre);

			// get all the books by genre
			String books = getBooksByGenre(genre);
			byte[] bufferBooks = books.getBytes();
			
			// Send the genres to the client
			clientOutputStream.write(bufferBooks);
			clientOutputStream.flush();
			System.out.println("List Books By Genre Command: Books sent to the client.");
	
			// Await client's confirmation that genres have been received
			int clientResponse = clientInputStream.readInt();
			if (clientResponse == tftpCodes.OK) {
				System.out.println("Client confirmed receipt of books.");
			} else {
				System.out.println("Client failed to confirm receipt of books.");
			}
		} catch (Exception e) {
			System.err.println("Error during listGenresCommand: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private synchronized String getBooksByGenre(String genre) {
		// Fetch the books from the binary search tree
		String books = bst.getBooksByGenre(genre); // Assuming bst.getBooksByGenre(genre) returns a formatted string of books
		return books;
	}

	private void searchBookCommand() {
		// TODO Auto-generated method stub

	}

	private void buyBookCommand() {
		
		System.out.println("Buy Book Command");
		
		//send ok to the client
		try {
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//read the book title from the client
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		try {
			int read;
			// Wait for book title
			System.out.println("Waiting for the book title");
			read = clientInputStream.read(buffer);
			String title = new String(buffer, 0, read).trim();
			
			//use the method to get all the info from the book as string
			String bookInfo = getBookInfo(title);

			//send the book info to the client
			clientOutputStream.write(bookInfo.getBytes());
			clientOutputStream.flush();
			System.out.println("Book info sent to the client.");

			// Await client's confirmation that book info has been received
			int clientResponse = clientInputStream.readInt();
			if (clientResponse == tftpCodes.OK) {
				System.out.println("Client confirmed receipt of book info.");
				//wait for the new book info
				//read the quantity and remove from the database the amount
				read = clientInputStream.read(buffer);
				int quantity = Integer.parseInt(new String(buffer, 0, read).trim());
				//modify the book
				boolean success = buyBook(title, quantity);

				if (success) {
					System.out.println("Book bought: " + title);
					// Send confirmation to the client
					clientOutputStream.writeInt(tftpCodes.OK);
					clientOutputStream.flush();
					System.out.println("Successful Data transfer");
				} else {
					System.out.println("Failed to buy book: " + title);
				}

		
			} else {
				System.out.println("Client failed to confirm receipt of book info.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private synchronized boolean buyBook(String title, int quantity) {
		// Modify the book in the binary search tree
		boolean result = bst.buyBook(title, quantity);
		return result;
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
