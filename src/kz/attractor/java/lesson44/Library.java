package kz.attractor.java.lesson44;

import java.util.ArrayList;
import java.util.List;


public class Library {
    private List<Book> booksList = new ArrayList<>();
    private List<Employee> employeesList = new ArrayList<>();
    private List<Entry> logbook = new ArrayList<>();
    private String userId;

    public List<Book> getBooksList() {
        return booksList;
    }

    public void setBooksList(List<Book> booksList) {
        this.booksList = booksList;
    }

    public List<Employee> getEmployeesList() {
        return employeesList;
    }

    public void setEmployeesList(List<Employee> employeesList) {
        this.employeesList = employeesList;
    }

    public List<Entry> getLogbook() {
        return logbook;
    }

    public void setLogbook(List<Entry> logbook) {
        this.logbook = logbook;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
