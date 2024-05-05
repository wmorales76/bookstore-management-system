package library;

/**
 * The BookList class represents a circular doubly linked list of books.
 * It provides methods to insert, modify, buy, and retrieve books from the list.
 */
public class BookList {
    private Node head;
    private Node tail;

    
    
    /**
     * Constructs a new instance of the BookList class with an empty list.
     */
    public BookList() {
        head = null;
        tail = null;
    }

    /**
     * Represents a node in the circular doubly linked list of books.
     */
    private class Node {
        Book book;
        Node prev;
        Node next;

        /**
         * Constructs a new instance of the Node class with the specified book.
         * 
         * @param book The book object to be stored in this node.
         */
        public Node(Book book) {
            this.book = book;
        }
    }

    
    /**
     * Adds a new book to the list in sorted order by title.
     * 
     * @param myBook The book to be added to the list.
     */
    public void insertSorted(Book myBook) {
        Node newNode = new Node(myBook);
        if (head == null) { // If the list is empty
            head = tail = newNode;
            newNode.next = newNode.prev = newNode; // Circular link
        } else if (myBook.getTitle().compareTo(head.book.getTitle()) <= 0) { // Insert before head
            newNode.next = head;
            newNode.prev = head.prev;
            head.prev.next = newNode;
            head.prev = newNode;
            head = newNode; // Update head
        } else if (myBook.getTitle().compareTo(tail.book.getTitle()) >= 0) {
            newNode.next = head; // Point to the head to maintain circular nature
            newNode.prev = tail;
            tail.next = newNode;
            head.prev = newNode; // Update head's previous to new node
            tail = newNode; // Update tail to the new node
        } else { // Insert in the middle
            Node current = head;
            while (current.next != head && myBook.getTitle().compareTo(current.next.book.getTitle()) > 0) {
                current = current.next;
            }
            newNode.next = current.next;
            newNode.prev = current;
            current.next.prev = newNode;
            current.next = newNode;
        }
    }

    
    /**
     * Modifies the price and quantity of a book in the list.
     * 
     * @param title    The title of the book to be modified.
     * @param price    The new price of the book.
     * @param quantity The new quantity of the book.
     * @return true if the book is found and modified, false otherwise.
     */
    public boolean modifyBook(String title, double price, int quantity) {
        if (head == null) { // If there are no books
            return false;
        }
        Node current = head;
        do {
            if (current.book.getTitle().equals(title)) { // If the title matches
                current.book.setPrice(price); // Set the new price
                current.book.setQuantity(quantity); // Set the new quantity
                return true; // Return success message
            }
            current = current.next;
        } while (current != head); // Continue until we reach the head again
        return false; // If the book is not found
    }

    
    /**
     * Buys a specified quantity of a book from the list.
     * 
     * @param title    The title of the book to be bought.
     * @param quantity The quantity of the book to be bought.
     * @return true if the book is found and bought, false otherwise.
     */
    public boolean buyBook(String title, int quantity) {
        if (head == null) { // If there are no books
            return false;
        }
        Node current = head;
        do {
            if (current.book.getTitle().equals(title)) { // If the title matches
                if (current.book.getQuantity() >= quantity) { // If there are enough books in stock
                    current.book.setQuantity(current.book.getQuantity() - quantity); // Update the quantity
                    return true; // Return success message
                } else {
                    return false; // Return failure message
                }
            }
            current = current.next;
        } while (current != head); // Continue until we reach the head again
        return false; // If the book is not found
    }

    
    /**
     * Retrieves the details of a book from the list.
     * 
     * @param title The title of the book to be retrieved.
     * @return the details of the book if found, "Book not found." otherwise.
     */
    public String getBook(String title) {
        if (head == null) { // If there are no books
            return "Book not found.";
        }
        Node current = head;
        do {
            if (current.book.getTitle().equals(title)) { // If the title matches
                return current.book.toString(); // Return the book info
            }
            current = current.next;
        } while (current != head); // Continue until we reach the head again
        return "Book not found."; // If the book is not found
    }

    
    /**
     * Retrieves the details of all books from the list.
     * 
     * @return the details of all books in the list.
     */
    public String getAllBooks() {
        if (head == null) { // If there are no books
            return "No books in the list.";
        }
        StringBuilder sb = new StringBuilder();
        Node current = head;
        do {
            // Append book details followed by two newlines for spacing
            sb.append(current.book.toString()).append("\n");
            current = current.next;
        } while (current != head); // Continue until we reach the head again
        return sb.toString().trim(); // Trim to remove the last extra newline
    }

    
    /**
     * Retrieves the details of all books from the list.
     * 
     * @return the details of all books in the list.
     */
    public String toString() {
        if (head == null) { // If there are no books
            return "No books in the list.";
        }
        StringBuilder sb = new StringBuilder();
        Node current = head;
        do {
            // Append book details followed by two newlines for spacing
            sb.append(current.book.toString()).append("\n");
            current = current.next;
        } while (current != head); // Continue until we reach the head again
        return sb.toString().trim(); // Trim to remove the last extra newline
    }

    
    /**
     * Retrieves the details of all books from the list in a short format.
     * 
     * @return the details of all books in the list in a short format.
     */
    public String getAllBooksShort() {
        if (head == null) { // If there are no books
            return "No books in the list.";
        }
        StringBuilder sb = new StringBuilder();
        Node current = head;
        do {
            // Append book details followed by two newlines for spacing
            sb.append(current.book.getTitle()).append(" (").append(current.book.getYear()).append(") by ").append(current.book.getAuthors()).append("\n");
            current = current.next;
        } while (current != head); // Continue until we reach the head again
        return sb.toString().trim(); // Trim to remove the last extra newline
    }
    public static void main(String[] args) {
        // Create a new instance of BookList
        BookList bookList = new BookList();
    
        // Create books and their author lists, then add them to the BookList
        AuthorList authors1 = new AuthorList();
        authors1.addAuthor(new Author("Frank", "Herbert"));
        Book book1 = new Book("Dune", "A desert planet with valuable resource", "1965", 5, 9.99, authors1);
    
        AuthorList authors2 = new AuthorList();
        authors2.addAuthor(new Author("William", "Gibson"));
        Book book2 = new Book("Neuromancer", "A tale of cyberpunk future", "1984", 10, 15.99, authors2);
    
        AuthorList authors3 = new AuthorList();
        authors3.addAuthor(new Author("J.R.R.", "Tolkien"));
        Book book3 = new Book("The Hobbit", "A journey there and back again", "1937", 7, 8.99, authors3);
    
        AuthorList authors4 = new AuthorList();
        authors4.addAuthor(new Author("George", "Orwell"));
        Book book4 = new Book("1984", "A dystopian future society", "1949", 8, 6.99, authors4);
    
        AuthorList authors5 = new AuthorList();
        authors5.addAuthor(new Author("Aldous", "Huxley"));
        Book book5 = new Book("Brave New World", "A dystopian novel about a genetically modified society", "1932", 6, 7.99, authors5);
    
        // Insert books into the book list
        bookList.insertSorted(book1);
        bookList.insertSorted(book2);
        bookList.insertSorted(book3);
        bookList.insertSorted(book4);
        bookList.insertSorted(book5);
    
        // Array of book titles to retrieve
        String[] titles = {"Dune", "Neuromancer", "The Hobbit", "1984", "Brave New World", "Moby Dick"}; // Include a title that doesn't exist
    
        // Retrieve and display details of each book by title
        for (String title : titles) {
            System.out.println("Retrieving book by title '" + title + "':");
            String result = bookList.getBook(title);
            System.out.println(result);
            System.out.println(); // Add a newline for better separation
        }
    }
    
    
}
