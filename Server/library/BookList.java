package library;

import library.*;

public class BookList {
    private Node head;
    private Node tail;

    // constructor
    public BookList() {
        head = null;
        tail = null;
    }

    private class Node {
        Book book;
        Node prev;
        Node next;

        public Node(Book book) {
            this.book = book;
        }
    }

    // Method to insert a new book in sorted order based on the title
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

    // getBook info in a string for a specific book
    public String getBook(String title) {
        if (head == null) { // If there are no books
            return "No books in the list.";
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

    // Get all book info in a string for all books in the list, formatted with extra
    // line spaces
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

    //modify book by title price and quantity
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

    //method to buy a book
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


    // Convert BookList to a string, formatted with extra line spaces
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

    public static void main(String[] args) {

        // create author list
        AuthorList al = new AuthorList();
        al.addAuthor(new Author("F. Scott", "Fitzgerald"));
        al.addAuthor(new Author("Harper", "Lee"));

        // test the get all books method
        BookList bl = new BookList();
        bl.insertSorted(new Book("The Great Gatsby", "F. Scott Fitzgerald", "Classic", 1925, 7.99, al));
        bl.insertSorted(new Book("To Kill a Mockingbird", "Harper Lee", "Classic", 1960, 6.99, al));
        bl.insertSorted(new Book("1984", "George Orwell", "Dystopian", 1949, 8.99, al));
        bl.insertSorted(new Book("Brave New World", "Aldous Huxley", "Dystopian", 1932, 9.99, al));

        System.out.println(bl.getAllBooks());

    }

}
