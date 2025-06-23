package com.example.Jewelry.dto.response;

import com.example.Jewelry.dto.CommentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicDTOResponse {
    private int id;

    private String title;

    private String content;

    private String status;

    private boolean isDeleted = false;

    private String author;

    private String avatar;

    private String categoryName;

    private String thumbnail;

    private LocalDateTime createdAt;

    private List<CommentDTO> commentDTOS;
}
