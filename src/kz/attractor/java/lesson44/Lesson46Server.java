package kz.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lesson46Server extends Lesson45Server{

    public Lesson46Server(String host, int port) throws IOException {
        super(host, port);

        registerGet("/cookie", this::cookieHandler);
    }

    private void cookieHandler(HttpExchange exchange) {
        //Cookie представляет собой стоку
        // создадим новый Cookie с именем userId и значением 123
        Cookie sessionCookie = Cookie.make("userId", "123");
        // добавим её в заголовки ответ. Set-Cookie это команда котора
        // говорит браузеру о записи куки
        exchange.getResponseHeaders().add("Set-Cookie", sessionCookie.toString());

        Map<String, Object> data = new HashMap<>();
        String name = "times";

        Cookie c1 = Cookie.make("user%Id", "456");
        setCookie(exchange, c1);

        Cookie c2 = Cookie.make("user-mail", "example@mail");
        setCookie(exchange, c2);

        Cookie c3 = Cookie.make("restricted()<>@,;:\\\"/[]?={}", "()<>@,;:\\\"/[]?={}");
        setCookie(exchange, c3);

        //создаем cookieString и
        String cookieString = getCookies(exchange);

        Map<String, String> cookies = Cookie.parse(cookieString);
        String cookiesValue = cookies.getOrDefault(name, "0");
        int times = Integer.parseInt(cookiesValue) + 1;

        Cookie c4 = new Cookie<>(name, times);
        setCookie(exchange, c4);
        data.put(name, times);
        data.put("cookies", cookies);
        renderTemplate(exchange, "cookie.html", data);
    }

}
