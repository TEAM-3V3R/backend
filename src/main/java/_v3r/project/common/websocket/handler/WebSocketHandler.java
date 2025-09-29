package _v3r.project.common.websocket.handler;

import _v3r.project.common.websocket.WsSessionRegistry;
import _v3r.project.history.dto.UnrealHistoryResponse;
import _v3r.project.history.service.UnrealHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
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
    private final UnrealHistoryService unrealHistoryService;

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
    // JMeter의 베이스라인 위한 임시 메서드 추가
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<UnrealHistoryResponse> requestList =
                    Arrays.asList(mapper.readValue(message.getPayload(), UnrealHistoryResponse[].class));
            Long userId = requestList.get(0).userId();
            int chatId = requestList.get(0).chatId();

            unrealHistoryService.saveAll(userId, chatId, requestList);
            session.sendMessage(new TextMessage("Processed " + requestList.size() + " items sequentially"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
