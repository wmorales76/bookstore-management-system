package library;

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

    /**
     * Represents a binary search tree data structure.
     */
    public BinarySearchTree() {
        root = null;
    }
    // INSERTING OR MODIFYING METHODS

    /**
     * Inserts a new genre and its corresponding book list into the binary search
     * tree.
     *
     * @param genre    the genre of the book list to be inserted
     * @param bookList the book list to be inserted
     */
    public void insert(String genre, BookList bookList) {
        root = insertRec(root, genre, bookList);
    }

    /**
     * Recursively inserts a new genre and its corresponding book list into the
     * binary search tree.
     *
     * @param root     the root node of the binary search tree
     * @param genre    the genre of the book list to be inserted
     * @param bookList the book list to be inserted
     * @return the root node of the binary search tree
     */
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

    /**
     * Inserts a new genre into the binary search tree.
     * 
     * @param genre the genre to be inserted
     * @return void
     */
    public void insertGenre(String genre) {
        root = insertGenreRec(root, genre);
    }

    /**
     * Recursively inserts a new genre into the binary search tree.
     * 
     * @param root  the root node of the binary search tree
     * @param genre the genre to be inserted
     * @return the root node of the binary search tree
     */
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

    /**
     * Adds a new book to the binary search tree.
     *
     * @param genre    the genre of the book to be added
     * @param title    the title of the book to be added
     * @param plot     the plot of the book to be added
     * @param authors  the authors of the book to be added
     * @param year     the year of the book to be added
     * @param price    the price of the book to be added
     * @param quantity the quantity of the book to be added
     * @return true if the book is successfully added, false otherwise
     */
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
            // System.out.println("Genre '" + genre + "' not found, creating new genre.");
            // Create a new BookList and add the book to it if the genre does not exist
            BookList bookList = new BookList();
            bookList.insertSorted(newBook);
            insert(genre, bookList);
            result = true;
        } else {
            // System.out.println("Adding book to existing genre '" + genre + "'.");
            // If the genre already exists, create a new book list if it doesn't exist
            if (genreNode.bookList == null) {
                genreNode.bookList = new BookList();
            }
            genreNode.bookList.insertSorted(newBook);
            result = true;
        }

        return result;
    }

    /**
     * Modifies a book by title, price, and quantity in the binary search tree.
     * 
     * @param title    the title of the book to be modified
     * @param price    the new price of the book
     * @param quantity the new quantity of the book
     * @return true if the book is successfully modified, false otherwise
     */
    public boolean modifyBook(String title, double price, int quantity) {
        return modifyBookRec(root, title, price, quantity);
    }

    /**
     * Recursively modifies a book by title, price, and quantity in the binary
     * search tree.
     * 
     * @param root     the root node of the binary search tree
     * @param title    the title of the book to be modified
     * @param price    the new price of the book
     * @param quantity the new quantity of the book
     * @return true if the book is successfully modified, false otherwise
     */
    private boolean modifyBookRec(Node root, String title, double price, int quantity) {
        if (root == null) {
            return false;
        }

        // Try to modify the book in the current node
        if (root.bookList != null && root.bookList.modifyBook(title, price, quantity)) {
            return true;
        }

        // Recursively modify left subtree, and if found and modified, no need to
        // proceed further
        if (modifyBookRec(root.left, title, price, quantity)) {
            return true;
        }

        // Recursively modify right subtree
        return modifyBookRec(root.right, title, price, quantity);
    }

    /**
     * Buys a book by title and quantity in the binary search tree.
     * 
     * @param title    the title of the book to be bought
     * @param quantity the quantity of the book to be bought
     * @return true if the book is successfully bought, false otherwise
     */
    public boolean buyBook(String title, int quantity) {
        return buyBookRec(root, title, quantity);
    }

    /**
     * Recursively buys a book by title and quantity in the binary search tree.
     * 
     * @param root     the root node of the binary search tree
     * @param title    the title of the book to be bought
     * @param quantity the quantity of the book to be bought
     * @return true if the book is successfully bought, false otherwise
     */
    public boolean buyBookRec(Node root, String title, int quantity) {
        if (root == null) {
            // System.out.println("Reached a null node, book not found.");
            return false;
        }

        // System.out.println("Checking books in genre: " + root.genre);
        if (root.bookList != null && root.bookList.buyBook(title, quantity)) {
            // System.out.println("Book purchased in " + root.genre + ": " + title);
            return true; // Book found and quantity updated, no need to search further
        }

        // Recursively search in the left subtree first
        boolean leftResult = buyBookRec(root.left, title, quantity);
        if (leftResult) {
            return true; // Book found and bought in the left subtree, stop searching
        }

        // Only search the right subtree if the book was not found in the left subtree
        // System.out.println("Book not found or not enough quantity in " + root.genre +
        // " and left subtree, searching right.");
        return buyBookRec(root.right, title, quantity);
    }

    // METHODS TO RETRIEVE INFORMATION FROM THE TREE

    /**
     * Checks if a genre exists in the binary search tree.
     * 
     * @param genre the genre to be checked
     * @return true if the genre exists, false otherwise
     */
    public boolean checkGenre(String genre) {
        return checkGenreRec(root, genre);
    }

    /**
     * Recursively checks if a genre exists in the binary search tree.
     * 
     * @param root  the root node of the binary search tree
     * @param genre the genre to be checked
     * @return true if the genre exists, false otherwise
     */
    private boolean checkGenreRec(Node root, String genre) {
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

    /**
     * Gets all genres in the binary search tree.
     * 
     * @return a string containing all genres in the binary search tree
     */
    public String getGenres() {
        if (root == null) {
            return "No genres found";
        }
        return getGenresRec(root);
    }

    /**
     * Recursively gets all genres from the binary search tree.
     * 
     * @param root the root node of the binary search tree
     * @return a string containing all genres in the binary search tree, one genre
     *         per line
     */
    public String getGenresRec(Node root) {
        StringBuilder genres = new StringBuilder();
        if (root != null) {
            genres.append(getGenresRec(root.left)); // Traverse left subtree
            genres.append(root.genre).append("\n"); // Add current node's genre
            genres.append(getGenresRec(root.right)); // Traverse right subtree
        }
        return genres.toString();
    }

    /**
     * Searches for a genre in the binary search tree.
     * 
     * @param root  the root node of the binary search tree
     * @param genre the genre to be searched
     * @return the node containing the genre if found, null otherwise
     */
    public Node search(Node root, String genre) {
        if (root == null) {
            // System.out.println("Search - Genre '" + genre + "' not found, reached a null
            // node.");
            return null;
        }
        // System.out.println("Searching at Genre: '" + root.genre + "'");
        if (genre.equals(root.genre)) {
            // System.out.println("Search - Genre '" + genre + "' found.");
            return root;
        }

        if (genre.compareTo(root.genre) < 0) {
            // System.out.println("Genre '" + genre + "' is less than '" + root.genre + "',
            // searching left.");
            return search(root.left, genre);
        } else {
            // System.out.println("Genre '" + genre + "' is greater than '" + root.genre +
            // "', searching right.");
            return search(root.right, genre);
        }
    }

    /**
     * Gets all books in the binary search tree.
     * 
     * @return a string containing all books in the binary search tree
     */
    public String getBooks() {
        if (root == null) {
            return "No books found.";
        }
        StringBuilder result = new StringBuilder();
        getBooksRec(root, result);
        return result.toString();
    }

    /**
     * Recursively gets all books from the binary search tree.
     * 
     * @param root   the root node of the binary search tree
     * @param result the string builder to store the result
     */
    private void getBooksRec(Node root, StringBuilder result) {
        if (root != null) {
            getBooksRec(root.left, result); // Traverse left subtree
            result.append("\n\n").append("========================================"); // Add dividing lane
            result.append("\n").append(root.genre).append(":\n"); // Add current node's genre
            if (root.bookList != null && root.bookList.getAllBooks() != null) {
                result.append(root.bookList.getAllBooks()); // Add current node's book list
            } else {
                result.append("No books for this genre yet"); // Add "No books in the list" if book list is null
            }
            result.append("\n").append("========================================"); // Add dividing lane
            getBooksRec(root.right, result); // Traverse right subtree
        }
    }

    /**
     * Gets all books in a specific genre from the binary search tree.
     * 
     * @param genre the genre of the books to be retrieved
     * @return a string containing all books in the specified genre
     */
    public String getBooksByGenre(String genre) {
        Node genreNode = search(root, genre);
        if (genreNode != null) {
            if (genreNode.bookList != null) {
                return genreNode.bookList.getAllBooksShort();
            } else {
                return "No books found.";

            }
        } else {
            return "Genre not found.";
        }
    }

    /**
     * Gets a book by title from the binary search tree.
     * 
     * @param title the title of the book to be retrieved
     * @return a string containing the book with the specified title
     */
    public String getBookByTitle(String title) {
        return getBookByTitleRec(root, title);
    }

    /**
     * Recursively gets a book by title from the binary search tree.
     * 
     * @param root  the root node of the binary search tree
     * @param title the title of the book to be retrieved
     * @return a string containing the book with the specified title
     */
    public String getBookByTitleRec(Node root, String title) {
        if (root == null) {
            return "Book not found.";
        }

        // System.out.println("Checking books in genre: " + root.genre);
        if (root.bookList != null && !root.bookList.getBook(title).equals("Book not found.")) {
            // System.out.println("Book found in " + root.genre + ": " + title);
            return root.bookList.getBook(title);
        }

        // System.out.println("Book not found in " + root.genre + ", searching left.");
        String leftResult = getBookByTitleRec(root.left, title);
        if (!leftResult.equals("Book not found.")) {
            return leftResult;
        }

        // System.out.println("Book not found in " + root.genre + ", searching right.");
        return getBookByTitleRec(root.right, title);
    }

}