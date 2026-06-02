package com.mentorhub.message.controller;

import com.mentorhub.common.dto.ApiResponse;
import com.mentorhub.message.dto.SendMessageRequest;
import com.mentorhub.message.entity.Message;
import com.mentorhub.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ApiResponse<Message> send(@Valid @RequestBody SendMessageRequest request) {
        return ApiResponse.ok(messageService.send(request));
    }

    @GetMapping("/conversation/{peerId}")
    public ApiResponse<List<Message>> conversation(@PathVariable Long peerId) {
        return ApiResponse.ok(messageService.conversation(peerId));
    }

    @PostMapping("/conversation/{peerId}/read")
    public ApiResponse<Void> markRead(@PathVariable Long peerId) {
        messageService.markRead(peerId);
        return ApiResponse.okMessage("Marked as read");
    }

    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> unreadCount() {
        return ApiResponse.ok(Map.of("count", messageService.unreadCount()));
    }
}
