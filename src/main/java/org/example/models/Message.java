package org.example.models;

public class Message {
    private int id;
    private int conversationId;
    private int userId;
    private String content;
    private String createdAt;

    public Message(int id, int conversationId, int userId, String content, String createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", conversationId=" + conversationId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
