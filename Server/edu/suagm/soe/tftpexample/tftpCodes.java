package edu.suagm.soe.tftpexample;

/**
 * The {@code tftpCodes} class represents the codes used in a TFTP (Trivial File Transfer Protocol) system.
 * It provides constants for various data transfer operations such as adding genres, adding books, modifying books,
 * listing genres, listing books, listing books by genre, searching books, buying books, and more.
 * These codes are used to communicate between the client and server in the TFTP system.
 */
public class tftpCodes {

	// Buffer size
	public static final int BUFFER_SIZE = 1024;

	// Data transfer codes--------------------------------------------
	// OK
	public static final int OK = 1;

	// Get
	public static final int ADD_GENRE = 2;

	// Put
	public static final int ADD_BOOK = 3;

	public static final int MODIFY_BOOK = 4;

	public static final int LIST_GENRES = 5;

	public static final int LIST_BOOKS = 6;

	// list book by genre
	public static final int LIST_BOOKS_BY_GENRE = 7;

	// search book
	public static final int SEARCH_BOOK = 8;

	// buy a book
	public static final int BUY_BOOK = 9;

	// found the item
	public static final int FOUND = 10;

	// item already exists
	public static final int ALREADYEXISTS = 11;

	// item not found
	public static final int NOTFOUND = 12;

	// empty
	public static final int EMPTY = 13;

	public static final int ERROR = 14;

	// Close connection
	public static final int CLOSECONNECTION = 15;

	// No valid command
	public static final int WRONGCOMMAND = 30;

}
