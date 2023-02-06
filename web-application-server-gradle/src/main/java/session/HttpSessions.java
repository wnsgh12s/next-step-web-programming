package session;

import java.util.Map;

public class HttpSessions {

    private static Map<String, HttpSession> sessions;

    public static HttpSession getSession(String id) {
        HttpSession hs = sessions.get(id);
        if (hs == null) {
            hs = new HttpSession(id);
            sessions.put(id,hs);
            return hs;
        }
        return hs;
    }

    public static void remove(String id) {
        sessions.remove(id);
    }
}
