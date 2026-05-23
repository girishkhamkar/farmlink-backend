package com.farmlink.controller;

import com.farmlink.model.Message;
import com.farmlink.model.User;
import com.farmlink.repository.MessageRepository;
import com.farmlink.repository.UserRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    // Manual constructor — replaces @RequiredArgsConstructor
    public ChatController(SimpMessagingTemplate messagingTemplate,
                          MessageRepository messageRepository,
                          UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    // WebSocket: send a message
    // Client sends to: /app/chat.send
    // Message delivered to: /topic/chat/{roomId}
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatPayload payload) {
        User sender = userRepository.findById(payload.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(payload.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Only FARMER and CUSTOMER can chat — block ADMIN from sending
        if (sender.getRole() == User.Role.ADMIN) {
            return;
        }

        // Build unique room ID (always smaller ID first for consistency)
        Long farmerId = sender.getRole() == User.Role.FARMER ? sender.getId() : receiver.getId();
        Long customerId = sender.getRole() == User.Role.CUSTOMER ? sender.getId() : receiver.getId();
        String roomId = "farmer" + farmerId + "_customer" + customerId;

        // Save to database
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(payload.getContent())
                .roomId(roomId)
                .sentAt(LocalDateTime.now())
                .build();
        messageRepository.save(message);

        // Build response payload
        Map<String, Object> response = Map.of(
                "senderId", sender.getId(),
                "senderName", sender.getName(),
                "receiverId", receiver.getId(),
                "content", payload.getContent(),
                "roomId", roomId,
                "sentAt", message.getSentAt().toString()
        );

        // Send to the private room topic
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, response);
    }

    // REST: get chat history for a room
    @GetMapping("/api/chat/history/{farmerId}/{customerId}")
    @PreAuthorize("hasRole('FARMER') or hasRole('CUSTOMER')")
    public List<Message> getChatHistory(@PathVariable Long farmerId,
                                         @PathVariable Long customerId,
                                         Authentication auth) {
        String roomId = "farmer" + farmerId + "_customer" + customerId;
        return messageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    // Inner class — replaces @Data with manual getters/setters
    static class ChatPayload {
        private Long senderId;
        private Long receiverId;
        private String content;

        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }

        public Long getReceiverId() { return receiverId; }
        public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}