package com.freelance.repository;

import com.freelance.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.sender.id = :a AND m.receiver.id = :b) OR (m.sender.id = :b AND m.receiver.id = :a) ORDER BY m.sentAt DESC")
    Page<Message> findConversationBetween(@Param("a") Long userA, @Param("b") Long userB, Pageable pageable);

    @Query(value = "SELECT DISTINCT CASE WHEN sender_id = :uid THEN receiver_id ELSE sender_id END FROM messages WHERE sender_id = :uid OR receiver_id = :uid", nativeQuery = true)
    List<Long> findPartnerUserIds(@Param("uid") Long userId);

    long countByReceiverIdAndReadFalse(Long receiverId);
}
