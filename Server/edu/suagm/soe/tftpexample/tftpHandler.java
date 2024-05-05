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
		// Read the genre from the client
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		// int totalRead = 0;

		System.out.println("Add Genre Command");

		try {
			int read;

			// Write the OK code: make acknowledgment of ADD GENRE command
			try {
				clientOutputStream.writeInt(tftpCodes.OK);
				clientOutputStream.flush();
			} catch (IOException e) {
				System.out.println("Error writing ACK to client.");
				e.printStackTrace();
			}

			// Wait for genre name
			System.out.println("Waiting for the genre name");
			String genre = "";
			try {
				read = clientInputStream.read(buffer);
				genre = new String(buffer).trim();
				// print the genre name
				System.out.println("add genre " + genre);
			} catch (IOException e) {
				System.out.println("Error reading genre name from client.");
				e.printStackTrace();
			}

			// Add the genre to the binary search tree
			// Synchronized access to the tree
			boolean genreAdded = addGenre(genre);
			System.out.println("Genre added: " + genre);

			// Send confirmation to the client
			try {
				clientOutputStream.writeInt(tftpCodes.OK);
				clientOutputStream.flush();
				System.out.println("Successful Data transfer");
			} catch (IOException e) {
				System.out.println("Error sending confirmation to client.");
				e.printStackTrace();
			}

		} catch (Exception e) {
			System.out.println("An error occurred in addGenreCommand.");
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
		// Receive the book info from the client
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		System.out.println("Add Book Command");

		try {
			int read;

			// Acknowledge ADD BOOK command
			try {
				clientOutputStream.writeInt(tftpCodes.OK);
				clientOutputStream.flush();
			} catch (IOException e) {
				System.out.println("Error sending ACK for ADD BOOK.");
				e.printStackTrace();
			}

			// Wait for the client to ask for list of genres and send it
			try {
				if (clientInputStream.readInt() == tftpCodes.LIST_GENRES) {
					String genres = listGenres();
					System.out.println(genres);

					if ("No genres found".equals(genres)) {
						clientOutputStream.writeInt(tftpCodes.EMPTY);
						clientOutputStream.flush();
						return;
					} else {
						System.out.println("Sending found code to the client");
						clientOutputStream.writeInt(tftpCodes.FOUND);
						clientOutputStream.flush();
					}

					if (clientInputStream.readInt() == tftpCodes.OK) {
						System.out.println("Received OK code. Sending genres to the client");
						buffer = genres.getBytes();
						clientOutputStream.write(buffer);
						clientOutputStream.flush();
					} else {
						System.out.println("Client failed to confirm receipt of genres.");
						return;
					}
				}
			} catch (IOException e) {
				System.out.println("Error communicating list of genres.");
				e.printStackTrace();
			}

			// Wait for and process book info
			System.out.println("Waiting for the book info");
			buffer = new byte[tftpCodes.BUFFER_SIZE];
			read = clientInputStream.read(buffer);
			String bookInfo = new String(buffer, 0, read).trim();
			System.out.println("add book \n" + bookInfo);

			// Parse and add book information
			try {
				String[] bookInfoArray = bookInfo.split("\\|");
				String title = bookInfoArray[0];
				String genre = bookInfoArray[1];
				String plot = bookInfoArray[2];
				String[] authors = bookInfoArray[3].split(",");
				String year = bookInfoArray[4];
				double price = Double.parseDouble(bookInfoArray[5]);
				int quantity = Integer.parseInt(bookInfoArray[6]);

				boolean bookAdded = addBook(genre, title, plot, authors, year, price, quantity);
				String book = getBookInfo(title);
				System.out.println("Book added: " + book);
			} catch (NumberFormatException e) {
				System.out.println("Error parsing book information.");
				e.printStackTrace();
			}

			// Send final confirmation
			try {
				clientOutputStream.writeInt(tftpCodes.OK);
				clientOutputStream.flush();
				System.out.println("Successful Data transfer");
			} catch (IOException e) {
				System.out.println("Error confirming data transfer.");
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("An error occurred in addBookCommand.");
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

		// Send OK to the client to acknowledge the command
		try {
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();
		} catch (IOException e) {
			System.out.println("Error sending OK to client.");
			e.printStackTrace();
		}

		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		try {
			// Wait for book title from the client
			System.out.println("Waiting for the book title");
			int read = clientInputStream.read(buffer);
			String title = new String(buffer, 0, read).trim();

			// Use the method to get all the info from the book as string
			String bookInfo = getBookInfo(title);

			// Send the book info to the client
			try {
				clientOutputStream.write(bookInfo.getBytes());
				clientOutputStream.flush();
				if (bookInfo.equals("Book not found")) {
					System.out.println("Book not found. Ending modifyBookCommand.");
					return;
				}
				System.out.println("Book info sent to the client.");
			} catch (IOException e) {
				System.out.println("Error sending book information to client.");
				e.printStackTrace();
			}

			// Await client's confirmation that book info has been received
			int clientResponse = clientInputStream.readInt();
			if (clientResponse == tftpCodes.OK) {
				System.out.println("Client confirmed receipt of book info.");
				try {
					// Server is ready to receive the new book info
					clientOutputStream.writeInt(tftpCodes.OK);
					clientOutputStream.flush();

					// Wait for the new book info
					read = clientInputStream.read(buffer);
					String newBookInfo = new String(buffer, 0, read).trim();

					// Parse the new book info: price | quantity
					try {
						String[] bookInfoArray = newBookInfo.split("\\|");
						double price = Double.parseDouble(bookInfoArray[0]);
						int quantity = Integer.parseInt(bookInfoArray[1]);

						// Modify the book
						boolean success = modifyBook(title, price, quantity);
						if (success) {
							System.out.println("Book modified: " + title);
							clientOutputStream.writeInt(tftpCodes.OK);
							clientOutputStream.flush();
							System.out.println("Successful Data transfer");
						} else {
							clientOutputStream.writeInt(tftpCodes.ERROR);
							clientOutputStream.flush();
							System.out.println("Failed to modify book: " + title);
						}
					} catch (NumberFormatException e) {
						System.out.println("Error parsing new book info.");
						e.printStackTrace();
					}
				} catch (IOException e) {
					System.out.println("Error confirming new book info receipt.");
					e.printStackTrace();
				}
			} else if (clientResponse == tftpCodes.ERROR) {
				System.out.println("Client does not want to modify the book.");
			}

		} catch (IOException e) {
			System.out.println("Error reading from client stream.");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("An unexpected error occurred in modifyBookCommand.");
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
			String genres;
			try {
				genres = listGenres(); // Assuming listGenres returns a formatted string of genres
				System.out.println(genres);
			} catch (Exception e) {
				System.err.println("Error fetching genres: " + e.getMessage());
				e.printStackTrace();
				return; // Stop further execution if genres can't be fetched
			}

			byte[] buffer = genres.getBytes();

			// Send the genres to the client
			try {
				clientOutputStream.write(buffer);
				clientOutputStream.flush();
				System.out.println("List Genres Command: Genres sent to the client.");
			} catch (IOException e) {
				System.err.println("Error sending genres to the client: " + e.getMessage());
				e.printStackTrace();
				return; // Stop further execution if send fails
			}

			// Await client's confirmation that genres have been received
			try {
				int clientResponse = clientInputStream.readInt();
				if (clientResponse == tftpCodes.OK) {
					System.out.println("Client confirmed receipt of genres.");
				} else {
					System.out.println("Client failed to confirm receipt of genres.");
				}
			} catch (IOException e) {
				System.err.println("Error reading client's confirmation: " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.err.println("Unexpected error during listGenresCommand: " + e.getMessage());
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
		// Get all the genres and display below each all the books that belong to that
		// genre with all the information
		try {
			String books;
			try {
				// Fetch books from the binary search tree and prepare to send them
				books = listBooks(); // Assuming listBooks returns a formatted string of books
				System.out.println(books);
			} catch (Exception e) {
				System.err.println("Error fetching books: " + e.getMessage());
				e.printStackTrace();
				return; // Stop further execution if books can't be fetched
			}

			byte[] buffer = books.getBytes();

			// Send the books to the client
			try {
				clientOutputStream.write(buffer);
				clientOutputStream.flush();
				System.out.println("List Books Command: Books sent to the client.");
			} catch (IOException e) {
				System.err.println("Error sending books to the client: " + e.getMessage());
				e.printStackTrace();
				return; // Stop further execution if send fails
			}

			// Await client's confirmation that books have been received
			try {
				int clientResponse = clientInputStream.readInt();
				if (clientResponse == tftpCodes.OK) {
					System.out.println("Client confirmed receipt of books.");
				} else {
					System.out.println("Client failed to confirm receipt of books.");
				}
			} catch (IOException e) {
				System.err.println("Error reading client's confirmation: " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.err.println("Unexpected error during listBooksCommand: " + e.getMessage());
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
		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		System.out.println("List Books By Genre Command");

		try {
			int read;

			// Acknowledge the client's request
			try {
				clientOutputStream.writeInt(tftpCodes.OK);
				clientOutputStream.flush();
			} catch (IOException e) {
				System.err.println("Error sending OK to client.");
				e.printStackTrace();
				return; // If we cannot send the OK, no point in continuing
			}

			// Wait for the genre name from the client
			try {
				System.out.println("Waiting for the genre name");
				read = clientInputStream.read(buffer);
				if (read == -1) {
					System.out.println("Client closed the connection unexpectedly.");
					return;
				}
			} catch (IOException e) {
				System.err.println("Error reading genre name from client.");
				e.printStackTrace();
				return; // If we can't read the genre, no point in continuing
			}

			String genre = new String(buffer, 0, read).trim();
			System.out.println("List books by genre " + genre);

			String books = getBooksByGenre(genre); // Get all the books by genre
			byte[] bufferBooks = books.getBytes();

			// Send the books to the client
			try {
				clientOutputStream.write(bufferBooks);
				clientOutputStream.flush();
				System.out.println("List Books By Genre Command: Books sent to the client.");
			} catch (IOException e) {
				System.err.println("Error sending books to client.");
				e.printStackTrace();
				return; // If sending fails, no point in continuing
			}

			// Await client's confirmation that books have been received
			try {
				int clientResponse = clientInputStream.readInt();
				if (clientResponse == tftpCodes.OK) {
					System.out.println("Client confirmed receipt of books.");
				} else {
					System.out.println("Client failed to confirm receipt of books.");
				}
			} catch (IOException e) {
				System.err.println("Error receiving confirmation from client.");
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.err.println("Unexpected error during listBooksByGenreCommand: " + e.getMessage());
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
			// Send OK to the client as an acknowledgment
			try {
				clientOutputStream.writeInt(tftpCodes.OK);
				clientOutputStream.flush();
			} catch (IOException e) {
				System.err.println("Error sending OK to client.");
				e.printStackTrace();
				return; // Early exit if unable to communicate with the client
			}

			byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
			int read;

			// Wait for the book title from the client
			try {
				System.out.println("Waiting for the book title");
				read = clientInputStream.read(buffer);
				if (read == -1) {
					System.err.println("Connection closed unexpectedly by client.");
					return;
				}
			} catch (IOException e) {
				System.err.println("Error reading book title from client.");
				e.printStackTrace();
				return; // Early exit if unable to read from the client
			}

			String title = new String(buffer, 0, read).trim();
			String bookInfo = getBookInfo(title); // Fetch book info based on title

			// Send the book info to the client
			try {
				clientOutputStream.write(bookInfo.getBytes());
				clientOutputStream.flush();
				System.out.println("Book info sent to the client.");
			} catch (IOException e) {
				System.err.println("Error sending book information to client.");
				e.printStackTrace();
				return; // Early exit if unable to send data to the client
			}

			// Await client's confirmation that book info has been received
			try {
				int clientResponse = clientInputStream.readInt();
				if (clientResponse == tftpCodes.OK) {
					System.out.println("Client confirmed receipt of book info.");
				} else {
					System.out.println("Client failed to confirm receipt of book info.");
				}
			} catch (IOException e) {
				System.err.println("Error receiving confirmation from client.");
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.err.println("Unexpected error during Search Book Command: " + e.getMessage());
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

		// Send OK to the client to acknowledge the command
		try {
			clientOutputStream.writeInt(tftpCodes.OK);
			clientOutputStream.flush();
		} catch (IOException e) {
			System.err.println("Error sending OK to client.");
			e.printStackTrace();
			return; // If the initial acknowledgment fails, there's no point in proceeding
		}

		byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
		try {
			int read;
			// Wait for book title from the client
			System.out.println("Waiting for the book title");
			read = clientInputStream.read(buffer);
			if (read == -1) {
				System.out.println("Connection closed unexpectedly by client.");
				return;
			}

			String title = new String(buffer, 0, read).trim();
			String bookInfo = getBookInfo(title);

			// Send the book info to the client
			try {
				clientOutputStream.write(bookInfo.getBytes());
				clientOutputStream.flush();
				System.out.println("Book info sent to the client.");
			} catch (IOException e) {
				System.err.println("Error sending book information to client.");
				e.printStackTrace();
				return;
			}

			// Await client's confirmation that book info has been received
			int clientResponse;
			try {
				clientResponse = clientInputStream.readInt();
			} catch (IOException e) {
				System.err.println("Error receiving confirmation from client.");
				e.printStackTrace();
				return;
			}

			if (clientResponse == tftpCodes.OK) {
				System.out.println("Client confirmed receipt of book info.");
				// send ok
				clientOutputStream.writeInt(tftpCodes.OK);
				clientOutputStream.flush();

				// Process the buying of the book
				try {
					if (clientInputStream.readInt() == tftpCodes.OK) {
						System.out.println("Client confirmed purchase intent.");
						boolean success = buyBook(title);
						if (success) {
							System.out.println("Book bought: " + title);
							clientOutputStream.writeInt(tftpCodes.OK);
							clientOutputStream.flush();
							System.out.println("Successful Data transfer");
						} else {
							clientOutputStream.writeInt(tftpCodes.ERROR);
							clientOutputStream.flush();
							System.out.println("Failed to buy book: " + title);
						}
					} else {
						System.out.println("Client failed to confirm purchase intent.");
					}
				} catch (IOException e) {
					System.err.println("Error during transaction confirmation process.");
					e.printStackTrace();
				}
			} else {
				System.out.println("Client failed to confirm receipt of book info.");
			}

		} catch (IOException e) {
			System.err.println("Error in book purchasing process.");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Unexpected error during Buy Book Command.");
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
