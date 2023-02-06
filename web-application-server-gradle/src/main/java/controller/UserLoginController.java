package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import session.HttpSession;
import util.HttpRequestUtils;

import java.io.IOException;
import java.util.Map;

public class UserLoginController implements Controller {
    @Override
    public void use(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        Map<String, String> queryString = HttpRequestUtils.parseQueryString(httpRequest.parseHttpRequestBody());
        User user = DataBase.findUserById(queryString.get("userId"));
        if (isLogin(queryString, user)) {
            httpResponse.loginSucess();
            httpRequest.getSession().setAttribute("user",user);
            return;
        }
        httpResponse.loginFailed();
    }

    private static boolean isLogin(Map<String, String> queryString, User user) {
        return user != null && user.getPassword().equals(queryString.get("password"));
    }
}
