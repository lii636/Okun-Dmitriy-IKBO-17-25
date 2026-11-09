package depgraph.repository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import depgraph.exception.DepgraphException;

public class JdkHttpTransport implements HttpTransport {
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private static final int NOT_FOUND = 404;
    private static final int OK = 200;

    private final HttpClient client;

    public JdkHttpTransport() {
        this(HttpClient.newBuilder().connectTimeout(TIMEOUT).build());
    }

    public JdkHttpTransport(HttpClient client) {
        this.client = client;
    }

    @Override
    public String get(URI url) {
        HttpRequest request = HttpRequest.newBuilder(url)
            .timeout(TIMEOUT)
            .GET()
            .build();
        try {
            HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );
            int status = response.statusCode();
            if (status == NOT_FOUND) {
                throw new DepgraphException("package version not found: " + url);
            }
            if (status != OK) {
                throw new DepgraphException(
                    "failed to fetch " + url + ": HTTP " + status
                );
            }
            return response.body();
        } catch (DepgraphException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new DepgraphException("failed to fetch " + url, exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new DepgraphException("failed to fetch " + url, exception);
        }
    }
}
