package com.mentorhub.message.service;

import com.mentorhub.auth.security.SecurityUtils;
import com.mentorhub.common.exception.ResourceNotFoundException;
import com.mentorhub.message.dto.SendMessageRequest;
import com.mentorhub.message.entity.Message;
import com.mentorhub.message.repository.MessageRepository;
import com.mentorhub.notification.entity.NotificationType;
import com.mentorhub.notification.service.NotificationService;
import com.mentorhub.user.entity.User;
import com.mentorhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public Message send(SendMessageRequest request) {
        User sender = userRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .attachmentUrl(request.getAttachmentUrl())
                .build();
        message = messageRepository.save(message);
        notificationService.notify(receiver, NotificationType.NEW_MESSAGE,
                "New message", sender.getFirstName() + ": " + request.getContent());
        return message;
    }

    public List<Message> conversation(Long peerId) {
        return messageRepository.findConversation(SecurityUtils.currentUserId(), peerId);
    }

    @Transactional
    public void markRead(Long peerId) {
        List<Message> messages = messageRepository.findConversation(SecurityUtils.currentUserId(), peerId);
        Long me = SecurityUtils.currentUserId();
        messages.stream()
                .filter(m -> m.getReceiver().getId().equals(me) && !m.isRead())
                .forEach(m -> m.setRead(true));
        messageRepository.saveAll(messages);
    }

    public long unreadCount() {
        return messageRepository.countByReceiverIdAndReadFalse(SecurityUtils.currentUserId());
    }
}
