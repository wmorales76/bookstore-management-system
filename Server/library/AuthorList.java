package library;

public class AuthorList {
    private AuthorNode head; // Head node of the linked list

    // Nested private class for list nodes
    private class AuthorNode {
        Author author; // The author object stored in this node
        AuthorNode next; // Reference to the next node in the list

        // Node constructor
        public AuthorNode(Author author) {
            this.author = author;
            this.next = null;
        }
    }

    // Constructor for AuthorList
    public AuthorList() {
        this.head = null;
    }

    // Method to add an author to the list in sorted order by last name
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

    // Method to display all authors in the list
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

    //convert all authors in node to string
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
