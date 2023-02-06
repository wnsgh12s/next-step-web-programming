package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import java.util.stream.Collectors;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import session.HttpSession;
import util.HttpRequestUtils;
import webserver.RequestHandler;

import java.util.Collection;

public class UserListController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    @Override
    public void use(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        String loginValue = HttpRequestUtils.parseCookies(httpRequest.getHeaders("Cookie")).get("logined");
        boolean isLoginCookie = Boolean.parseBoolean(loginValue);
        if (isLoginCookie && isLoinged(httpRequest.getSession())) {
            Collection<User> users = DataBase.findAll();
            log.debug("{}", users.stream().map(User::getName).collect(Collectors.joining("\n")));
            httpResponse.redirect("/index.html");
            return;
        }
        log.debug("You must log in first..");
        httpResponse.redirect("/index.html");
    }

    private static boolean isLoinged(HttpSession httpSession){
        Object user = httpSession.getAttribute("user");
        return user != null;
    }
}
