# bookstore-management-system
 
Bookstore Management System

Welcome to the Bookstore Management System, a Java-based application designed to streamline bookstore management with a focus on efficient data organization and user-friendly interactions. Developed as a comprehensive solution, this system leverages binary search trees for genre categorization and incorporates multi-threading for robust data handling. It's built with precision to cater to the dynamic needs of bookstore operations.
Features

    Add Genre: Seamlessly introduce new genres to the database to expand the bookstore's catalog.
    Add Book: Enter detailed information for each book, including title, genre, plot, authors, release year, price, and stock quantity.
    Modify Book: Update the price and stock quantity of existing books with ease.
    List Genres: View all available genres in alphabetical order for easy navigation.
    List Books by Genre: Discover books within specific genres, organized alphabetically by title, and view key details such as release year, authors, quantity, and price.
    Search for Book: Locate books with precision using title searches, enhancing customer service by quickly finding what you're looking for.
    Buy Book: Complete purchases within the system, which automatically updates stock quantities.

Architecture

The system architecture is designed with efficiency and scalability in mind:

    Genres Binary Search Tree: Genres are organized in a binary search tree, sorted by title for quick access.
    Double Circular List for Books: Each genre node links to a sorted double circular list containing books, ensuring organized and efficient data retrieval.
    Authors Singly Linked List: Books' authors are maintained in a singly linked list, ordered by the last name, facilitating straightforward author management.
    Multi-Threaded Server: The back-end server uses multi-threading to handle concurrent requests, ensuring data consistency and real-time updates across clients.
    Client-Server Model: Utilizes sockets for communication between the client interface and the server, maintaining a seamless and responsive user experience.