package my.school;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream trustStoreStream = new FileInputStream("path/to/your/truststore.jks")) {
            trustStore.load(trustStoreStream, "truststore-password".toCharArray());
        }

        SSLContext sslContext = SSLContextBuilder.create()
            .loadTrustMaterial(trustStore, null)
            .build();

        CloseableHttpClient httpClient = HttpClients.custom()
            .setSSLContext(sslContext)
            .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return builder.requestFactory(() -> factory).build();
    }

    @Bean
    public RestTemplate restTemplate2(RestTemplateBuilder builder) throws Exception {
        // Load the PKCS12 trust store
        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream trustStoreStream = new FileInputStream("path/to/your/truststore.p12")) {
            trustStore.load(trustStoreStream, "truststore-password".toCharArray());
        }

        // Create an SSLContext using the trust store
        SSLContext sslContext = SSLContextBuilder.create()
            .loadTrustMaterial(trustStore, null)
            .build();

        // Create an HttpClient with the SSL context
        CloseableHttpClient httpClient = HttpClients.custom()
            .setSSLContext(sslContext)
            .build();

        // Configure RestTemplate to use the custom HttpClient
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return builder.requestFactory(() -> factory).build();
    }

//    <dependency>
//    <groupId>org.apache.httpcomponents.client5</groupId>
//    <artifactId>httpclient5</artifactId>
//</dependency>
//<dependency>
//    <groupId>org.apache.httpcomponents.core5</groupId>
//    <artifactId>httpcore5</artifactId>
//</dependency>
}
