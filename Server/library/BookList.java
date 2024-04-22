package library;

public class BookList {
    private Node head;
    private Node tail;

    //constructor
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
            newNode.next = head;  // Point to the head to maintain circular nature
            newNode.prev = tail;
            tail.next = newNode;
            head.prev = newNode;  // Update head's previous to new node
            tail = newNode;       // Update tail to the new node
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

    
    //getBook
    public Book getBook(String title) {
        Node current = head;
        while (current != null) {
            if (current.book.getTitle().equals(title)) {
                return current.book;
            }
            current = current.next;
        }
        return null;
    }

    //convert Book to string
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            sb.append(current.book.toString()).append("\n");
            current = current.next;
        }
        return sb.toString();
    }

    public static void main(String[] args) {

    }


}
