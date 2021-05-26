package org.example.features.conversation;

public class ConvForUser {
    public final int id;
    public final int userId;
    public final int interlocutorId;
    public final String interlocutorUsername;
    public final String interlocutorAvatarUrl;
    public final String updatedAt;

    public ConvForUser(int id, int userId, int interlocutorId, String interlocutorUsername, String interlocutorAvatarUrl, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.interlocutorId = interlocutorId;
        this.interlocutorUsername = interlocutorUsername;
        this.interlocutorAvatarUrl = interlocutorAvatarUrl;
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ConvForUser{" +
                "id=" + id +
                ", userId=" + userId +
                ", interlocutorId=" + interlocutorId +
                ", interlocutorUsername='" + interlocutorUsername + '\'' +
                ", interlocutorAvatarUrl='" + interlocutorAvatarUrl + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getInterlocutorId() {
        return interlocutorId;
    }

    public String getInterlocutorUsername() {
        return interlocutorUsername;
    }

    public String getInterlocutorAvatarUrl() {
        return interlocutorAvatarUrl;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
