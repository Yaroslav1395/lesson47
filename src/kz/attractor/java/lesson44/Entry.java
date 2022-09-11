package kz.attractor.java.lesson44;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Entry {
    private String tookTheBook;
    private String returnedTheBook;
    private Book book;
    private Employee employee;

    public Entry(String tookTheBook, String returnedTheBook, Book book, Employee employee) {
        this.tookTheBook = tookTheBook;
        this.returnedTheBook = returnedTheBook;
        this.book = book;
        this.employee = employee;
    }

    public String getTookTheBook() {
        return tookTheBook;
    }

    public void setTookTheBook(String tookTheBook) {
        this.tookTheBook = tookTheBook;
    }

    public String getReturnedTheBook() {
        return returnedTheBook;
    }

    public void setReturnedTheBook(String returnedTheBook) {
        this.returnedTheBook = returnedTheBook;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
