package kz.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import kz.attractor.java.server.ContentType;
import kz.attractor.java.server.ResponseCodes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Random;

public class Lesson45Server extends Lesson44Server {

    public Lesson45Server(String host, int port) throws IOException {
        super(host, port);
        //обработчик, который может обработать GET запрос
        registerGet("/login", this::loginGet);
        //обработчик, который может обработать POST запрос
        registerPost("/login", this::loginPost);
        registerGet("/registration", this::registrationGet);
        registerPost("/registration", this::registrationPost);
    }

    private void loginPost(HttpExchange exchange) {
        WorkingLibrary library = getLibraryModel();
        library.setAuthorized(false);
        String row = getBody(exchange);
        Map<String, String> parsed = Utils.parseUrlEncoded(row, "&");
        Random rnd = new Random();
        String numberId = String.valueOf(rnd.nextInt(9000) + 1000);
        for(int i = 0; i < library.getLibrary().getEmployeesList().size(); i++){
            if(library.getLibrary().getEmployeesList().get(i).getEmail().equals(parsed.get("email"))){
                library.setUserId(numberId);
                library.setAuthorized(true);
                library.getLibrary().getEmployeesList().get(i).setId(numberId);
                Cookie userId = Cookie.make("id", numberId);
                userId.setMaxAge(120);
                userId.setHttpOnly(true);
                setCookie(exchange, userId);
                library.makeAndSave("library.json", library.getLibrary());
            }
        }
        if(library.isAuthorized()){
            renderTemplate(exchange, "employee.html", library);
        }
        else {
            redirect303(exchange, "/failAuthorization.html");
        }
    }


    private void loginGet(HttpExchange exchange) {
        Path path = makeFilePath("login.html");
        WorkingLibrary library = getLibraryModel();
        String cookieString = getCookies(exchange);
        Map<String, String> cookies = Cookie.parse(cookieString);
        String cookiesValue = cookies.getOrDefault("id", "0");
        Employee employee = null;
        if(!cookiesValue.equals("0")){
            for(int i = 0; i < library.getLibrary().getEmployeesList().size(); i++){
                if(cookiesValue.equals(library.getLibrary().getEmployeesList().get(i).getId())){
                    employee = library.getLibrary().getEmployeesList().get(i);
                    library.setAuthorized(true);
                    library.setUserId(library.getLibrary().getEmployeesList().get(i).getId());
                }
            }
        }
        if(employee != null){
            renderTemplate(exchange, "employee.html", library);
        }
        else {
            sendFile(exchange, path, ContentType.TEXT_HTML);
        }
    }

    private void registrationGet(HttpExchange exchange){
        Path path = makeFilePath("registration.html");
        sendFile(exchange, path, ContentType.TEXT_HTML);
    }

    private void registrationPost(HttpExchange exchange){
        boolean isEmail = false;
        String row = getBody(exchange);
        Map<String, String> parsed = Utils.parseUrlEncoded(row, "&");
        Employee employee = new Employee(
                parsed.get("name"),
                parsed.get("surname"),
                parsed.get("email"),
                parsed.get("user-password")
        );

        WorkingLibrary library = getLibraryModel();

        for(int i = 0; i < library.getLibrary().getEmployeesList().size(); i++){
            if(library.getLibrary().getEmployeesList().get(i).getEmail().equals(parsed.get("email"))){
                isEmail = true;
            }
        }
        if(!isEmail){
            library.getLibrary().getEmployeesList().add(employee);
            redirect303(exchange, "/sucRegistration.html");
        }
        else {
            redirect303(exchange, "/failRegistration.html");
        }
        library.makeAndSave("library.json", library.getLibrary());
    }
}
