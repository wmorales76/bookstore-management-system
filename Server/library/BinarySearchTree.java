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
            genres += root.genre+ " "; // Add current node's genre
            genres += getGenresRec(root.right); // Traverse right subtree
        }
        return genres;
    }


    
    public void addBooktoBST(String genre, String title, String plot, String[] authors, String year, double price, int quantity){
        AuthorList authorList = new AuthorList();
        for (String author : authors) {
            String[] name = author.split(" ");
            Author newAuthor = new Author(name[0], name[1]);
            authorList.addAuthor(newAuthor);
        }
        Book newBook = new Book(title, plot, year, quantity, price, authorList);
    
        Node genreNode = search(root, genre);
        if (genreNode == null) {
            // Create a new BookList and add the book to it if the genre does not exist
            BookList bookList = new BookList();
            bookList.insertSorted(newBook);
            insert(genre, bookList);
        } else {
            // If the genre already exists, add the book to the existing book list
            genreNode.bookList.insertSorted(newBook);
        }
    }
    

    //get all books
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
            books += "\n\n"+ root.genre + ":\n"; // Add current node's genre
            books += root.bookList.getAllBooks(); // Add current node's book list
            books += getBooksRec(root.right); // Traverse right subtree
        }
        return books;
    }

    //get book by title
    public String getBookByTitle(String title) {
        return getBookByTitleRec(root, title);

    }

    // Recursive method to get a book by title from the binary search tree
    public String getBookByTitleRec(Node root, String title) {
        if (root != null) {
            String book = root.bookList.getBook(title);
            if (book != null) {
                return book;
            }
            String leftBook = getBookByTitleRec(root.left, title);
            if (leftBook != null) {
                return leftBook;
            }
            String rightBook = getBookByTitleRec(root.right, title);
            if (rightBook != null) {
                return rightBook;
            }
        }
        return null;
    }

    //modify book by title, price and quantity
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

    //buy a book, simply modify the quantity, price stays the same as it was before
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


    //get books by genre
    public String getBooksByGenre(String genre) {
        Node genreNode = search(root, genre);
        if (genreNode == null) {
            return "Genre not found.";
        }
        return genreNode.bookList.getAllBooks();

    }


      //main
      public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        do {
            System.out.println("1. Add a genre");
            System.out.println("2. Add a book to a genre");
            System.out.println("3. List all genres");
            System.out.println("4. List all books");
            System.out.println("5. Search for a book");
            System.out.println("6. Buy a book");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    System.out.print("Enter the genre: ");
                    String genre = scanner.nextLine();
                    bst.insertGenre(genre);
                    break;
                case 2:
                    System.out.print("Enter the genre: ");
                    genre = scanner.nextLine();
                    System.out.print("Enter the title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter the plot: ");
                    String plot = scanner.nextLine();
                    System.out.print("Enter the author(s) (separated by commas): ");
                    String[] authors = scanner.nextLine().split(",");
                    System.out.print("Enter the year: ");
                    String year = scanner.nextLine();
                    System.out.print("Enter the price: ");
                    double price = scanner.nextDouble();
                    System.out.print("Enter the quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();
                    bst.addBooktoBST(genre, title, plot, authors, year, price, quantity);
                    break;
                case 3:
                    System.out.println(bst.getGenres());
                    break;
                case 4:
                    System.out.println(bst.getBooks());
                    break;
                case 5:
                    System.out.print("Enter the genre: ");
                    genre = scanner.nextLine();
                    System.out.print("Enter the title: ");
                    title = scanner.nextLine();
                    System.out.println(bst.getBookByTitle(title));
                    break;
                case 7:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
        scanner.close();
    }
  
}
