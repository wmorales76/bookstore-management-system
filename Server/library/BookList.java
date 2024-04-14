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
        AuthorList author;
        Node prev;
        Node next;

        public Node(Book book, AuthorList author) {
            this.book = book;
            this.author = author;
        }
    }

    // Method to insert a new book in sorted order based on the title
    public void insertSorted(Book myBook, AuthorList author) {
        Node newNode = new Node(myBook, author);
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

    // Method to display the list starting from the head
    public void display() {
        if (head != null) {
            Node current = head;
            do {
                current.book.display(); // Use the Book's display method to show details
                current.author.displayAuthors(); // Use the AuthorList's displayAuthors method to show authors
                current = current.next;
            } while (current != head);
        }
    }

    public static void main(String[] args) {
        //create authors for my book
        Author author1 = new Author("John", "Soe");
        Author author2 = new Author("Jane", "Dmith");
        AuthorList authorList = new AuthorList();

        //create a list of authors
        authorList.addAuthor(author1);
        authorList.addAuthor(author2);

        //create a book
        Book book1 = new Book("Java Programming", "plot","2020", 10, 29.99);
        BookList list = new BookList();
        list.insertSorted(book1, authorList);
        list.display();

    }


}
