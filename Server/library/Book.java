package library;

public class Book {
    String title, plot, year;
    int quantity;
    double price;
    AuthorList author;

    /**
     * Constructs a new Book object with the specified details.
     *
     * @param title    the title of the book
     * @param plot     the plot of the book
     * @param year     the year of publication
     * @param quantity the quantity of books available
     * @param price    the price of the book
     * @param author   the author(s) of the book
     */
    public Book(String title, String plot, String year, int quantity, double price, AuthorList author) {
        this.title = title;
        this.plot = plot;
        this.year = year;
        this.quantity = quantity;
        this.price = price;
        this.author = author;
    }

    /**
     * Returns the title of the book.
     *
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the plot of the book.
     *
     * @return the plot of the book
     */
    public String getYear() {
        return year;
    }

    /**
     * Returns the price of the book.
     *
     * @return the price of the book
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Returns the quantity of the book.
     *
     * @return the quantity of the book
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Returns the author(s) of the book.
     *
     * @return the author(s) of the book
     */
    public AuthorList getAuthors() {
        return author;
    }

    /**
     * Sets the title of the book.
     *
     * @param title the title of the book
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the plot of the book.
     *
     * @param plot the plot of the book
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Sets the price of the book.
     *
     * @param price the price of the book
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the author(s) of the book.
     *
     * @param author the author(s) of the book
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Sets the author(s) of the book.
     *
     * @param author the author(s) of the book
     */
    public void setAuthors(AuthorList author) {
        this.author = author;
    }

    /**
     * Creates a new book with the given details.
     *
     * @param title    the title of the book
     * @param plot     the plot of the book
     * @param year     the year of publication
     * @param quantity the quantity of books available
     * @param price    the price of the book
     * @param author   the author(s) of the book
     * @return a new book with the given details
     */
    public static Book createBook(String title, String plot, String year, int quantity, double price,
            AuthorList author) {
        return new Book(title, plot, year, quantity, price, author);
    }

    /**
     * Returns a string representation of the book.
     *
     * @return a string representation of the book
     */
    public String toString() {
        return "\n\nTitle:" + title + "\nPlot: " + plot + "\nRelease Year: " + year + "\nQuantity: " + quantity
                + "\nPrice: " + price + "\nAuthor(s) " + author.toString() + "\n";
    }

    /**
     * Displays the details of the book.
     */
    public void display() {
        System.out.println("Book Details");
        System.out.println("--------------");
        System.out.println("Title: " + title);
        System.out.println("Plot: " + plot);
        System.out.println("Year: " + year);
        System.out.println("Quantity: " + quantity);
        System.out.println("Price: " + price);
        System.out.println("--------------");
    }

}
