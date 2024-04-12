package edu.suagm.soe.tftpexample;

public class tftpCodes {

	//Buffer size
	public static final int BUFFER_SIZE=1024;
	
	
	//Data transfer codes--------------------------------------------
	//OK 
	public static final int OK=1;
	
	//Get
	public static final int ADD_GENRE=2;
	
	//Put
	public static final int ADD_BOOK=3;

	public static final int MODIFY_BOOK=4;

	public static final int LIST_GENRES=5;

	public static final int LIST_BOOKS=6;

	//list book by genre
	public static final int LIST_BOOKS_BY_GENRE=7;

	//search book
	public static final int SEARCH_BOOK=8;

	//buy a book
	public static final int BUY_BOOK=9;
	

	
	//found the item
	public static final int FOUND=10;

	//item already exists
	public static final int ALREADYEXISTS=11;

	//item not found
	public static final int NOTFOUND=12;

	//empty
	public static final int EMPTY=13;

	//Close connection
	public static final int CLOSECONNECTION=15;
	
	//No valid command
	public static final int WRONGCOMMAND=30;
	
}
