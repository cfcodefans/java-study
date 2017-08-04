package cf.study.java.javax.persistence.jdo.tutorial;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class Book extends Product {
    String author = null;
    String isbn = null;
    String publisher = null;

    public Book(String name, String description, double price, String author, String isbn, String publisher) {
        super(name, description, price);
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
    }
}
