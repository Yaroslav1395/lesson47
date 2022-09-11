package kz.attractor.java.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import kz.attractor.java.lesson44.Cookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public abstract class BasicServer {

    private final HttpServer server;
    // путь к каталогу с файлами, которые будет отдавать сервер по запросам клиентов
    private final String dataDir = "data";
    private Map<String, RouteHandler> routes = new HashMap<>();

    //создается конструктор BasicServer. В конструктор передается host и port
    protected BasicServer(String host, int port) throws IOException {
        //создается сервер
        server = createServer(host, port);
        //
        registerCommonHandlers();
    }

    //метод возвращает строку с методом(запроса) и путем к файлу
    private static String makeKey(String method, String route) {
        route = ensureStartWithSlash(route);
        return String.format("%s %s", method.toUpperCase(), route);
    }

    private static String ensureStartWithSlash(String route) {
        if(route.startsWith(".")){
            return route;
        }
        return route.startsWith("/") ? route : "/" + route;
    }

    //метод определяет ключ по которому мы выбираем обработчик получая данные из объекта
    /*
     HttpExchange класс инкапсулирует полученный запрос HTTP и ответ, который будет сгенерирован
     в одном обмене. Это обеспечивает методы для того, чтобы они исследовали запрос от клиента,
     и для создания и отправки ответа.
    */
    private static String makeKey(HttpExchange exchange) {
        //берем метод и путь из объекта exchange и собираем из них
        //строковый ключ который будет использоваться для выбора обработчика
        var method = exchange.getRequestMethod();// GET, POST
        //URI это URL(адрес хоста) + URN(путь к фалу)
        var path = exchange.getRequestURI().getPath();

        if(path.endsWith("/") && path.length() > 1){
            path = path.substring(0, path.length() - 1);
        }
        //метод lastIndexOf(char) вернет индекс последнего вхождения char или -1 если его не было
        var index = path.lastIndexOf(".");
        //метод path.substring(int) начинает возвращать символы начиная с индекса под номером int
        var extOrPath = index != -1 ? path.substring(index).toLowerCase() : path;
        //вернет строку формата ТИП_МЕТОДА(GET || POST) какой-то_путь/до_файла/без_расширения
        return makeKey(method, extOrPath);
    }

    //getResponseHeaders() Получает заголовки, связанные с ответом.
    //String.valueOf(type) преобразует в строку
    //Content-Type сообщает клиенту, какой будет тип передаваемого контента
    //setContentType(передается запрос, тип передаваемого контента) метод запишет в заголовок ответа HttpExchange
    //тип контента который будет передан
    private static void setContentType(HttpExchange exchange, ContentType type) {
        exchange.getResponseHeaders().set("Content-Type", String.valueOf(type));
    }

    public String getContentType(HttpExchange exchange) {
        //getOrDefault(1 параметр, 2 параметр) если первого не будет, вернет второй
        return exchange.getRequestHeaders().getOrDefault("Content-Type", List.of("")).get(0);
    }

    //метод возвращает созданный сервер, для создания необходимы хост и порт
    private static HttpServer createServer(String host, int port) throws IOException {
        var msg = "Starting server on http://%s:%s/%n";
        System.out.printf(msg, host, port);
        //создаем сокет
        var address = new InetSocketAddress(host, port);
        return HttpServer.create(address, 50);
    }

    //в методе имеется обработчик
    private void registerCommonHandlers() {
        // самый основной обработчик, который будет определять
        // какие обработчики вызывать в дальнейшем
        server.createContext("/", this::handleIncomingServerRequests);//обработчик
        // специфичные обработчики, которые выполняют свои действия
        // в зависимости от типа запроса

        // обработчик для корневого запроса
        // именно этот обработчик отвечает что отображать,
        // когда пользователь запрашивает localhost:9889
        registerGet("/", exchange -> sendFile(exchange, makeFilePath("index.html"), ContentType.TEXT_HTML));

        // эти обрабатывают запросы с указанными расширениями
        registerFileHandler(".css", ContentType.TEXT_CSS);
        registerFileHandler(".html", ContentType.TEXT_HTML);
        registerFileHandler(".jpg", ContentType.IMAGE_JPEG);
        registerFileHandler(".png", ContentType.IMAGE_PNG);

    }

    //универсальный метод для обработки GET запросов
    protected final void registerGet(String route, RouteHandler handler) {
        registerGenericHandler("GET", route, handler);
    }

    protected final void registerGenericHandler(String method, String route, RouteHandler handler){
        getRoutes().put(makeKey(method, route), handler);
    }

    //универсальный метод для обработки POST запросов
    protected  final void registerPost(String route, RouteHandler handler){
        registerGenericHandler("POST", route, handler);

    }
    protected final void registerFileHandler(String fileExt, ContentType type) {
        registerGet(fileExt, exchange -> sendFile(exchange, makeFilePath(exchange), type));
    }

    //метод вернет map<Строка,
    protected final Map<String, RouteHandler> getRoutes() {
        return routes;
    }

    //метод вернет тело запроса в виде строки
    protected final String getBody(HttpExchange exchange) {
        //чтобы получить тело, надо запустить InputStream
        InputStream input = exchange.getRequestBody();
        //устанавливаем Charset
        InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);

        try(BufferedReader reader = new BufferedReader(isr)){
            //тело прочитаем построчно, но делимитр будет пустой, в итоге получится одна строка
            return  reader.lines().collect(joining(""));
        } catch (IOException e){
            e.printStackTrace();
        }
        return "";
    }

    //метод отправит файл sendFile(запрос, путь к файлу, тип передаваемого контента)
    protected final void sendFile(HttpExchange exchange, Path pathToFile, ContentType contentType) {
        try {
            //если файла по переданному пути не, то выбросит ошибку 404
            if (Files.notExists(pathToFile)) {
                respond404(exchange);
                return;
            }
            //запишет в переменную data все данные которые будут в конце пути pathToFile преобразовав это в байты
            var data = Files.readAllBytes(pathToFile);
            //отправит данные
            sendByteData(exchange, ResponseCodes.OK, contentType, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //метод вернет путь полученный из запроса
    private Path makeFilePath(HttpExchange exchange) {
        return makeFilePath(exchange.getRequestURI().getPath());
    }

    //метод примет строку и создаст путь Path.of(dataDir = "data", s)
    protected Path makeFilePath(String... s) {
        return Path.of(dataDir, s);
    }

    //метод отправит данные
    //sendByteData(запрос, код состояния, тип передаваемого контента, сообщение в байтах)
    protected final void sendByteData(HttpExchange exchange, ResponseCodes responseCode,
                                      ContentType contentType, byte[] data) throws IOException {
        //создаем поток output через запрос тела
        try (var output = exchange.getResponseBody()) {
            //установит тип передаваемого контента в запросе
            setContentType(exchange, contentType);
            //в заголовке запроса укажет код состояния
            exchange.sendResponseHeaders(responseCode.getCode(), 0);
            //запишет сообщение в байтах
            output.write(data);
            //отправит
            output.flush();
        }
    }

    //метод нужен для возврата 404 если страница не найдена
    //принимает запрос
    private void respond404(HttpExchange exchange) {
        try {
            //преобразует строку в байты
            var data = "404 Not found".getBytes();
            //отправит сообщение в виде ответа на запрос, через поток, установив код состояния, тип контената
            //сообщение в байтах
            sendByteData(exchange, ResponseCodes.NOT_FOUND, ContentType.TEXT_PLAIN, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected final void redirect303(HttpExchange exchange, String path){
        try {
            exchange.getResponseHeaders().add("Location", path);
            exchange.sendResponseHeaders(303, 0);
            exchange.getResponseBody().close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //обрабатывает входящие запросы к серверу
    private void handleIncomingServerRequests(HttpExchange exchange) {
        var route = getRoutes().getOrDefault(makeKey(exchange), this::respond404);
        route.handle(exchange);
    }

    public final void start() {
        server.start();
    }

    protected String getCookies(HttpExchange exchange) {
        return exchange.getRequestHeaders()
                //либо куки, если нет то пустой лист
                .getOrDefault("Cookie", List.of(""))
                .get(0);
    }

    protected void setCookie(HttpExchange exchange, Cookie c1) {
        exchange.getResponseHeaders().add("Set-Cookie", c1.toString());
    }

    protected String getQueryParams(HttpExchange exchange){
        String query = exchange.getRequestURI().getQuery();
        return Objects.nonNull(query) ? query : "";
    }
}
