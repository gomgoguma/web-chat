package com.webchat.config.kafka;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Message implements Serializable {
    private String author;
    private String content;
    private String timestamp;

    public Message(String author, String content) {
        this.author = author;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}