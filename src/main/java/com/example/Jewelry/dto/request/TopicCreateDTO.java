package com.example.Jewelry.dto.request;

import com.example.Jewelry.dto.CommentDTO;
import com.example.Jewelry.entity.CTV;
import com.example.Jewelry.entity.Category;
import com.example.Jewelry.entity.Comment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicCreateDTO {

    private int id;

    private String title;

    private String content;

    private String status;

    private boolean isDeleted = false;

    private int ctvID;

    private int categoryID;

    private List<CommentDTO> comments;

}
