package _v3r.project.common.websocket.handler;

import _v3r.project.common.websocket.WsSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final WsSessionRegistry registry;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        registry.set(session);

        try {
            String msg = """
            {
              "type": "SESSION_INFO",
              "sessionId": "%s"
            }
            """.formatted(session.getId());
            session.sendMessage(new TextMessage(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Unreal connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        registry.remove(session);
        System.out.println("Unreal disconnected: " + session.getId());
    }
}
