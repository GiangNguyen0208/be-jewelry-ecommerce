package com.example.Jewelry.service;

import com.example.Jewelry.dto.CommentDTO;
import com.example.Jewelry.dto.request.TopicCreateDTO;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.dto.response.TopicDTOResponse;
import com.example.Jewelry.entity.Topic;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TopicService {
    ResponseEntity<CommonApiResponse> createTopicByCTV(TopicCreateDTO request);

    List<TopicDTOResponse> fetchAllTopic();

    ResponseEntity<TopicDTOResponse> fetchDetailByID(int topicID);

    List<CommentDTO> fetchChildComments(int parentId);
}
