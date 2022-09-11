package kz.attractor.java;

import com.google.gson.Gson;
import kz.attractor.java.lesson44.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            new Lesson47Server("localhost", 9879).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        WorkingLibrary library = new WorkingLibrary("library.json");

    }
}
