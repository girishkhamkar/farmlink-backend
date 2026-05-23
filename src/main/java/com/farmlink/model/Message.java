package com.farmlink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private User receiver;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private String roomId;

    private boolean read = false;

    @Column(updatable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }

    public Message() {}

    public Long getId() { return id; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public String getContent() { return content; }
    public String getRoomId() { return roomId; }
    public boolean isRead() { return read; }
    public LocalDateTime getSentAt() { return sentAt; }

    public void setId(Long id) { this.id = id; }
    public void setSender(User sender) { this.sender = sender; }
    public void setReceiver(User receiver) { this.receiver = receiver; }
    public void setContent(String content) { this.content = content; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void setRead(boolean read) { this.read = read; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User sender;
        private User receiver;
        private String content;
        private String roomId;
        private boolean read = false;
        private LocalDateTime sentAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder sender(User s) { this.sender = s; return this; }
        public Builder receiver(User r) { this.receiver = r; return this; }
        public Builder content(String c) { this.content = c; return this; }
        public Builder roomId(String r) { this.roomId = r; return this; }
        public Builder read(boolean read) { this.read = read; return this; }
        public Builder sentAt(LocalDateTime s) { this.sentAt = s; return this; }

        public Message build() {
            Message m = new Message();
            m.id = this.id;
            m.sender = this.sender;
            m.receiver = this.receiver;
            m.content = this.content;
            m.roomId = this.roomId;
            m.read = this.read;
            m.sentAt = this.sentAt;
            return m;
        }
    }
}
