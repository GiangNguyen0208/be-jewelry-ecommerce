package com.example.Jewelry.service.ServiceImpl;

import com.example.Jewelry.Utility.Constant;
import com.example.Jewelry.dao.CommentDAO;
import com.example.Jewelry.dao.CtvDAO;
import com.example.Jewelry.dao.TopicDAO;
import com.example.Jewelry.dto.CommentDTO;
import com.example.Jewelry.dto.request.TopicCreateDTO;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.dto.response.TopicDTOResponse;
import com.example.Jewelry.entity.*;
import com.example.Jewelry.service.CategoryService;
import com.example.Jewelry.service.TopicService;
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
public class TopicServiceImpl implements TopicService {
    @Autowired
    private TopicDAO topicDAO;

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<CommonApiResponse> createTopicByCTV(TopicCreateDTO request) {
        CommonApiResponse response = new CommonApiResponse();

        if (request == null) {
            response.setResponseMessage("request input missing");
            response.setSuccess(false);
            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getCtvID() == 0) {
            response.setResponseMessage("Yêu cầu CTV ID thêm Topic");
            response.setSuccess(false);
            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }
        if (request.getCategoryID() == 0) {
            response.setResponseMessage("Yêu cầu ID danh mục về chủ đề");
            response.setSuccess(false);
            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        Category categoryTopic = categoryService.getCategoryById(request.getCategoryID());

        if (categoryTopic == null) {
            response.setResponseMessage("Danh mục không tồn tại");
            response.setSuccess(false);
            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.NOT_FOUND);
        }

        User authorCreateTopic = userService.getUserById(request.getCtvID());

        if (authorCreateTopic.getId() == 0) {
            response.setResponseMessage("Người dùng không tồn tại");
            response.setSuccess(false);
            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.NOT_FOUND);
        }

        if (!authorCreateTopic.getRole().equals(Constant.UserRole.ROLE_CTV.value()) ) {
            response.setResponseMessage("Người dùng ko có quyền thêm Topic");
            response.setSuccess(false);
            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.UNAUTHORIZED);
        }

        Topic newTopic = Topic.builder()
                .title(request.getTitle())
                .author(authorCreateTopic)
                .category(categoryTopic)
                .createdAt(LocalDateTime.now())
                .content(request.getContent())
                .status(Constant.TopicStatus.OPEN.name())
                .isDeleted(false)
                .build();

        topicDAO.save(newTopic);

        response.setResponseMessage("NTạo Topic thành công !!!");
        response.setSuccess(true);
        return new ResponseEntity<CommonApiResponse>(response, HttpStatus.CREATED);
    }

    @Override
    public List<TopicDTOResponse> fetchAllTopic() {
        String status = Constant.TopicStatus.OPEN.name();
        List<Topic> topics = topicDAO.findAllByOPEN(status);

        List<TopicDTOResponse> responses = new ArrayList<>();

        for (Topic topic : topics) {
            TopicDTOResponse topicDTOResponse = TopicDTOResponse.builder()
                    .id(topic.getId())
                    .title(topic.getTitle())
                    .content(topic.getContent())
                    .author(topic.getAuthor().getUsername())
                    .avatar(topic.getAuthor().getAvatar())
                    .categoryName(topic.getCategory().getName())
                    .thumbnail(topic.getCategory().getThumbnail())
                    .createdAt(topic.getCreatedAt())
                    .status(status)
                    .build();
            responses.add(topicDTOResponse);
        }
        return responses;
    }

    @Override
    public ResponseEntity<TopicDTOResponse> fetchDetailByID(int topicID) {

        TopicDTOResponse response = new TopicDTOResponse();

        if (topicID == 0) {
            return new ResponseEntity<TopicDTOResponse>(HttpStatus.BAD_REQUEST);
        }

        Optional<Topic> optionalTopic = topicDAO.findById(topicID);
        if (!optionalTopic.isPresent()) {
            return new ResponseEntity<TopicDTOResponse>(HttpStatus.NOT_FOUND);
        }
        Topic topic = optionalTopic.get();

        List<Comment> parentComments = commentDAO.findByTopicIdAndParentCommentIsNull(topicID);

        List<CommentDTO> commentDTOS = parentComments.stream()
                .map(comment -> CommentDTO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .isDeleted(comment.isDeleted())
                        .userID(comment.getAuthor().getId())
                        .topicID(comment.getTopic().getId())
                        .avatar(comment.getAuthor().getAvatar())
                        .createdAt(comment.getCreatedAt())
                        .authorName(comment.getAuthor().getUsername())
//                        .commentID(0) // không có cha
//                        .level(0)
                        .build())
                .collect(Collectors.toList());

        response.setId(topic.getId());
        response.setAvatar(topic.getAuthor().getAvatar());
        response.setThumbnail(topic.getCategory().getThumbnail());
        response.setTitle(topic.getTitle());
        response.setContent(topic.getContent());
        response.setStatus(topic.getStatus());
        response.setCreatedAt(topic.getCreatedAt());
        response.setDeleted(false);
        response.setAuthor(topic.getAuthor().getUsername());
        response.setCategoryName(topic.getCategory().getName());
        response.setCommentDTOS(commentDTOS);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    public List<CommentDTO> fetchChildComments(int parentId) {
        List<Comment> childComments = commentDAO.findByParentCommentId(parentId);

        return childComments.stream()
                .map(comment -> CommentDTO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .isDeleted(comment.isDeleted())
                        .userID(comment.getAuthor().getId())
                        .topicID(comment.getTopic().getId())
                        .avatar(comment.getAuthor().getAvatar())
                        .authorName(comment.getAuthor().getUsername())
                        .createdAt(comment.getCreatedAt())
                        .commentID(comment.getParentComment().getId())
                        .level(CommentDTO.calculateLevel(comment))
                        .build())
                .collect(Collectors.toList());
    }

}
