package com.example.Jewelry.dto;

import com.example.Jewelry.entity.Comment;
import com.example.Jewelry.entity.Topic;
import com.example.Jewelry.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private int id;

    private String content;

    private boolean isDeleted = false;

    private int userID;

    private int topicID;

    private int commentID;

    private String type;

    private LocalDateTime createdAt;

    private String authorName;

    private String avatar;

    private List<CommentDTO> childComments;

    private Integer level;

    private int parentCommentID;

    public static Integer calculateLevel(Comment comment) {
        int level = 0;
        Comment current = comment;
        while (current.getParentComment() != null) {
            level++;
            current = current.getParentComment();
        }
        return level;
    }
}
