package com.farmlink.repository;

import com.farmlink.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRoomIdOrderBySentAtAsc(String roomId);
    long countByReceiverIdAndReadFalse(Long receiverId);
}
