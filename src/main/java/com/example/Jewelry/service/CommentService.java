package com.example.Jewelry.service;

import com.example.Jewelry.dto.CommentDTO;
import com.example.Jewelry.dto.response.CommonApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommentService {
    ResponseEntity<CommonApiResponse> createComment(CommentDTO request);

    ResponseEntity<List<CommentDTO>> fetchCommentParent(int topicID);

    ResponseEntity<List<CommentDTO>> fetchAllChildrenCommentByParentID(int parentID);

//    ResponseEntity<CommentDTO> fetchAllChildrenCommentByParentID(int parentID);
}
