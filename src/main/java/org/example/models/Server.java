package org.example.models;

public class Server {
    private int id;
    private String name;
    private String avatarUrl;
    private String created_at;
    private int owner;

    public Server(int id, String name, String avatarUrl, String createdAt, int owner){
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.created_at = createdAt;
        this.owner = owner;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }
}
