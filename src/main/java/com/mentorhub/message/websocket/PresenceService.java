package com.mentorhub.message.websocket;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    private final Map<Long, Boolean> onlineUsers = new ConcurrentHashMap<>();

    public void setOnline(Long userId, boolean online) {
        if (online) {
            onlineUsers.put(userId, true);
        } else {
            onlineUsers.remove(userId);
        }
    }

    public boolean isOnline(Long userId) {
        return onlineUsers.containsKey(userId);
    }
}
