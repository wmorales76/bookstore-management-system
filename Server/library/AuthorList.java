package library;

/**
 * The AuthorList class represents a linked list of authors.
 * It provides methods to add authors to the list in sorted order by last name,
 * display all authors in the list, and convert the list to a string representation.
 */
public class AuthorList {
    private AuthorNode head; // Head node of the linked list

    /**
     * Represents a node in the linked list of authors.
     */
    private class AuthorNode {
        Author author; // The author object stored in this node
        AuthorNode next; // Reference to the next node in the list

        /**
         * Constructs a new instance of the AuthorNode class with the specified author.
         * 
         * @param author The author object to be stored in this node.
         */
        public AuthorNode(Author author) {
            this.author = author;
            this.next = null;
        }
    }

    /**
     * Constructs a new instance of the AuthorList class with an empty list.
     */
    public AuthorList() {
        this.head = null;
    }

    /**
     * Adds a new author to the list in sorted order by last name.
     * 
     * @param author The author to be added to the list.
     */
    public void addAuthor(Author author) {
        AuthorNode newNode = new AuthorNode(author);
        if (head == null || author.getLastName().compareTo(head.author.getLastName()) < 0) {
            // Insert at the head if list is empty or new author's last name is
            // alphabetically first
            newNode.next = head;
            head = newNode;
        } else {
            // Find the correct position for new node
            AuthorNode current = head;
            while (current.next != null && current.next.author.getLastName().compareTo(author.getLastName()) < 0) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
    }

    /**
     * Displays all authors in the list.
     */
    public void displayAuthors() {
        AuthorNode current = head;
        while (current != null) {
            System.out.print(current.author);
            if (current.next != null) {
                System.out.print(", ");
            } else {
                System.out.println("\n");//jump 2 lines
            }
            current = current.next;
        }

    }

    /**
     * Returns a string representation of the list of authors.
     * 
     * @return A string representation of the list of authors.
     */
    public String toString(){
        String authors = "";
        AuthorNode current = head;
        while (current != null) {
            authors += current.author.toString();
            if (current.next != null) {
                authors += ", ";
            } else {
                authors += "\n";
            }
            current = current.next;
        }
        return authors;
    }
}
