package library;

public class Author {
    private String firstName;
    private String lastName;

    // Constructor to create a new author with first and last names
    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }


    // Getter for the first name
    public String getFirstName() {
        return firstName;
    }

    // Getter for the last name
    public String getLastName() {
        return lastName;
    }

    // Setter for the first name
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Setter for the last name
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Method to display author's full name
    public String toString() {
        return lastName + " " + firstName;
    }
}
