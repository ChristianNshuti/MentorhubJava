package com.mentorhub.message.websocket;

import com.mentorhub.message.dto.SendMessageRequest;
import com.mentorhub.message.entity.Message;
import com.mentorhub.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PresenceService presenceService;

    @MessageMapping("/chat.send")
    public void send(@Payload SendMessageRequest request, Principal principal) {
        Message saved = messageService.send(request);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(request.getReceiverId()),
                "/queue/messages",
                saved);
        if (principal != null) {
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/messages",
                    saved);
        }
    }

    @MessageMapping("/chat.typing")
    @SendTo("/topic/typing")
    public Map<String, Object> typing(@Payload Map<String, Object> payload) {
        return payload;
    }

    @MessageMapping("/chat.read")
    public void read(@Payload Map<String, Long> payload) {
        Long peerId = payload.get("peerId");
        if (peerId != null) {
            messageService.markRead(peerId);
        }
    }

    @MessageMapping("/presence.{userId}")
    public void presence(@DestinationVariable Long userId, @Payload Map<String, Boolean> body) {
        presenceService.setOnline(userId, Boolean.TRUE.equals(body.get("online")));
        Object payload = Map.<String, Object>of("userId", userId, "online", body.get("online"));
        messagingTemplate.convertAndSend("/topic/presence", payload);
    }
}
