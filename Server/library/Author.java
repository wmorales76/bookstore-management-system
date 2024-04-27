package library;

/**
 * The Author class represents an author of a book.
 */
public class Author {
    private String firstName;
    private String lastName;

    /**
     * Constructor to create a new author with first and last names.
     *
     * @param firstName the first name of the author
     * @param lastName  the last name of the author
     */
    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Returns the first name of the author.
     *
     * @return the first name of the author
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the author.
     *
     * @return the last name of the author
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the first name of the author.
     *
     * @param firstName the first name of the author
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the last name of the author.
     *
     * @param lastName the last name of the author
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Creates a new author with the given first and last names.
     *
     * @param firstName the first name of the author
     * @param lastName  the last name of the author
     * @return a new author with the given first and last names
     */
    public static Author createAuthor(String firstName, String lastName) {
        return new Author(firstName, lastName);
    }

    /**
     * Returns the full name of the author.
     *
     * @return the full name of the author
     */
    public String toString() {
        return lastName + " " + firstName;
    }
}
