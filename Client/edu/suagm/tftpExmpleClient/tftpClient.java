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
 * 		   Wilfredo Morales
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

			do {
				ShowMenu();
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

	/*
	 * ShowMenu Method
	 * This method shows the menu for the user to select the command to execute
	 */
	public void ShowMenu() {
		System.out.println("1. Add Genre");
		System.out.println("2. Add Book");
		System.out.println("3. Modify Book");
		System.out.println("4. List all Genres");
		System.out.println("5. List all Books by Genre");
		System.out.println("6. List all Books by a particular Genre");
		System.out.println("7. Search for a Book by Title");
		System.out.println("8. Buy a Book");
		System.out.println("9. Exit");
	}


	/**
	 * Adds a genre to the bookstore management system.
	 * 
	 * This method prompts the user to enter a genre and sends the "ADD_GENRE" command to the server.
	 * If the server responds with an "OK" code, the genre is sent to the server for addition.
	 * If the server confirms the addition with another "OK" code, the genre is added successfully.
	 * 
	 * @throws IOException if an I/O error occurs while communicating with the server.
	 */
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


	/**
	 * Sends a command to add a book to the server.
	 * This method sends the ADD_BOOK command to the server and waits for the OK code.
	 * If the OK code is received, it asks the server for the list of genres and sends the book information.
	 * Finally, it waits for the confirmation code and prints a success message if the book was added successfully.
	 */
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



	/**
	 * Modifies a book in the bookstore management system.
	 */
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
				if (bookInfo.equals("Book not found")) {
					System.out.println("Book not found");
					return;
				}

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



	/**
	 * Sends a SEARCH_BOOK command to the server, prompts the user to enter the title of the book,
	 * sends the title to the server, and receives and displays the book information returned by the server.
	 * 
	 * @throws IOException if an I/O error occurs while communicating with the server
	 */
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
				String title = getUserInput("Enter the title of the book\n", new Scanner(System.in));
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


	/**
	 * Sends the LIST_GENRES command to the server, receives the genres from the server,
	 * and acknowledges the receipt of genres.
	 * 
	 * @throws IOException if an I/O error occurs while communicating with the server
	 */
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


	/**
	 * Sends a command to the server to list all the books and their genres.
	 * Prints the genres and the books that belong to each genre.
	 * Confirms receipt of genres to the server.
	 * 
	 * @throws IOException if an I/O error occurs while communicating with the server.
	 */
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


	/**
	 * Sends a command to the server to list books by genre and displays the received books.
	 * 
	 * This method sends the LIST_BOOKS_BY_GENRE command to the server and waits for the server's confirmation.
	 * It then prompts the user to enter the genre of the book and sends it to the server.
	 * The server returns all the book information for the specified genre, which is then displayed to the user.
	 * Finally, it confirms the receipt of the books to the server.
	 * 
	 * @throws IOException if an I/O error occurs while communicating with the server
	 */
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



	/**
	 * Sends a command to buy a book to the server.
	 * Asks for the book information and decides whether to buy the book or not.
	 * 
	 * @throws IOException if an I/O error occurs while communicating with the server.
	 */
	private void buyBookCommand() {
		// read the title and ask for the book information, then decide if buy or not
		System.out.println("Sending command: BUY_BOOK");

		try {
			// Send the BUY_BOOK command to the server
			socketOutputStream.writeInt(tftpCodes.BUY_BOOK);
			socketOutputStream.flush();

			if (socketInputStream.readInt() == tftpCodes.OK) {

				// input the title of the book
				String title = getUserInput("Enter the title of the book\n", new Scanner(System.in));
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
					String answer = getUserInput("Do you want to buy this book? (y/n)\n", new Scanner(System.in));
					if (answer.equals("y")) {
						// send the quantity to the server
						socketOutputStream.writeInt(tftpCodes.OK);
						socketOutputStream.flush();

						if (tftpCodes.OK == socketInputStream.readInt()) {
							System.out.println("Book bought successfully");
						} else {
							System.out.println("The book could not be bought. Please try again later.");

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


	/**
	 * Sends a CloseConnection command to the server and waits for an OK code.
	 * Prints "Goodbye!" to the console upon successful completion.
	 */
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



	/**
	 * Reads the user's input commands and processes them.
	 * 
	 * @param reader the Scanner object used to read user input
	 * @return a String representing the results of the command processing
	 */
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


	/**
	 * Prompts the user to enter information about a book and returns the book info as a string.
	 *
	 * @param genres the list of genres available
	 * @return the book information as a string
	 */
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
			try {
				int genreIndex = Integer.parseInt(genre) - 1; // Convert to zero-based index
				if (genreIndex >= 0 && genreIndex < genreArray.length) {
					genre = genreArray[genreIndex]; // Assign the valid genre
					break; // Exit the loop once a valid genre is chosen
				} else {
					System.out.println("Invalid genre selection. Please try again.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a number.");
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
			String firstName = getUserInput("\nEnter the first name of author " + (i + 1) + ": ", reader);
			String lastName = getUserInput("Enter the last name of author " + (i + 1) + ": ", reader);
			authors[i] = firstName + " " + lastName;
		}

		// Ask for the quantity
		int quantity = getNumberInput("\nEnter the quantity of the book: ", reader);
		// Ask for the price
		double price = getDoubleInput("\nEnter the price of the book: ", reader);

		// Construct the book info string
		String bookInfo = title + "|" + genre + "|" + plot + "|" + String.join(",", authors) + "|"
				+ year + "|" + price + "|" + quantity;

		return bookInfo;
	}


	/**
	 * Checks if a given string is numeric.
	 *
	 * @param str the string to be checked
	 * @return true if the string is numeric, false otherwise
	 */
	private boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	
	/**
		* Prompts the user for input and returns the entered value as a string.
		* If the user enters an empty value, it will prompt again until a non-empty value is entered.
		*
		* @param prompt the message to display as a prompt for user input
		* @param reader the Scanner object used to read user input
		* @return the user's input as a string
		*/
	private static String getUserInput(String prompt, Scanner reader) {
		System.out.print(prompt);
		String input = reader.nextLine();
		while (input.trim().isEmpty()) {
			System.out.println("Invalid input. Please enter a value.");
			System.out.print(prompt);
			input = reader.nextLine();
		}
		return input;
	}

	/**
	 * Prompts the user for a number input and returns the input as an integer.
	 * 
	 * @param prompt the message to display as a prompt for the user
	 * @param reader the Scanner object used to read user input
	 * @return the user's input as an integer
	 */
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

	/**
	 * Prompts the user for a double input and returns the input as a double value.
	 * 
	 * @param prompt the message to display as a prompt for the user
	 * @param reader the Scanner object used to read user input
	 * @return the double value entered by the user
	 */
	private double getDoubleInput(String prompt, Scanner reader) {
		System.out.print(prompt);
		String input = reader.nextLine();
		while (!isNumeric(input)) {
			System.out.println("Invalid input. Please enter a number.");
			System.out.print(prompt);
			input = reader.nextLine();
		}
		return Double.parseDouble(input);
	}
}