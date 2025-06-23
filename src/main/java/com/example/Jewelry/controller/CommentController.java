package com.example.Jewelry.controller;

import com.example.Jewelry.dto.CommentDTO;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.entity.Comment;
import com.example.Jewelry.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {
    @Autowired
    private CommentService commentService;


    @PostMapping("/create")
    public ResponseEntity<CommonApiResponse> createComment(
            @RequestBody CommentDTO request,
            @RequestParam(value = "parentCommentId", required = false) Integer parentCommentId) {
        System.out.println("Received request: " + request + ", parentCommentId: " + parentCommentId);
        request.setCommentID(parentCommentId != null ? parentCommentId : 0);
        return commentService.createComment(request);
    }

    @GetMapping("/fetch-all-comment-parent/{topicID}")
    public ResponseEntity<List<CommentDTO>> fetchCommentParent(@PathVariable("topicID") int topicID) {
        return commentService.fetchCommentParent(topicID);
    }

    @GetMapping("/fetch-all-children-comment/{parentID}")
    public ResponseEntity<List<CommentDTO>> fetchAllChildrenCommentByParentID(@PathVariable("parentID") int parentID) {
        return commentService.fetchAllChildrenCommentByParentID(parentID);
    }

}
