package ru.practicum.shareit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class BaseClient {
    //Поле, которое хранит экземпляр RestTemplate, используемого для выполнения HTTP-запросов.
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    //Выполняет GET-запрос по указанному пути.
    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }
    //Выполняет GET-запрос с указанием ID пользователя.

    protected ResponseEntity<Object> get(String path, Long userId) {
        return get(path, userId, null);
    }

    //Выполняет GET-запрос с параметрами.
    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    //Выполняет POST-запрос с телом запроса
    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, null, body);
    }

    //Выполняет POST-запрос с ID пользователя и телом
    protected <T> ResponseEntity<Object> post(String path, Long userId, T body) {
        return post(path, userId, null, body);
    }

    //Выполняет PUT-запрос с ID пользователя и телом.
    protected <T> ResponseEntity<Object> post(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    //Выполняет PUT-запрос с ID пользователя и телом
    protected <T> ResponseEntity<Object> put(String path, Long userId, T body) {
        return put(path, userId, null, body);
    }

    //Выполняет PUT-запрос с ID пользователя и телом.
    protected <T> ResponseEntity<Object> put(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId) {
        return patch(path, userId, null, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body) {
        return patch(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    //Выполняет DELETE-запрос по указанному пути
    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null, null);
    }

    // Выполняет DELETE-запрос с ID пользователя
    protected ResponseEntity<Object> delete(String path, Long userId) {
        return delete(path, userId, null);
    }

    // Выполняет DELETE-запрос с параметрами
    protected ResponseEntity<Object> delete(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    /*
    Этот метод выполняет основной процесс отправки HTTP-запроса. Он создает HttpEntity, содержащий тело запроса
     и заголовки, а затем использует RestTemplate для выполнения запроса.
    Если возникает ошибка HTTP, метод возвращает ответ с кодом ошибки.
     */
    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
/*
HttpEntity<T>: Этот объект представляет собой HTTP-запрос, который включает как тело
запроса (если оно есть), так и заголовки.
new HttpEntity<>(body, defaultHeaders(userId)): Конструктор HttpEntity принимает два
параметра: тело запроса и заголовки. Заголовки создаются с помощью метода defaultHeaders(userId).
 */
        ResponseEntity<Object> shareitServerResponse;
        /*
        Переменная для хранения ответа от сервера
         */
        try {
            if (parameters != null) {
                //Метод exchange выполняет HTTP-запрос. Он принимает следующие параметры:
                /*
                path: Путь к ресурсу.
method: Метод HTTP (GET, POST и т.д.).
requestEntity: Объект, содержащий тело и заголовки запроса.
Object.class: Тип ответа, который ожидается от сервера (в данном случае это Object).
parameters: Дополнительные параметры запроса, если они есть.
Если parameters не равен null, они передаются в метод exchange.
                 */
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            log.error("Ошибка от сервера: {}", e.getResponseBodyAsString());
            /*
            случае возникновения исключения метод возвращает новый объект ResponseEntity, содержащий
            статус ошибки и тело ответа, полученное от сервера (в виде массива байтов).
             */
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());

        }

        return prepareGatewayResponse(shareitServerResponse);
    }

    /*
    Создает и возвращает стандартные заголовки для запросов, включая заголовок X-Sharer-User -Id,
    если указан ID пользователя.
     */
    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    /*
    Подготавливает ответ от сервера, проверяя, успешен ли запрос (статус кода 2xx).
    Если ответ содержит тело, оно добавляется в ответ.
     */
    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
