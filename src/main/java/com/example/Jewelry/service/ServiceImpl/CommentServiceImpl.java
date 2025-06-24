package com.example.Jewelry.service.ServiceImpl;

import com.example.Jewelry.dao.CommentDAO;
import com.example.Jewelry.dao.TopicDAO;
import com.example.Jewelry.dao.UserDAO;
import com.example.Jewelry.dto.CommentDTO;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.entity.Comment;
import com.example.Jewelry.entity.Topic;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.service.CommentService;
import com.example.Jewelry.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private TopicDAO topicDAO;

    @Override
    public ResponseEntity<CommonApiResponse> createComment(CommentDTO request) {
        CommonApiResponse response = new CommonApiResponse();

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonApiResponse.fail("Vui lòng nhập nội dung bình luận"));
        }

        if (request.getUserID() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonApiResponse.fail("Vui lòng đăng nhập"));
        }

        if (request.getTopicID() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonApiResponse.fail("Không tìm thấy chủ đề"));
        }

        Optional<User> userOpt = userDAO.findById(request.getUserID());
        Optional<Topic> topicOpt = topicDAO.findById(request.getTopicID());
        if (!userOpt.isPresent() || !topicOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonApiResponse.fail("Không tìm thấy thông tin người dùng hoặc chủ đề"));
        }

        Comment comment = new Comment();
        comment.setType(request.getType());
        comment.setContent(request.getContent());
        comment.setAuthor(userOpt.get());
        comment.setTopic(topicOpt.get());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setDeleted(false);
        comment.setPositive(request.isPositive());

        // Nếu có parentCommentID > 0 thì tìm comment cha
        if (request.getParentCommentID() > 0) {
            Optional<Comment> parentOpt = commentDAO.findById(request.getParentCommentID());
            if (parentOpt.isPresent()) {
                comment.setParentComment(parentOpt.get());
            }
        } else {
            comment.setParentComment(null);
        }

        commentDAO.save(comment);

        return ResponseEntity.ok(CommonApiResponse.success("Tạo bình luận thành công"));
    }

    @Override
    public ResponseEntity<List<CommentDTO>> fetchCommentParent(int topicID) {
        List<Comment> parentComments = commentDAO.findByTopicIdAndParentCommentIsNull(topicID);

        List<CommentDTO> commentDTOS = parentComments.stream()
                .map(comment -> CommentDTO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .isDeleted(comment.isDeleted())
                        .type(comment.getType())
                        .userID(comment.getAuthor().getId())
                        .topicID(comment.getTopic().getId())
                        .avatar(comment.getAuthor().getAvatar())
                        .createdAt(comment.getCreatedAt())
                        .isPositive(comment.isPositive())
                        .authorName(comment.getAuthor().getUsername())
//                        .commentID(0) // không có cha
//                        .level(0)
                        .build())
                .collect(Collectors.toList());

        return new ResponseEntity<List<CommentDTO>>(commentDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CommentDTO>> fetchAllChildrenCommentByParentID(int parentID) {
        List<Comment> commentChildrentByParentID = commentDAO.findByParentCommentId(parentID);

        List<CommentDTO> commentDTOS = commentChildrentByParentID.stream()
                .map(comment -> CommentDTO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .isDeleted(comment.isDeleted())
                        .type(comment.getType())
                        .userID(comment.getAuthor().getId())
                        .topicID(comment.getTopic().getId())
                        .avatar(comment.getAuthor().getAvatar())
                        .createdAt(comment.getCreatedAt())
                        .authorName(comment.getAuthor().getUsername())
                        .isPositive(comment.isPositive())
//                        .commentID(0) // không có cha
//                        .level(0)
                        .build())
                .collect(Collectors.toList());

        return new ResponseEntity<List<CommentDTO>>(commentDTOS, HttpStatus.OK);
    }

}
