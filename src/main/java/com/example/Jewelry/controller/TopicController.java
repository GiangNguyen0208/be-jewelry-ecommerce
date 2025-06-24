package com.example.Jewelry.controller;


import com.example.Jewelry.Utility.Constant;
import com.example.Jewelry.dao.TopicDAO;
import com.example.Jewelry.dto.CommentDTO;
import com.example.Jewelry.dto.request.RegisterCTVRequest;
import com.example.Jewelry.dto.request.TopicCreateDTO;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.dto.response.TopicDTOResponse;
import com.example.Jewelry.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topic")
public class TopicController {
    @Autowired
    private TopicService topicService;

    @PostMapping("/create")
    public ResponseEntity<CommonApiResponse> registerCTV(@RequestBody TopicCreateDTO request) {
        return topicService.createTopicByCTV(request);
    }
    @GetMapping("/fetch-all")
    public List<TopicDTOResponse> fetchAllTopic() {
        return topicService.fetchAllTopic();
    }

    @GetMapping("/{topicID}")
    public ResponseEntity<TopicDTOResponse> fetchDetailByID(@PathVariable("topicID") int topicID) {
        return topicService.fetchDetailByID(topicID);
    }

    @GetMapping("/comments/children/{parentId}")
    public ResponseEntity<List<CommentDTO>> getChildComments(@PathVariable int parentId) {
        List<CommentDTO> children = topicService.fetchChildComments(parentId);
        return ResponseEntity.ok(children);
    }

}
