package com.mentorhub.message.repository;

import com.mentorhub.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
            SELECT m FROM Message m
            WHERE (m.sender.id = :userId AND m.receiver.id = :peerId)
               OR (m.sender.id = :peerId AND m.receiver.id = :userId)
            ORDER BY m.timestamp ASC
            """)
    List<Message> findConversation(Long userId, Long peerId);

    long countByReceiverIdAndReadFalse(Long receiverId);
}
