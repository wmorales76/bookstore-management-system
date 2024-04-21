package library;


public class Book {
    String title, plot,year;
    int quantity;
    double price;
    
    //constructor for the class
    public Book(String title, String plot,String year, int quantity, double price){
        this.title = title;
        this.plot = plot;
        this.year = year;
        this.quantity = quantity;
        this.price = price;
    }

    //get title
    public String getTitle(){
        return title;
    }

    //get year
    public String getYear(){
        return year;
    }

    //get author
    public Double getPrice(){
        return price;
    }

    //get quantity
    public int getQuantity(){
        return quantity;
    }

    //set title
    public void setTitle(String title){
        this.title = title;
    }

    //set year
    public void setYear(String year){
        this.year = year;
    }


    //set quantity
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    //set price 
    public void setPrice(double price){
        this.price = price;
    }

    //create a book
    public static Book createBook(String title, String plot, String year, int quantity, double price){
        return new Book(title, plot, year, quantity, price);
    }

    //method to display the book details
    public void display(){
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
