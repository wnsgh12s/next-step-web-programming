package controller;

import http.HttpRequest;
import http.HttpResponse;
import util.path.Path;

import java.io.IOException;
import java.util.UUID;

public class MainController {

    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;

    public MainController(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        if (httpRequest.getcookies().get("JSESSIONID") == null) {
            httpResponse.setCookie("JSESSIONID = " + UUID.randomUUID());
        }
    }

    public void runProcess() throws IOException {
        Path url = Path.from(httpRequest.getUrl());
        Controller controller = url.selectController();
        controller.use(httpRequest, httpResponse);
        httpRequest.getcookies();
    }

}
