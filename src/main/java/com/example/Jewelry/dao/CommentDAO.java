package com.example.Jewelry.dao;

import com.example.Jewelry.entity.Comment;
import com.example.Jewelry.entity.Topic;
import jakarta.websocket.server.PathParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentDAO  extends JpaRepository<Comment, Integer> {
    @Query("SELECT c FROM Comment c WHERE c.topic.id = :topicID AND c.parentComment IS NULL")
    List<Comment> findByTopicIdAndParentCommentIsNull(@Param("topicID") int topicID);

    @Query("select c from Comment c where c.parentComment.id = :parentId")
    List<Comment> findByParentCommentId(@Param("parentId") int parentId);

}
