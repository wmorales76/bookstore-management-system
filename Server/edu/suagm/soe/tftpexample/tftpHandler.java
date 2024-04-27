package edu.suagm.soe.tftpexample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

//import the bst
import library.BinarySearchTree;

/**************************************************************************
 * 
 * @author Idalides Vergara
 * @author Wilfredo Morales Aponte
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
				System.out.println("Waiting for a command");
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

	/**
	 * Adds a genre to the bookstore management system.
	 * This method reads the genre name from the client, adds it to the binary
	 * search tree,
	 * and sends a confirmation to the client.
	 */
	private void addGenreCommand() {
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
			boolean genreAdded = addGenre(genre);
			System.out.println("Genre added: " + genre);

			// Send confirmation to the client
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();
			System.out.println("Successful Data transfer");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Receives book information from the client and adds the book to the booklist.
	 * Sends acknowledgment codes and genres to the client as necessary.
	 * 
	 * @throws IOException if an I/O error occurs while reading or writing data.
	 */
	private void addBookCommand() {
		// receive the book info from the client
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		System.out.println("Add Book Command");

		try {
			int read;

			// Write the OK code: make acknowledgment of ADD BOOK command
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();
			// wait for the client to ask for list of genres and send it
			if (clientInputStream.readInt() == tftpCodes.LIST_GENRES) {
				String genres = listGenres();
				System.out.println(genres);
				if ("No genres found".equals(genres)) {
					// Send empty code
					clientOutputStream.writeInt(tftpCodes.EMPTY);
					clientOutputStream.flush();
					return;
				} else {
					// send found
					System.out.println("Sending found code to the client");
					clientOutputStream.writeInt(tftpCodes.FOUND);
					clientOutputStream.flush();

				}
				if (clientInputStream.readInt() == tftpCodes.OK) {
					// Send the genres to the client
					System.out.println("Received OK code. Sending genres to the client");
					buffer = genres.getBytes();
					// Send the genres to the client
					clientOutputStream.write(buffer);
					clientOutputStream.flush();
				} else {
					System.out.println("Client failed to confirm receipt of genres.");
					return;
				}

			}
			// Wait for book info
			System.out.println("Waiting for the book info");
			buffer = new byte[tftpCodes.BUFFER_SIZE];
			read = clientInputStream.read(buffer);
			String bookInfo = new String(buffer, 0, read).trim();
			// Save current time for computing transmission time
			// print the book info
			System.out.println("add book \n" + bookInfo);

			// Split the book info into its components
			String[] bookInfoArray = bookInfo.split("\\|");
			String title = bookInfoArray[0];
			String genre = bookInfoArray[1];
			String plot = bookInfoArray[2];
			String[] authors = bookInfoArray[3].split(",");
			String year = bookInfoArray[4];
			double price = Double.parseDouble(bookInfoArray[5]);
			int quantity = Integer.parseInt(bookInfoArray[6]);

			// add book to a booklist
			boolean bookAdded = addBook(genre, title, plot, authors, year, price, quantity);
			String book = getBookInfo(title);
			System.out.println("Book added: " + book);
			// Send confirmation to the client
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();
			System.out.println("Successful Data transfer");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Modifies a book based on the client's request.
	 * This method receives the book title from the client, retrieves the book
	 * information,
	 * sends the book info to the client, receives the new book info, and modifies
	 * the book accordingly.
	 * It communicates with the client using input and output streams.
	 */
	private void modifyBookCommand() {

		System.out.println("Modify Book Command");

		// send ok to the client
		try {
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// read the book title from the client
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		try {
			int read;
			// Wait for book title
			System.out.println("Waiting for the book title");
			read = clientInputStream.read(buffer);
			String title = new String(buffer, 0, read).trim();

			// use the method to get all the info from the book as string
			String bookInfo = getBookInfo(title);

			// send the book info to the client
			clientOutputStream.write(bookInfo.getBytes());
			clientOutputStream.flush();

			System.out.println("Book info sent to the client.");

			// Await client's confirmation that book info has been received
			int clientResponse = clientInputStream.readInt();
			if (clientResponse == tftpCodes.OK) {
				System.out.println("Client confirmed receipt of book info.");

				// server is ready to receive the new book info
				clientOutputStream.writeInt(tftpCodes.OK);
				clientOutputStream.flush();

				// wait for the new book info
				read = clientInputStream.read(buffer);
				String newBookInfo = new String(buffer, 0, read).trim();
				// Split the book info into its components price | quantity
				String[] bookInfoArray = newBookInfo.split("\\|");
				double price = Double.parseDouble(bookInfoArray[0]);
				int quantity = Integer.parseInt(bookInfoArray[1]);
				// modify the book
				boolean success = modifyBook(title, price, quantity);
				if (success) {
					System.out.println("Book modified: " + title);
					// Send confirmation to the client
					clientOutputStream.writeInt(tftpCodes.OK);
					clientOutputStream.flush();

					System.out.println("Successful Data transfer");
				} else {
					clientOutputStream.writeInt(tftpCodes.ERROR);
					clientOutputStream.flush();
					System.out.println("Failed to modify book: " + title);
				}

			} else if (clientResponse == tftpCodes.ERROR) {
				System.out.println("Client does not want to modify the book.");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Sends a list of genres to the client.
	 * 
	 * This method fetches the genres from a binary search tree and sends them to
	 * the client.
	 * It first retrieves the genres as a formatted string using the `listGenres`
	 * method.
	 * Then, it converts the string to a byte array and sends it to the client using
	 * the `clientOutputStream`.
	 * After sending the genres, it waits for the client's confirmation of receipt.
	 * If the client confirms receipt, it prints a success message. Otherwise, it
	 * prints a failure message.
	 * 
	 * @throws IOException if there is an error during the I/O operations.
	 */
	private void listGenresCommand() {
		try {

			// Fetch genres from the binary search tree and prepare to send them
			String genres = listGenres(); // Assuming listgenres returns a formatted string of genres
			System.out.println(genres);
			byte[] buffer = genres.getBytes();

			// Send the genres to the client
			clientOutputStream.write(buffer);
			clientOutputStream.flush();
			System.out.println("List Genres Command: Genres sent to the client.");

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

	/**
	 * Sends a list of books to the client.
	 * 
	 * This method fetches all the genres and displays below each genre all the
	 * books that belong to it, along with their information.
	 * The list of books is sent to the client as a byte array.
	 * 
	 * @throws IOException if an I/O error occurs while sending the books to the
	 *                     client.
	 */
	private void listBooksCommand() {
		// get all the genres and display below each all the book that belong to that
		// genre with all the information

		try {

			// Fetch genres from the binary search tree and prepare to send them
			String books = listBooks(); // Assuming listbooks returns a formatted string of books
			System.out.println(books);
			byte[] buffer = books.getBytes();

			// Send the genres to the client
			clientOutputStream.write(buffer);
			clientOutputStream.flush();
			System.out.println("List Books Command: Books sent to the client.");

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

	/**
	 * Retrieves and sends a list of books to the client based on the specified
	 * genre.
	 * 
	 * This method reads the genre name from the client, retrieves all the books
	 * belonging to that genre, and sends the list of books to the client. It also
	 * waits for the client's confirmation that the books have been received.
	 * 
	 * @throws IOException if an I/O error occurs while reading from or writing to
	 *                     the client
	 */
	private void listBooksByGenreCommand() {
		// get the genre from the client
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
			String genre = new String(buffer, 0, read).trim();
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

	/**
	 * Executes the search book command.
	 * This method sends an acknowledgment to the client, reads the book title from
	 * the client,
	 * retrieves the book information based on the title, sends the book information
	 * to the client,
	 * and waits for the client's confirmation of receipt.
	 * 
	 * @throws IOException if an I/O error occurs while reading from or writing to
	 *                     the client
	 */
	private void searchBookCommand() {
		System.out.println("Search Book Command");
		try {
			// send ok to the client
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();

			// read the book title from the client
			byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
			int read;
			// Wait for book title
			System.out.println("Waiting for the book title");
			read = clientInputStream.read(buffer);
			String title = new String(buffer, 0, read).trim();

			// use the method to get all the info from the book as string
			String bookInfo = getBookInfo(title);

			// send the book info to the client
			clientOutputStream.write(bookInfo.getBytes());
			clientOutputStream.flush();
			System.out.println("Book info sent to the client.");

			// Await client's confirmation that book info has been received
			int clientResponse = clientInputStream.readInt();
			if (clientResponse == tftpCodes.OK) {
				System.out.println("Client confirmed receipt of book info.");
			} else {
				System.out.println("Client failed to confirm receipt of book info.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Executes the "Buy Book" command.
	 * This method sends an acknowledgment to the client, reads the book title from
	 * the client,
	 * retrieves the book information, sends the book information to the client,
	 * waits for the client's confirmation, and performs the necessary actions to
	 * buy the book.
	 * If the book is successfully bought, a confirmation is sent to the client.
	 * If there are any errors during the process, appropriate error messages are
	 * sent to the client.
	 */
	private void buyBookCommand() {

		System.out.println("Buy Book Command");

		// send ok to the client
		try {
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// read the book title from the client
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		try {
			int read;
			// Wait for book title
			System.out.println("Waiting for the book title");
			read = clientInputStream.read(buffer);
			String title = new String(buffer, 0, read).trim();

			// use the method to get all the info from the book as string
			String bookInfo = getBookInfo(title);

			// send the book info to the client
			clientOutputStream.write(bookInfo.getBytes());
			clientOutputStream.flush();
			System.out.println("Book info sent to the client.");

			// Await client's confirmation that book info has been received
			int clientResponse = clientInputStream.readInt();
			if (clientResponse == tftpCodes.OK) {

				System.out.println("Client confirmed receipt of book info.");
				// wait for the new book info
				// read the quantity and remove from the database the amount of books

				if (clientInputStream.readInt() == tftpCodes.OK) {

					// modify the book
					boolean success = buyBook(title);

					if (success) {
						System.out.println("Book bought: " + title);
						// Send confirmation to the client
						clientOutputStream.writeInt(tftpCodes.OK);
						clientOutputStream.flush();
						System.out.println("Successful Data transfer");
					} else {
						clientOutputStream.writeInt(tftpCodes.ERROR);
						clientOutputStream.flush();
						System.out.println("Failed to buy book: " + title);
					}

				} else if (clientInputStream.readInt() == tftpCodes.ERROR) {
					System.out.println("Client failed to confirm receipt of book info.");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

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

	// SYNCHRONIZED METHODS

	/**
	 * Retrieves a formatted string of genres from the binary search tree.
	 *
	 * @return A string containing the genres.
	 */
	private synchronized String listGenres() {
		// Fetch genres from the binary search tree and prepare to send them
		String genres = bst.getGenres(); // Assuming bst.getGenres() returns a formatted string of genres
		return genres;
	}

	/**
	 * Adds a genre to the binary search tree.
	 * 
	 * @param genre the genre to be added
	 * @return true if the genre was successfully added, false otherwise
	 */
	private synchronized boolean addGenre(String genre) {
		// Add the genre to the binary search tree
		bst.insertGenre(genre);
		boolean flag = bst.checkGenre(genre);
		return flag;
	}

	/**
	 * Adds a book to the bookstore management system.
	 *
	 * @param genre    the genre of the book
	 * @param title    the title of the book
	 * @param plot     the plot of the book
	 * @param authors  the authors of the book
	 * @param year     the year of publication of the book
	 * @param price    the price of the book
	 * @param quantity the quantity of the book available in the inventory
	 * @return true if the book was successfully added, false otherwise
	 */
	private synchronized boolean addBook(String genre, String title, String plot, String[] authors, String year,
			double price, int quantity) {
		// Add the book to the binary search tree
		boolean flag = bst.addBooktoBST(genre, title, plot, authors, year, price, quantity);
		return flag;
	}

	/**
	 * Retrieves the information of a book based on its title.
	 * 
	 * @param title the title of the book to retrieve information for
	 * @return a string containing the formatted information of the book, or null if
	 *         the book is not found
	 */
	private synchronized String getBookInfo(String title) {
		// Fetch the book from the binary search tree
		String book = bst.getBookByTitle(title); // Assuming bst.getBookByTitle(title) returns a formatted string of
													// book info
		return book;
	}

	/**
	 * Modifies a book in the binary search tree.
	 * 
	 * @param title    the title of the book to modify
	 * @param price    the new price of the book
	 * @param quantity the new quantity of the book
	 * @return true if the book was successfully modified, false otherwise
	 */
	private synchronized boolean modifyBook(String title, double price, int quantity) {
		// Modify the book in the binary search tree
		boolean result = bst.modifyBook(title, price, quantity);
		return result;
	}

	/**
	 * Buys a book with the specified title.
	 * This method modifies the book in the binary search tree by decrementing its
	 * quantity by 1.
	 *
	 * @param title the title of the book to buy
	 * @return true if the book was successfully bought, false otherwise
	 */
	private synchronized boolean buyBook(String title) {
		// Modify the book in the binary search tree
		boolean result = bst.buyBook(title, 1);
		return result;
	}

	private synchronized String listBooks() {
		// Fetch genres from the binary search tree and prepare to send them
		String books = bst.getBooks(); // Assuming bst.getGenres() returns a formatted string of genres
		return books;
	}

	/**
	 * Retrieves a formatted string of books by genre from the binary search tree.
	 *
	 * @param genre the genre of the books to retrieve
	 * @return a formatted string of books matching the specified genre
	 */
	private synchronized String getBooksByGenre(String genre) {
		// Fetch the books from the binary search tree
		String books = bst.getBooksByGenre(genre); // Assuming bst.getBooksByGenre(genre) returns a formatted string of
													// books
		return books;
	}
}
