package kz.attractor.java.lesson44;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class WorkingLibrary {
    private Library library;
    private boolean isAuthorized;
    private String userId;


    public WorkingLibrary(String fileName) {
        readJson(fileName);
    }

    public void makeAndSave(String fileName, Library workingLibrary) {
        var filePath = Path.of("data", fileName);
        var json = new Gson().toJson(workingLibrary);
        try {
            Files.writeString(filePath, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readJson(String fileName){
        var filePath = Path.of("data", fileName);
        Gson gson = new Gson();
        try {
            library = gson.fromJson(Files.readString(filePath), Library.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
