package com.musashi.spring_boot_jwt.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Objects;

@Document
public class Note {
    @Id private ObjectId id = ObjectId.get();
    private ObjectId ownerId;
    private String title;
    private String content;
    private long color;
    private Instant createdAt;

    public Note() {

    }

    public Note(ObjectId id, ObjectId ownerId, String title, String content, long color, Instant createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.content = content;
        this.color = color;
        this.createdAt = createdAt;
    }

    public ObjectId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ObjectId ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getColor() {
        return color;
    }

    public void setColor(long color) {
        this.color = color;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", color=" + color +
                ", createdAt=" + createdAt +
                ", ownerId=" + ownerId +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return color == note.color && Objects.equals(title, note.title) && Objects.equals(content, note.content) && Objects.equals(createdAt, note.createdAt) && Objects.equals(ownerId, note.ownerId) && Objects.equals(id, note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, color, createdAt, ownerId, id);
    }
}
