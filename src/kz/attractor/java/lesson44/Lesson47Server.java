package kz.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Lesson47Server extends Lesson46Server{
    public Lesson47Server(String host, int port) throws IOException {
        super(host, port);

        registerGet("/query",this::handleQueryRequest);
    }

    private void handleQueryRequest(HttpExchange exchange) {
        String query = getQueryParams(exchange);
        //разделяет строку полученную из запроса QueryParams по &
        //получаем map ключ значение
        Map<String, String> params = Utils.parseUrlEncoded(query, "&");
        //дальше передача ответа от сервера
        //создание объекта для передачи
        Map<String, Object> data = new HashMap<>();
        //помещаем в объект map c ключом params
        //для шаблонизатора
        data.put("params", params);
        //помещает шаблон в ответ на запрос с данными
        renderTemplate(exchange, "query.html", data);
    }


}
