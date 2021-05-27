package org.example.models;

public class Channel {
    private int id;
    private int channelId;
    private String name;
    private String createdAt;

    public Channel(int id,  int channelId, String name, String createdAt){
        this.id = id;
        this.channelId = channelId;
        this.name = name;
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }
}
