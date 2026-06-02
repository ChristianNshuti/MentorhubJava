package com.mentorhub.notification.service;

import com.mentorhub.auth.security.SecurityUtils;
import com.mentorhub.notification.entity.Notification;
import com.mentorhub.notification.entity.NotificationType;
import com.mentorhub.notification.repository.NotificationRepository;
import com.mentorhub.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void notify(User user, NotificationType type, String title, String body) {
        notificationRepository.save(Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .body(body)
                .build());
    }

    public List<Notification> myNotifications() {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(SecurityUtils.currentUserId());
    }

    @Transactional
    public void markRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getUser().getId().equals(SecurityUtils.currentUserId())) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }
}
