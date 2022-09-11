package kz.attractor.java.lesson44;

public class Book {
    private int id;
    private String name;
    private String authorName;
    private String authorSurname;
    private int year;
    private boolean sued;
    private Employee toWhomIssued;
    private String pictureName;

    public Book(String name, String authorName, String authorSurname, int year, boolean sued) {
        this.name = name;
        this.authorName = authorName;
        this.authorSurname = authorSurname;
        this.year = year;
        this.sued = sued;
    }

    public Book(int id, String name, String authorName, String authorSurname, int year) {
        this.id = id;
        this.name = name;
        this.authorName = authorName;
        this.authorSurname = authorSurname;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorSurname() {
        return authorSurname;
    }

    public void setAuthorSurname(String authorSurname) {
        this.authorSurname = authorSurname;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isSued() {
        return sued;
    }

    public void setSued(boolean sued) {
        this.sued = sued;
    }

    public Employee getToWhomIssued() {
        return toWhomIssued;
    }

    public void setToWhomIssued(Employee toWhomIssued) {
        this.toWhomIssued = toWhomIssued;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

}
