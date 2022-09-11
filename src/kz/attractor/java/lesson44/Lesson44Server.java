package kz.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import kz.attractor.java.server.BasicServer;
import kz.attractor.java.server.ContentType;
import kz.attractor.java.server.ResponseCodes;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Lesson44Server extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();

    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/sample", this::freemarkerSampleHandler);

        registerGet("/books", this::booksHandlerGet);

        registerPost("/books", this::booksHandlerPost);

        registerGet("/book", this::bookModelHandlerGet);

        registerGet("/employees", this::employeesListHandlerGet);

        registerGet("/logbook", this::logbookModelHandler);

        registerPost("/employee", this::employeeModelHandlerPost);

        registerGet("/employee", this::employeeModelHandlerGet);

    }

    //в методе происходит подключение фримаркета
    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            // путь к каталогу в котором у нас хранятся шаблоны
            // это может быть совершенно другой путь, чем тот, откуда сервер берёт файлы
            // которые отправляет пользователю
            cfg.setDirectoryForTemplateLoading(new File("data"));

            // прочие стандартные настройки о них читать тут
            // https://freemarker.apache.org/docs/pgui_quickstart_createconfiguration.html
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void freemarkerSampleHandler(HttpExchange exchange) {
        renderTemplate(exchange, "sample.html", getSampleDataModel());
    }

    private  void booksHandlerGet(HttpExchange exchange){
        WorkingLibrary library = getLibraryModel();
        String cookieString = getCookies(exchange);
        Map<String, String> cookies = Cookie.parse(cookieString);
        String cookiesValue = cookies.getOrDefault("id", "0");
        library.setAuthorized(false);
        for(int i = 0;  i < library.getLibrary().getEmployeesList().size(); i++){
            if(library.getLibrary().getEmployeesList().get(i).getId() != null
                && library.getLibrary().getEmployeesList().get(i).getId().equals(cookiesValue)
                && !cookiesValue.equals("0")){
                library.setAuthorized(true);
                library.setUserId(library.getLibrary().getEmployeesList().get(i).getId());
            }
        }

        if(library.isAuthorized()){
            renderTemplate(exchange, "books.html", library);
        }
        else {
            renderTemplate(exchange, "login.html", library);
        }
    }

    private  void booksHandlerPost(HttpExchange exchange){
        LocalDate localDate = LocalDate.now();
        String parsLocalDate = String.valueOf(localDate);
        WorkingLibrary library = getLibraryModel();
        String row = getBody(exchange);
        Map<String, String> parsedDate = Utils.parseUrlEncoded(row, "&");
        String cookieString = getCookies(exchange);
        Map<String, String> cookies = Cookie.parse(cookieString);
        String cookiesValue = cookies.getOrDefault("id", "0");
        if(parsedDate.containsKey("bookTake")){
            for(int i = 0; i < library.getLibrary().getEmployeesList().size(); i++){
                if(library.getLibrary().getEmployeesList().get(i).getId().equals(cookiesValue)
                        && library.getLibrary().getEmployeesList().get(i).getNumberOfBooks() < 2
                        && !library.getLibrary().getBooksList().get(Integer.parseInt(parsedDate.get("bookTake")) - 1).isSued()){
                    library.getLibrary().getEmployeesList().get(i).setNumberOfBooks(
                            library.getLibrary().getEmployeesList().get(i).getNumberOfBooks() + 1);
                    library.getLibrary().getBooksList().get(Integer.parseInt(parsedDate.get("bookTake")) - 1).setSued(true);
                    library.getLibrary().getBooksList().get(Integer.parseInt(parsedDate.get("bookTake")) - 1).setToWhomIssued(
                            library.getLibrary().getEmployeesList().get(i));
                    library.getLibrary().getLogbook().add(new Entry(
                            parsLocalDate,
                            "",
                            library.getLibrary().getBooksList().get(Integer.parseInt(parsedDate.get("bookTake")) - 1),
                            library.getLibrary().getEmployeesList().get(i)));
                }
            }
        } else if (parsedDate.containsKey("bookReturn")) {
            if(library.getLibrary().getLogbook().size() > 0){
                for(int i = 0; i < library.getLibrary().getLogbook().size(); i++){
                    if(library.getLibrary().getLogbook().get(i).getReturnedTheBook().equals("")
                            && library.getLibrary().getLogbook().get(i).getBook().getName().equals(
                            library.getLibrary().getBooksList().get(Integer.parseInt(parsedDate.get("bookReturn")) - 1).getName())
                    ){
                        library.getLibrary().getLogbook().get(i).setReturnedTheBook(parsLocalDate);
                    }
                }
            }
            System.out.println("Return");
            for(int i = 0; i < library.getLibrary().getEmployeesList().size(); i++){
                if(library.getLibrary().getEmployeesList().get(i).getId().equals(cookiesValue)
                        && library.getLibrary().getBooksList().get(Integer.parseInt(parsedDate.get("bookReturn")) - 1).isSued()){
                    library.getLibrary().getEmployeesList().get(i).setNumberOfBooks(
                            library.getLibrary().getEmployeesList().get(i).getNumberOfBooks() - 1);
                    library.getLibrary().getBooksList().get(Integer.parseInt(parsedDate.get("bookReturn")) - 1).setSued(false);
                    library.getLibrary().getBooksList().get(Integer.parseInt(parsedDate.get("bookReturn")) - 1).setToWhomIssued(
                            null);
                }
            }
        }

        library.makeAndSave("library.json", library.getLibrary());
        renderTemplate(exchange, "books.html", library);
    }

    private  void employeesListHandlerGet(HttpExchange exchange){
        renderTemplate(exchange, "employees.html", getLibraryModel());
    }

    private  void bookModelHandlerGet(HttpExchange exchange) {
        System.out.println("Start");
        WorkingLibrary library = getLibraryModel();
        String query = getQueryParams(exchange);
        Map<String, String> bookId = Utils.parseUrlEncoded(query, "&");
        Book book = null;
        for (int i = 0; i < library.getLibrary().getBooksList().size(); i++) {
            if (Integer.parseInt(bookId.get("bookId")) == library.getLibrary().getBooksList().get(i).getId()) {
                book = library.getLibrary().getBooksList().get(i);
                renderTemplate(exchange, "book.html", book);
            }
        }
    }

    private  void logbookModelHandler(HttpExchange exchange){
        renderTemplate(exchange, "logbook.html", getLibraryModel());
    }

    private  void employeeModelHandlerPost(HttpExchange exchange){
        String row = getBody(exchange);
        Map<String, String> parsedDate = Utils.parseUrlEncoded(row, "&");
        if(parsedDate.get("exit").equals("1")){
            deleteCookie(exchange);
        }
    }

    private  void employeeModelHandlerGet(HttpExchange exchange){
        WorkingLibrary library = getLibraryModel();
        System.out.println(library.getLibrary().getLogbook().get(0).getTookTheBook());
        String cookieString = getCookies(exchange);
        Map<String, String> cookies = Cookie.parse(cookieString);
        String cookiesValue = cookies.getOrDefault("id", "0");
        library.setAuthorized(false);
        for(int i = 0;  i < library.getLibrary().getEmployeesList().size(); i++){
            if(library.getLibrary().getEmployeesList().get(i).getId() != null
                    && library.getLibrary().getEmployeesList().get(i).getId().equals(cookiesValue)
                    && !cookiesValue.equals("0")){
                library.setUserId(library.getLibrary().getEmployeesList().get(i).getId());
                library.setAuthorized(true);
            }
        }
        if(library.isAuthorized()){
            renderTemplate(exchange, "employee.html", library);
        }
        else {
            renderTemplate(exchange, "login.html", library);
        }
    }


    //в методе происходит обработка шаблона
    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            // загружаем шаблон из файла по имени.
            // шаблон должен находится по пути, указанном в конфигурации
            Template temp = freemarker.getTemplate(templateFile);

            // freemarker записывает преобразованный шаблон в объект класса writer
            // а наш сервер отправляет клиенту массивы байт
            // по этому нам надо сделать "мост" между этими двумя системами

            // создаём поток который сохраняет всё, что в него будет записано в байтовый массив
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // создаём объект, который умеет писать в поток и который подходит для freemarker
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {

                // обрабатываем шаблон заполняя его данными из модели
                // и записываем результат в объект "записи"
                temp.process(dataModel, writer);
                writer.flush();

                // получаем байтовый поток
                var data = stream.toByteArray();

                // отправляем результат клиенту
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private SampleDataModel getSampleDataModel() {
        // возвращаем экземпляр тестовой модели-данных
        // которую freemarker будет использовать для наполнения шаблона
        return new SampleDataModel();
    }

    WorkingLibrary getLibraryModel(){
        return new WorkingLibrary("library.json");
    }

    private BookModel getBookModel(){
        return new BookModel();
    }

    protected  void deleteCookie(HttpExchange exchange){
        WorkingLibrary library = new WorkingLibrary("library.json");
        String cookiesString = getCookies(exchange);
        Map<String, String> cookies = Cookie.parse(cookiesString);
        for(int i = 0; i < library.getLibrary().getEmployeesList().size(); i++){
            if(library.getLibrary().getEmployeesList().get(i).getId().equals(cookies.get("id"))){
                library.getLibrary().getEmployeesList().get(i).setId("0");
                library.setAuthorized(false);
                library.makeAndSave("library.json", library.getLibrary());
            }
        }
        for (Map.Entry<String, String> pair : cookies.entrySet()) {
            if(pair.getValue() != null && pair.getKey().equals("id")){
                pair.setValue("0");
            }
            Cookie userId = Cookie.make(pair.getKey(), pair.getValue());
            userId.setMaxAge(120);
            userId.setHttpOnly(true);
            setCookie(exchange, userId);
            //Path path = makeFilePath("index.html");
            //sendFile(exchange, path, ContentType.TEXT_HTML);
        }
        renderTemplate(exchange, "index.html", library);

    }

}
