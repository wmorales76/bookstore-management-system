package library;
import java.util.Scanner;
import library.BookList;
import library.Book;
import library.AuthorList;
import library.Author;



public class BinarySearchTree {
    
    private class Node {
        String genre;
        BookList bookList;
        Node left;
        Node right;
    
        // Constructor to initialize a node with a genre
        public Node(String genre, BookList bookList) {
            this.genre = genre;
            this.bookList = bookList;
            left = right = null;
        }
    }

    Node root;

    // Constructor to initialize an empty binary search tree
    public BinarySearchTree() {
        root = null;
    }

    // Method to insert a new genre into the binary search tree
    public void insert(String genre, BookList bookList) {
        root = insertRec(root, genre, bookList);
    }

    // Recursive method to insert a new genre into the binary search tree
    public Node insertRec(Node root, String genre, BookList bookList) {
        // If the tree is empty, create a new node as the root
        if (root == null) {
            root = new Node(genre, bookList);
            return root;
        }

        // If the genre comes before the root genre, insert in the left subtree
        if (genre.compareTo(root.genre) < 0) {
            root.left = insertRec(root.left, genre, bookList);
        }
        // If the genre comes after the root genre, insert in the right subtree
        else if (genre.compareTo(root.genre) > 0) {
            root.right = insertRec(root.right, genre, bookList);
        }

        return root;
    }

    // Method to perform inorder traversal of the binary search tree
    public void inorder() {
        inorderRec(root);
    }

    // Recursive method to perform inorder traversal of the binary search tree
    public void inorderRec(Node root) {
        if (root != null) {
            inorderRec(root.left); // Traverse left subtree
            System.out.print("------------------------------------\n"); 
            System.out.print(root.genre+"\n"); // Print current node's genre
            System.out.print("------------------------------------\n\n");
            root.bookList.display(); // Display the book list for the current genre
            inorderRec(root.right); // Traverse right subtree
        }
    }

    // Method to search for a genre in the binary search tree
    public Node search(Node root, String genre) {
        // If the tree is empty or the root node has the desired genre, return root
        if (root == null || root.genre.equals(genre)) {
            return root;
        }

        // If the desired genre comes before the root genre, search in the left subtree
        if (genre.compareTo(root.genre) < 0) {
            return search(root.left, genre);
        } else { // Otherwise, search in the right subtree
            return search(root.right, genre);
        }

    }

    // Method to delete a genre from the binary search tree
    public void delete(String genre) {
        root = deleteRec(root, genre);
    }

    // Recursive method to delete a genre from the binary search tree
    public Node deleteRec(Node root, String genre) {
        // If the tree is empty, return null
        if (root == null) {
            return root;
        }

        // If the desired genre comes before the root genre, delete from the left subtree
        if (genre.compareTo(root.genre) < 0) {
            root.left = deleteRec(root.left, genre);
        }
        // If the desired genre comes after the root genre, delete from the right subtree
        else if (genre.compareTo(root.genre) > 0) {
            root.right = deleteRec(root.right, genre);
        }
        // If the root genre is the desired genre
        else {
            // If the root has only one child or no child
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }

            // If the root has two children, get the inorder successor
            root.genre = minValue(root.right);

            // Delete the inorder successor
            root.right = deleteRec(root.right, root.genre);
        }

        return root;
    }

    //insert a genre only
    public void insertGenre(String genre) {
        root = insertGenreRec(root, genre);
    }

    // Recursive method to insert a new genre into the binary search tree
    public Node insertGenreRec(Node root, String genre) {
        // If the tree is empty, create a new node as the root
        if (root == null) {
            root = new Node(genre, null);
            return root;
        }

        // If the genre comes before the root genre, insert in the left subtree
        if (genre.compareTo(root.genre) < 0) {
            root.left = insertGenreRec(root.left, genre);
        }
        // If the genre comes after the root genre, insert in the right subtree
        else if (genre.compareTo(root.genre) > 0) {
            root.right = insertGenreRec(root.right, genre);
        }

        return root;
    }

    // Method to find the inorder successor of a node
    public String minValue(Node root) {
        String minv = root.genre;
        while (root.left != null) {
            minv = root.left.genre;
            root = root.left;
        }
        return minv;
    }

    //get genres only
    public String getGenres() {
        return getGenresRec(root);
    }

    // Recursive method to get genres from the binary search tree
    public String getGenresRec(Node root) {
        String genres = "";
        if (root != null) {
            genres += getGenresRec(root.left); // Traverse left subtree
            genres += root.genre+ " "; // Add current node's genre
            genres += getGenresRec(root.right); // Traverse right subtree
        }
        return genres;
    }



  
    // Main method to test the binary search tree
    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree();
        AuthorList authorList = new AuthorList();
        AuthorList authorList2 = new AuthorList();
        AuthorList authorList3 = new AuthorList();
        AuthorList authorList4 = new AuthorList();


        BookList bookList = new BookList();

        // Insert books into the list
        Book book1 = new Book("The Great Gatsby", "A novel about the American Dream", "1925", 5, 9.99);
        Book book2 = new Book("To Kill a Mockingbird", "A novel about racial injustice", "1960", 3, 7.99);
        Book book3 = new Book("1984", "A novel about totalitarianism", "1949", 4, 8.99);
        Book book4 = new Book("Pride and Prejudice", "A novel about love and class", "1813", 6, 10.99);

        // Insert authors into the list
        Author author1 = new Author("F. Scott", "Fitzgerald");
        Author author2 = new Author("Harper", "Lee");
        Author author3 = new Author("George", "Orwell");
        Author author4 = new Author("Jane", "Austen");


        // Add authors to the author list
        authorList.addAuthor(author1);
        authorList.addAuthor(author2);

        authorList2.addAuthor(author2);
        authorList2.addAuthor(author3);

        authorList3.addAuthor(author3);
        authorList3.addAuthor(author4);

        authorList4.addAuthor(author4);
        authorList4.addAuthor(author1);

        // Insert books into the list
        bookList.insertSorted(book1, authorList);
        bookList.insertSorted(book2, authorList2);
        bookList.insertSorted(book3, authorList3);
        bookList.insertSorted(book4, authorList4);

        //insert the genre and the booklist into the tree
        tree.insert("Fiction", bookList);
        tree.insert("Non-Fiction", bookList);
        tree.insert("Science Fiction", bookList);
        

        // Perform inorder traversal to print genres in sorted order
        tree.inorder();

        // Search for a genre in the tree
        //give the user an input option 
        System.out.println("\nSearch for a genre in the tree");
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the genre you want to search for: ");
        String genre = sc.nextLine();
        Node result = tree.search(tree.root, genre);
        if (result != null) {
            System.out.println("Genre found: " + result.genre);
        } else {
            System.out.println("Genre not found");
        }
    
        //delete a genre from the tree
        System.out.println("\nDelete a genre from the tree");
        System.out.println("Enter the genre you want to delete: ");
        genre = sc.nextLine();
        tree.delete(genre);
        tree.inorder();
        sc.close();

        // Perform inorder traversal to print genres in sorted order
        tree.inorder();

    }
}
