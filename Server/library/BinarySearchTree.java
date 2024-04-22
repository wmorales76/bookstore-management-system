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

        // If the genre already exists, update the book list
        if (genre.equals(root.genre)) {
            root.bookList = bookList;
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

    public void addBooktoBST(String genre, String title, String plot, String[] authors, String year, double price, int quantity){
        BookList bookList = new BookList();
        AuthorList authorList = new AuthorList();
 
        for (String author : authors) {
            String[] name = author.split(" ");
            Author newAuthor = new Author(name[0], name[1]);
            authorList.addAuthor(newAuthor);
        }
        Book newBook = new Book(title, plot, year, quantity, price, authorList);
        bookList.insertSorted(newBook);
        insert(genre, bookList);
    }

    //getbookbytitle
    public String getBookByTitle(String genre, String title) {
        Node genreNode = search(root, genre);
        if (genreNode != null) {
            Book book = genreNode.bookList.getBook(title);
            
            if (book != null) {
                return book.toString();
            }
        }
        return null;
    }
  
  
    // Main method to test the binary search tree
    public static void main(String[] args) {
    }
}
