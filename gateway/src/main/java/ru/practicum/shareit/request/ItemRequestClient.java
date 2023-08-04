package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Map;

@Slf4j
@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(PostItemRequestDto requestDto, int userId) {
        log.info("Обработка запроса создания запроса аренды от пользователя c id = {} ", userId);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getForOwner(int userId) {
        log.info("Обработка запроса владельца с userId = {} на возврат запросов бронирования ", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> getAll(int from, int size, int userId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        log.info("Обработка запроса пользователя с userId = {} на возврат всех запросов бронирования ", userId);
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getById(int requestId, int userId) {
        log.info("Обработка запроса на возврат всех запросов бронирования с Id = {}", requestId);
        return get("/" + requestId, userId);
    }
}
