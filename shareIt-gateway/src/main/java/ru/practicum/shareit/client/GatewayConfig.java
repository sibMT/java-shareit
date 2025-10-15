package ru.practicum.shareit.client;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class GatewayConfig {
    @Bean
    public RestTemplate restTemplate(@Value("${shareit-server.url}") String serverUrl) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate rest = new RestTemplate(factory);
        rest.setUriTemplateHandler(new DefaultUriBuilderFactory(serverUrl));
        return rest;
    }
}

