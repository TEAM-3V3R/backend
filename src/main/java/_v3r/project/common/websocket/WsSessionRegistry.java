package _v3r.project.common.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WsSessionRegistry {

    private volatile WebSocketSession currentSession;

    public void set(WebSocketSession session) {
        this.currentSession = session;
    }

    public void remove(WebSocketSession session) {
        if (this.currentSession != null && this.currentSession.getId().equals(session.getId())) {
            this.currentSession = null;
        }
    }

    public boolean sendTo(String jsonPayload) {
        if (currentSession == null || !currentSession.isOpen()) return false;
        try {
            currentSession.sendMessage(new TextMessage(jsonPayload));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

