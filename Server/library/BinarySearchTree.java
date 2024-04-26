package library;

import java.util.Scanner;

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
    // INSERTING OR MODIFYING METHODS

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

    // insert a genre only
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

    public boolean addBooktoBST(String genre, String title, String plot, String[] authors, String year, double price,
            int quantity) {
        AuthorList authorList = new AuthorList();
        for (String author : authors) {
            String[] name = author.split(" ");
            Author newAuthor = new Author(name[0], name[1]);
            authorList.addAuthor(newAuthor);
        }
        Book newBook = new Book(title, plot, year, quantity, price, authorList);

        Node genreNode = search(root, genre);
        boolean result;
        if (genreNode == null) {
            // Create a new BookList and add the book to it if the genre does not exist
            BookList bookList = new BookList();
            bookList.insertSorted(newBook);
            insert(genre, bookList);
            result = true;
        } else {
            // If the genre already exists, add the book to the existing book list
            genreNode.bookList.insertSorted(newBook);
            result = true;
        }

        return result;
    }

    // modify book by title, price and quantity
    public boolean modifyBook(String title, double price, int quantity) {
        return modifyBookRec(root, title, price, quantity);
    }

    // Recursive method to modify a book by title, price, and quantity
    public boolean modifyBookRec(Node root, String title, double price, int quantity) {
        if (root != null) {
            if (root.bookList.modifyBook(title, price, quantity)) {
                return true;
            }
            if (modifyBookRec(root.left, title, price, quantity)) {
                return true;
            }
            if (modifyBookRec(root.right, title, price, quantity)) {
                return true;
            }
        }
        return false;
    }

    // buy a book, simply modify the quantity, price stays the same as it was before
    public boolean buyBook(String title, int quantity) {
        return buyBookRec(root, title, quantity);
    }

    // Recursive method to buy a book by title and quantity
    public boolean buyBookRec(Node root, String title, int quantity) {
        if (root != null) {
            if (root.bookList.buyBook(title, quantity)) {
                return true;
            }
            if (buyBookRec(root.left, title, quantity)) {
                return true;
            }
            if (buyBookRec(root.right, title, quantity)) {
                return true;
            }
        }
        return false;
    }

    // METHODS TO RETRIEVE INFORMATION FROM THE TREE
    // chec if a genre exist, return true
    public boolean checkGenre(String genre) {
        return checkGenreRec(root, genre);
    }

    // Recursive method to check if a genre exists in the binary search tree
    public boolean checkGenreRec(Node root, String genre) {
        if (root != null) {
            if (root.genre.equals(genre)) {
                return true;
            }
            if (checkGenreRec(root.left, genre)) {
                return true;
            }
            if (checkGenreRec(root.right, genre)) {
                return true;
            }
        }
        return false;
    }

    // get genres only
    public String getGenres() {
        if (root == null) {
            return "No genres found";
        }
        return getGenresRec(root);
    }

    // Recursive method to get genres from the binary search tree
    public String getGenresRec(Node root) {
        String genres = "";
        if (root != null) {
            genres += getGenresRec(root.left); // Traverse left subtree
            genres += root.genre + " "; // Add current node's genre
            genres += getGenresRec(root.right); // Traverse right subtree
        }
        return genres;
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

    // get all books
    public String getBooks() {
        if (root == null) {
            return "No books found";
        }
        return getBooksRec(root);

    }

    // Recursive method to get all books from the binary search tree
    public String getBooksRec(Node root) {
        String books = "";
        if (root != null) {
            books += getBooksRec(root.left); // Traverse left subtree
            books += "\n\n" + root.genre + ":\n"; // Add current node's genre
            books += root.bookList.getAllBooks(); // Add current node's book list
            books += getBooksRec(root.right); // Traverse right subtree
        }
        return books;
    }

    // get books by genre
    public String getBooksByGenre(String genre) {
        Node genreNode = search(root, genre);
        if (genreNode == null) {
            return "Genre not found.";
        }
        return genreNode.bookList.getAllBooks();

    }

    // Get book by title
    public String getBookByTitle(String title) {
        return getBookByTitleRec(root, title);
    }

    // Recursive method to get a book by title from the binary search tree
    public String getBookByTitleRec(Node root, String title) {
        // Check if the current node is null
        if (root == null) {
            return "Book not found.";
        }

        // Check if the book list at the current node is not null
        if (root.bookList != null) {
            String book = root.bookList.getBook(title);
            // If the book is found at the current node, return it
            if (book != null) {
                return book;
            }
        }

        // Recurse on the left child
        String leftBook = getBookByTitleRec(root.left, title);
        if (leftBook != null && !leftBook.equals("Book not found.")) {
            return leftBook;
        }

        // Recurse on the right child
        String rightBook = getBookByTitleRec(root.right, title);
        if (rightBook != null && !rightBook.equals("Book not found.")) {
            return rightBook;
        }

        // If the book is not found in any nodes, return "Book not found."
        return "Book not found.";
    }
}