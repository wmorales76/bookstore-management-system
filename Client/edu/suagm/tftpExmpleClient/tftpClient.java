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

import javax.swing.plaf.synth.SynthStyle;

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
		add_genre, add_book, list_genres, search_book, list_books, modify_book, list_genre_books, buy_book, exit
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

	/*****************************************************************************
	 * Data transfer method
	 * Tasks: Manage connection with the Server.
	 * Receive commands from the keyboard thru a Scanner object
	 * Invoke the appropriate method depending on the received command
	 * 
	 *****************************************************************************/
	@SuppressWarnings("resource")
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
					case tftpCodes.SEARCH_BOOK:
						searchBookCommand();
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
		System.out.println("2. Add Book <add_book>");
		System.out.println("3. Modify Book <modify_book>");
		System.out.println("4. List all Genres <list_genres>");
		System.out.println("5. List all Books by Genre <list_books>");
		System.out.println("6. List all Books by a particular Genre <list_genre_books>");
		System.out.println("7. Search for a Book by Title <search_book>");
		System.out.println("8. Buy a Book <buy_book>");
		System.out.println("9. Exit <exit>");
	}

	/***************************************************************************
	 * addGenreCommand Method
	 * Tasks: Sends the ADD_GENRE to the server
	 * Waits for OK confirmation code from the server
	 * Sends the genre to the server
	 * Waits for OK confirmation code from the server
	 * 
	 **************************************************************************/
	private void addGenreCommand() {
		try {
			String genre = getUserInput("Enter the genre:\n", new Scanner(System.in));
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

	private void addBookCommand() {
		try {

			// Send AddBook command
			System.out.println("Sending command: ADD_BOOK");
			socketOutputStream.writeInt(tftpCodes.ADD_BOOK);
			socketOutputStream.flush();
			System.out.println("Sent command: " + tftpCodes.ADD_BOOK);

			// Wait for OK code
			int read = socketInputStream.readInt();
			if (read == tftpCodes.OK) {
				// ask the server for the list of genres
				socketOutputStream.writeInt(tftpCodes.LIST_GENRES);
				socketOutputStream.flush();

				// Read the response which contains the genres
				byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
				read = socketInputStream.readInt();
				if (read == tftpCodes.EMPTY) {

					System.out.println("No genres found. Insert a genre first.");
					return;
				} else if (read == tftpCodes.FOUND) {
					System.out.println("Genres found.");
					// send ok
					socketOutputStream.writeInt(tftpCodes.OK);
					socketOutputStream.flush();
				}
				// extract all the genres from the buffed dynamically
				read = socketInputStream.read(buffer);
				String genres = new String(buffer, 0, read).trim();
				// print the genres

				String bookInfo = readBookInfo(genres);
				// Send the book info
				System.out.println("Sending the book info");
				buffer = bookInfo.getBytes();
				socketOutputStream.write(buffer);
				socketOutputStream.flush();
				System.out.println("Sent book info: " + bookInfo);

				System.out.println("Waiting for confirmation");
				// Wait for OK code
				read = socketInputStream.readInt();

				if (read == tftpCodes.OK) {
					System.out.println("Book added successfully");
				}

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

				// ask if they are sure they want to modify it
				String answer = getUserInput("Do you want to modify this book? (y/n)", new Scanner(System.in));
				if (answer.equals("y")) {
					socketOutputStream.writeInt(tftpCodes.OK);
					socketOutputStream.flush();
					if (socketInputStream.readInt() == tftpCodes.OK) {

						// ask for the new info
						String newInfo = getUserInput("Enter the new price", new Scanner(System.in));
						newInfo += "|" + getUserInput("Enter the new quantity", new Scanner(System.in));
						// send the new info to the server
						socketOutputStream.write(newInfo.getBytes());
						socketOutputStream.flush();

						// wait for the server to return the confirmation
						if (socketInputStream.readInt() == tftpCodes.OK) {
							System.out.println("Book modified successfully");
						} else if (socketInputStream.readInt() == tftpCodes.ERROR) {
							System.out.println("Error modifying book");
						}
					}
				} else {
					System.out.println("You did not choose to modify the book");
					socketOutputStream.writeInt(tftpCodes.ERROR);
					socketOutputStream.flush();
				}

			}
		} catch (Exception e) {
			System.out.println("An error occurred while listing genres: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private void searchBookCommand() {
		System.out.println("Sending command: SEARCH_BOOK");
		try {
			// Send the SEARCH_BOOK command to the server
			socketOutputStream.writeInt(tftpCodes.SEARCH_BOOK);
			socketOutputStream.flush();

			// Wait for OK code
			int read = socketInputStream.readInt();
			if (read == tftpCodes.OK) {
				// input the title of the book
				String title = getUserInput("Enter the title of the book", new Scanner(System.in));
				// send the title to the server
				socketOutputStream.write(title.getBytes());
				socketOutputStream.flush();
				// wait for the server to return all the book information
				byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
				read = socketInputStream.read(buffer);
				// extract all the genres from the buffed dynamically
				String bookInfo = new String(buffer, 0, read).trim();
				System.out.println("Received book info from the server: ");
				System.out.println("=============================");
				System.out.println(bookInfo);
				System.out.println("=============================");
				// Confirm receipt of genres to the server
				socketOutputStream.writeInt(tftpCodes.OK);
				socketOutputStream.flush();
				System.out.println("Book received and acknowledged successfully.");
			} else {
				System.out.println("Error listing books by genre");
			}

		} catch (Exception e) {
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

			System.out.println("Received genres from the server: \n");
			System.out.println(genres);

			// Confirm receipt of genres to the server
			socketOutputStream.writeInt(tftpCodes.OK);
			socketOutputStream.flush();
			System.out.println("\nGenres received and acknowledged successfully.");

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
			System.out.println("\nBooks received and acknowledged successfully.");

		} catch (Exception e) {
			System.out.println("An error occurred while listing genres: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private void listBooksByGenreCommand() {
		// wait for server conformation for command
		System.out.println("Sending command: LIST_BOOKS_BY_GENRE");

		try {
			// Send the LIST_BOOKS_BY_GENRE command to the server
			socketOutputStream.writeInt(tftpCodes.LIST_BOOKS_BY_GENRE);
			socketOutputStream.flush();

			// Wait for OK code
			int read = socketInputStream.readInt();
			if (read == tftpCodes.OK) {
				// input the genre
				String genre = getUserInput("Enter the genre of the book\n", new Scanner(System.in));
				// send the genre to the server
				socketOutputStream.write(genre.getBytes());
				socketOutputStream.flush();
				// wait for the server to return all the book information
				byte[] buffer = new byte[tftpCodes.BUFFER_SIZE];
				read = socketInputStream.read(buffer);
				// extract all the genres from the buffed dynamically
				String books = new String(buffer, 0, read).trim();
				System.out.println("Received books from the server:\n");
				System.out.println("=============================");
				System.out.println(books);
				System.out.println("=============================");
				// Confirm receipt of genres to the server
				socketOutputStream.writeInt(tftpCodes.OK);
				socketOutputStream.flush();
				System.out.println("\nBooks received and acknowledged successfully.");
			} else {
				System.out.println("Error listing books by genre");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void buyBookCommand() {
		// read the title and ask for the book information, then decide if buy or not
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

				// wait for ok from the server
				if (socketInputStream.readInt() == tftpCodes.OK) {
					System.out.println("System is ready to buy the book.");

					// ask if they are sure they want to buy it
					String answer = getUserInput("Do you want to buy this book? (y/n)", new Scanner(System.in));
					if (answer.equals("y")) {
						// send the quantity to the server
						socketOutputStream.writeInt(tftpCodes.OK);
						socketOutputStream.flush();

						if (tftpCodes.OK == socketInputStream.readInt()) {
							System.out.println("Book bought successfully");
						} else {
							System.out.println("Server did not acknowledge the BUY_BOOK command");

						}

					} else {
						System.out.println("You did not choose to buy the book");
						socketOutputStream.writeInt(tftpCodes.ERROR);
						socketOutputStream.flush();
					}

				}

			}
		} catch (Exception e) {
			System.out.println("An error occurred while listing genres: " + e.getMessage());
			e.printStackTrace();
		}

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
			switch (Integer.parseInt(commandList.get(0))) {
				// Get command
				case 1:
					if (commandList.size() == 1) {
						currentCommand = tftpCodes.ADD_GENRE;
					} else {
						currentCommand = tftpCodes.WRONGCOMMAND;
					}
					break;

				case 2:
					if (commandList.size() == 1) {
						currentCommand = tftpCodes.ADD_BOOK;
					} else {
						currentCommand = tftpCodes.WRONGCOMMAND;
					}
					break;
				case 3:
					if (commandList.size() == 1) {
						currentCommand = tftpCodes.MODIFY_BOOK;
					} else {
						currentCommand = tftpCodes.WRONGCOMMAND;
					}
					break;

				case 4:
					if (commandList.size() == 1) {
						currentCommand = tftpCodes.LIST_GENRES;
					} else {
						currentCommand = tftpCodes.WRONGCOMMAND;
					}
					break;
				case 5:
					if (commandList.size() == 1) {
						currentCommand = tftpCodes.LIST_BOOKS;
					} else {
						currentCommand = tftpCodes.WRONGCOMMAND;
					}
					break;

				case 6:
					if (commandList.size() == 1) {
						currentCommand = tftpCodes.LIST_BOOKS_BY_GENRE;
					} else {
						currentCommand = tftpCodes.WRONGCOMMAND;
					}
					break;

				case 7:
					if (commandList.size() == 1) {
						currentCommand = tftpCodes.SEARCH_BOOK;
					} else {
						currentCommand = tftpCodes.WRONGCOMMAND;
					}
					break;
				case 8:
					if (commandList.size() == 1) {
						currentCommand = tftpCodes.BUY_BOOK;
					} else {
						currentCommand = tftpCodes.WRONGCOMMAND;
					}
					break;
				case 9:
					if (commandList.size() == 1) {
						currentCommand = tftpCodes.CLOSECONNECTION;
					} else {
						currentCommand = tftpCodes.WRONGCOMMAND;
					}
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

	private String readBookInfo(String genres) {
		Scanner reader = new Scanner(System.in);

		// Display the list of genres to the user
		System.out.println("\nAvailable Genres:");
		String[] genreArray = genres.split("\n");
		for (int i = 0; i < genreArray.length; i++) {
			System.out.println((i + 1) + ": " + genreArray[i]);
		}

		// Ensure the user selects a valid genre
		String genre;
		while (true) {
			genre = getUserInput("\nChoose a genre by entering the corresponding number: ", reader);
			int genreIndex = Integer.parseInt(genre) - 1; // Convert to zero-based index
			if (genreIndex >= 0 && genreIndex < genreArray.length) {
				genre = genreArray[genreIndex]; // Assign the valid genre
				break; // Exit the loop once a valid genre is chosen
			} else {
				System.out.println("Invalid genre selection. Please try again.");
			}
		}

		// Ask for the title
		String title = getUserInput("\nEnter the title of the book: ", reader);
		// Ask for the plot
		String plot = getUserInput("\nEnter the plot of the book: ", reader);
		// Ask for the year
		String year = getUserInput("\nEnter the year of the book: ", reader);
		// Ask for the number of authors
		int amountAuthors = getNumberInput("\nEnter the number of authors: ", reader);

		// Collect author names
		String[] authors = new String[amountAuthors];
		for (int i = 0; i < amountAuthors; i++) {
			authors[i] = getUserInput("\nEnter the name of author " + (i + 1) + ": ", reader);
		}

		// Ask for the quantity
		int quantity = getNumberInput("\nEnter the quantity of the book: ", reader);
		// Ask for the price
		double price = Double.parseDouble(getUserInput("\nEnter the price of the book: ", reader));

		// Construct the book info string
		String bookInfo = title + "|" + genre + "|" + plot + "|" + String.join(",", authors) + "|"
				+ year + "|" + price + "|" + quantity;

		return bookInfo;
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
}