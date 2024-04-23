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
 *         CPEN 457 - Programming Languages
 *         This class is the Trivial FTP client
 *         Fall 2017
 *
 *************************************************************************/

public class tftpClient {

	// List of valid commmands on the protocol
	private enum tftpValidCommands {
		add_genre, add_book, list_genres, list_books, modify_book, list_genre_books, buy_book, exit
	}

	// Current command
	private int currentCommand;

	// Data stream and output streams for data transfer
	private DataInputStream socketInputStream;
	private DataOutputStream socketOutputStream;

	// Connection parameters
	private String serverAddressStr;
	private int serverPort;

	public static void main(String args[]) {
		// Extract port and IP address from the arguments
		String serverAddressStr = args[0];
		@SuppressWarnings("removal")
		int serverPort = (new Integer(args[1])).intValue();
		System.out.println("Connecting to " + serverAddressStr + " Through " + serverPort);

		// Create the client object
		tftpClient clientObj = new tftpClient(serverAddressStr, serverPort);

		// Perform data transfer
		clientObj.dataTransfer();

	}

	/****************************************************************************
	 * 
	 * Constructor
	 * 
	 * @param serverAddressStr: Server IP Address in canonical representation
	 *                          W.X.Y.Z
	 * @param serverPort:       Server Port
	 * 
	 *****************************************************************************/
	public tftpClient(String serverAddressStr, int serverPort) {
		this.serverAddressStr = serverAddressStr;
		this.serverPort = serverPort;
	}

	// get user input
	private static String getUserInput(String prompt, Scanner reader) {
		System.out.print(prompt);
		return reader.nextLine();
	}

	// get a number input
	private int getNumberInput(String prompt, Scanner reader) {
		System.out.print(prompt);
		String input = reader.nextLine();
		while (!isNumeric(input)) {
			System.out.println("Invalid input. Please enter a number.");
			System.out.print(prompt);
			input = reader.nextLine();
		}
		return Integer.parseInt(input);
	}

	/*****************************************************************************
	 * Data transfer method
	 * Tasks: Manage connection with the Server.
	 * Receive commands from the keyboard thru a Scanner object
	 * Invoke the appropriate method depending on the received command
	 * 
	 *****************************************************************************/
	private void dataTransfer() {
		String arguments = "";

		// Scanner for reading commands from the keyboard
		Scanner reader = new Scanner(System.in);

		try {

			// Converting canonical IP address into InetAddress
			InetAddress serverAddress = InetAddress.getByName(serverAddressStr);

			// Create a client socket and connect to the Server
			Socket socket = new Socket(serverAddress, serverPort);

			// Create the Data Stream for data transfer
			socketInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			socketOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			System.out.println("Connected to the server");
			// Show the menu
			ShowMenu();
			do {
				// Read next command
				arguments = readCommands(reader);

				switch (currentCommand) {
					case tftpCodes.ADD_GENRE:
						addGenreCommand(arguments);
						break;

					case tftpCodes.ADD_BOOK:
						// TODO print the instructions for inputs
						// TODO print all the genres

						String title = getUserInput("\nEnter the title of the book", reader);
						// ask for the plot
						String plot = getUserInput("\nEnter the plot of the book", reader);
						String year = getUserInput("\nEnter the year of the book", reader);
						String genre = getUserInput("\nEnter the genre of the book", reader);
						// ask the amount of authors
						int amountAuthors = getNumberInput("\nEnter the amount of authors", reader);
						// create a string with the authors
						String authors = "";
						for (int i = 0; i < amountAuthors; i++) {
							authors += getUserInput("\nEnter the author " + (i + 1) + " of the book", reader);
							if (i < amountAuthors - 1) {
								authors += ",";
							}
						}
						// ask the quantity
						int quantity = getNumberInput("\nEnter the quantity of the book", reader);
						// ask the price
						double price = Double.parseDouble(getUserInput("\nEnter the price of the book", reader));

						// create the book info
						String bookInfo = title + "|" + genre + "|" + plot + "|" + String.join(",", authors) + "|"
								+ year + "|" + price + "|" + quantity;
						addBookCommand(bookInfo);
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

					// Wrong command
					case tftpCodes.WRONGCOMMAND:
						System.out.println(arguments + " is not a valid command");
						break;
				}

			} while (currentCommand != tftpCodes.CLOSECONNECTION);

			// Close scanner
			reader.close();
			reader = null;

			// Close socket and connections
			socketOutputStream.close();
			socketInputStream.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// create the menu for add genre, add book, modify book, list all genres,
	// list all books by genre, list all books by a particular genre, search for a
	// book by title
	// buy a book and exit
	public void ShowMenu() {
		System.out.println("1. Add Genre <add_genre>");
		System.out.println("2. Add Book");
		System.out.println("3. Modify Book");
		System.out.println("4. List all Genres");
		System.out.println("5. List all Books by Genre");
		System.out.println("6. List all Books by a particular Genre");
		System.out.println("7. Search for a Book by Title");
		System.out.println("8. Buy a Book");
		System.out.println("9. Exit");
	}

	/***************************************************************************
	 * addGenreCommand Method
	 * Tasks: Sends the ADD_GENRE to the server
	 * Waits for OK confirmation code from the server
	 * Sends the genre to the server
	 * Waits for OK confirmation code from the server
	 * 
	 **************************************************************************/
	private void addGenreCommand(String genre) {
		try {
			// Send AddGenre command
			System.out.println("Sending command: ADD_GENRE");
			socketOutputStream.writeInt(tftpCodes.ADD_GENRE);
			socketOutputStream.flush();
			System.out.println("Sent command: " + tftpCodes.ADD_GENRE);

			// Wait for OK code
			int read = socketInputStream.readInt();
			if (read == tftpCodes.OK) {
				// Send the genre
				System.out.println("Sending the genre" + genre);
				socketOutputStream.write(genre.getBytes());
				socketOutputStream.flush();
				System.out.println("Sent genre: " + genre);

				System.out.println("Waiting for confirmation");
				// Wait for OK code
				read = socketInputStream.readInt();
				if (read == tftpCodes.OK) {
					System.out.println("Genre added successfully");
				}

			} else {
				System.out.println("Error adding genre");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addBookCommand(String bookInfo) {
		try {
			// Send AddBook command
			System.out.println("Sending command: ADD_BOOK");
			socketOutputStream.writeInt(tftpCodes.ADD_BOOK);
			socketOutputStream.flush();
			System.out.println("Sent command: " + tftpCodes.ADD_BOOK);

			// Wait for OK code
			int read = socketInputStream.readInt();
			if (read == tftpCodes.OK) {
				// Send the book info
				System.out.println("Sending the book info");
				socketOutputStream.write(bookInfo.getBytes());
				socketOutputStream.flush();
				System.out.println("Sent book info: " + bookInfo);

				System.out.println("Waiting for confirmation");
				// Wait for OK code
				read = socketInputStream.readInt();
				long startTime = System.currentTimeMillis();

				if (read == tftpCodes.OK) {
					System.out.println("Book added successfully");
				}

				long endTime = System.currentTimeMillis();
				System.out.println("Book added in " + (endTime - startTime) + " ms.");

			} else {
				System.out.println("Error adding book");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void modifyBookCommand() {

		System.out.println("Sending command: MODIFY_BOOK");

		try {
			// Send the MODIFY_BOOK command to the server
			socketOutputStream.writeInt(tftpCodes.MODIFY_BOOK);
			socketOutputStream.flush();

			if (socketInputStream.readInt() == tftpCodes.OK) {
				// input the title of the book
				String title = getUserInput("Enter the title of the book", new Scanner(System.in));
				// send the title to the server
				socketOutputStream.write(title.getBytes());
				socketOutputStream.flush();
				// wait for the server to return all the book information
				byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
				int read = socketInputStream.read(buffer);
				// extract all the genres from the buffed dynamically
				String bookInfo = new String(buffer, 0, read).trim();
				System.out.println("Received book info from the server: ");
				System.out.println(bookInfo);
				// Confirm receipt of genres to the server
				socketOutputStream.writeInt(tftpCodes.OK);
				socketOutputStream.flush();

				// ask if they are sure they want to modify it
				String answer = getUserInput("Do you want to modify this book? (y/n)", new Scanner(System.in));
				if (answer.equals("y")) {
					// ask for the new info
					String newInfo = getUserInput("Enter the new price", new Scanner(System.in));
					newInfo += "|" + getUserInput("Enter the new quantity", new Scanner(System.in));
					// send the new info to the server
					socketOutputStream.write(newInfo.getBytes());
					socketOutputStream.flush();
					// wait for the server to return the confirmation
					if (socketInputStream.readInt() == tftpCodes.OK) {
						System.out.println("Book modified successfully");
					} else {
						System.out.println("Server did not acknowledge the MODIFY_BOOK command");
					}

				} else {
					System.out.println("You did not choose to modify the book");
				}

			}
		} catch (Exception e) {
			System.out.println("An error occurred while listing genres: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private void listGenresCommand() {
		System.out.println("Sending command: LIST_GENRES");
		try {
			// Send the LIST_GENRES command to the server
			socketOutputStream.writeInt(tftpCodes.LIST_GENRES);
			socketOutputStream.flush();

			// Read the response which contains the genres
			byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
			int read = socketInputStream.read(buffer);
			// extract all the genres from the buffed dynamically
			String genres = new String(buffer, 0, tftpCodes.BUFFER_SIZE).trim();

			System.out.println("Received genres from the server: ");
			System.out.println(genres);

			// Confirm receipt of genres to the server
			socketOutputStream.writeInt(tftpCodes.OK);
			socketOutputStream.flush();
			System.out.println("Genres received and acknowledged successfully.");

		} catch (Exception e) {
			System.out.println("An error occurred while listing genres: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void listBooksCommand() {
		// print the genre and then below all the books that belong to that genre
		try {
			// Send the LIST_BOOKS command to the server
			socketOutputStream.writeInt(tftpCodes.LIST_BOOKS);
			socketOutputStream.flush();

			// read the response that constains all the genres and the books that belong to
			// it
			byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
			int read = socketInputStream.read(buffer);
			// extract all the genres from the buffed dynamically
			String books = new String(buffer, 0, read).trim();

			System.out.println("Received books from the server: \n");
			System.out.println(books);

			// Confirm receipt of genres to the server
			socketOutputStream.writeInt(tftpCodes.OK);
			socketOutputStream.flush();
			System.out.println("Books received and acknowledged successfully.");

		} catch (Exception e) {
			System.out.println("An error occurred while listing genres: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private void listBooksByGenreCommand() {
		//wait for server conformation for command
		System.out.println("Sending command: LIST_BOOKS_BY_GENRE");

		try {
			// Send the LIST_BOOKS_BY_GENRE command to the server
			socketOutputStream.writeInt(tftpCodes.LIST_BOOKS_BY_GENRE);
			socketOutputStream.flush();

			// Wait for OK code
			int read = socketInputStream.readInt();
			if (read == tftpCodes.OK) {
				// input the genre
				String genre = getUserInput("Enter the genre of the book", new Scanner(System.in));
				// send the genre to the server
				socketOutputStream.write(genre.getBytes());
				socketOutputStream.flush();
				// wait for the server to return all the book information
				byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
				read = socketInputStream.read(buffer);
				// extract all the genres from the buffed dynamically
				String books = new String(buffer, 0, read).trim();
				System.out.println("Received books from the server: ");
				System.out.println(books);
				// Confirm receipt of genres to the server
				socketOutputStream.writeInt(tftpCodes.OK);
				socketOutputStream.flush();
				System.out.println("Books received and acknowledged successfully.");
			} else {
				System.out.println("Error listing books by genre");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void buyBookCommand() {
		//read the title and ask for the book information, then decide if buy or not
		System.out.println("Sending command: BUY_BOOK");

		try {
			// Send the BUY_BOOK command to the server
			socketOutputStream.writeInt(tftpCodes.BUY_BOOK);
			socketOutputStream.flush();

			if (socketInputStream.readInt() == tftpCodes.OK) {
				// input the title of the book
				String title = getUserInput("Enter the title of the book", new Scanner(System.in));
				// send the title to the server
				socketOutputStream.write(title.getBytes());
				socketOutputStream.flush();
				// wait for the server to return all the book information
				byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
				int read = socketInputStream.read(buffer);
				// extract all the genres from the buffed dynamically
				String bookInfo = new String(buffer, 0, read).trim();
				System.out.println("Received book info from the server: ");
				System.out.println(bookInfo);
				// Confirm receipt of genres to the server
				socketOutputStream.writeInt(tftpCodes.OK);
				socketOutputStream.flush();

				// ask if they are sure they want to buy it
				String answer = getUserInput("Do you want to buy this book? (y/n)", new Scanner(System.in));
				if (answer.equals("y")) {
					// ask for the quantity
					int quantity = getNumberInput("Enter the quantity", new Scanner(System.in));
					// send the quantity to the server
					socketOutputStream.writeInt(quantity);
					socketOutputStream.flush();
					// wait for the server to return the confirmation
					if (socketInputStream.readInt() == tftpCodes.OK) {
						System.out.println("Book bought successfully");
					} else {
						System.out.println("Server did not acknowledge the BUY_BOOK command");
					}

				} else {
					System.out.println("You did not choose to buy the book");
				}

			}
		} catch (Exception e) {
			System.out.println("An error occurred while listing genres: " + e.getMessage());
			e.printStackTrace();
		}

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
	 * Tasks: Sends the CLOSECONNECTION to the server for closing the connection
	 * Waits for OK confirmation code from the server
	 * 
	 **************************************************************************/
	private void exitCommand() {
		try {
			// Send CloseConnection command
			socketOutputStream.writeInt(tftpCodes.CLOSECONNECTION);
			socketOutputStream.flush();

			// Wait for OK code
			int read = socketInputStream.readInt();
			System.out.println("Goodbye!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String readCommands(Scanner reader) {
		// Print the prompt
		System.out.print(">");

		// Linked list for storing tokens in the sentence
		LinkedList<String> commandList = new LinkedList<String>();

		// Read the next command
		String sText = reader.nextLine();
		String results = "";

		// Parse the sentence
		StringTokenizer st = new StringTokenizer(sText);
		while (st.hasMoreTokens()) {
			commandList.add(st.nextToken());
		}

		// Verify the command
		try {
			switch (tftpValidCommands.valueOf(commandList.get(0))) {
				// Get command
				case add_genre:
					currentCommand = tftpCodes.ADD_GENRE;
					results = commandList.get(1);
					break;

				case add_book:
					if (commandList.size() == 1) {
						currentCommand = tftpCodes.ADD_BOOK;
					} else {
						currentCommand = tftpCodes.WRONGCOMMAND;

					}
					break;
				case list_genres:
					currentCommand = tftpCodes.LIST_GENRES;
					break;

				case list_books:
					currentCommand = tftpCodes.LIST_BOOKS;
					break;

				case exit:
					currentCommand = tftpCodes.CLOSECONNECTION;
					break;

				case modify_book:
					currentCommand = tftpCodes.MODIFY_BOOK;
					break;
				case list_genre_books:
					currentCommand = tftpCodes.LIST_BOOKS_BY_GENRE;
					break;
				case buy_book:
					currentCommand = tftpCodes.BUY_BOOK;
					break; 
				default:
					currentCommand = tftpCodes.WRONGCOMMAND;
					results = sText;
					break;
			}
		} catch (Exception e) {
			currentCommand = tftpCodes.WRONGCOMMAND;
			results = sText;
		}

		return results;
	}

	// check if the string is a number
	private boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
