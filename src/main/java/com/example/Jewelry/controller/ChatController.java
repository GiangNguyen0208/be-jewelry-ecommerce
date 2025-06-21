package com.example.Jewelry.controller;

import com.example.Jewelry.dao.ChatMessageDAO;
import com.example.Jewelry.dao.ProductDAO;
import com.example.Jewelry.dao.UserDAO;
import com.example.Jewelry.dto.ChatMessageDTO;
import com.example.Jewelry.entity.ChatMessage;
import com.example.Jewelry.entity.Product;
import com.example.Jewelry.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageDAO chatMessageDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ProductDAO productDAO;

//     @MessageMapping("/sendMessage")
//     public void sendMessage(@Payload ChatMessageDTO chatMessageDTO) {
//         // Validate sender and product

//         User sender = userDAO.findById(chatMessageDTO.getSenderId())
//                 .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
//         User recipient = userDAO.findById(chatMessageDTO.getRecipientId())
//                 .orElseThrow(() -> new IllegalArgumentException("Invalid recipient ID"));
//         Product product = productDAO.findById(chatMessageDTO.getProductId())
//                 .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

//         // Save message to database
//         ChatMessage chatMessage = new ChatMessage();
//         chatMessage.setContent(chatMessageDTO.getContent());
//         chatMessage.setSender(sender);
//         // chatMessage.setRecipient(recipient);
//         // chatMessage.setProduct(product);
//         chatMessageDAO.save(chatMessage);

//         // Convert to DTO for WebSocket
//         chatMessageDTO.setId(chatMessage.getId());
//         chatMessageDTO.setSentAt(chatMessage.getSentAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

//         // Send to both sender and recipient's topic
//         String destination = String.format("/topic/%d/%d/%d",
//                 chatMessageDTO.getProductId(),
//                 chatMessageDTO.getSenderId(),
//                 chatMessageDTO.getRecipientId());
//         messagingTemplate.convertAndSend(destination, chatMessageDTO);

//         String recipientDestination = String.format("/topic/%d/%d/%d",
//                 chatMessageDTO.getProductId(),
//                 chatMessageDTO.getRecipientId(),
//                 chatMessageDTO.getSenderId());
//         messagingTemplate.convertAndSend(recipientDestination, chatMessageDTO);
//     }
//     @GetMapping("/history")
//     public List<ChatMessageDTO> getChatHistory(
//             @RequestParam int productId,
//             @RequestParam int senderId,
//             @RequestParam int recipientId) {
//         List<ChatMessage> messages = chatMessageDAO
//                 .findByProductIdAndSenderIdAndRecipientIdOrRecipientIdAndSenderId(
//                         productId, senderId, recipientId, recipientId, senderId);
//         return messages.stream().map(msg -> {
//             ChatMessageDTO dto = new ChatMessageDTO();
//             dto.setId(msg.getId());
//             dto.setContent(msg.getContent());
//             dto.setSenderId(msg.getSender().getId());
//         //     dto.setRecipientId(msg.getRecipient().getId());
//         //     dto.setProductId(msg.getProduct().getId());
//             dto.setSentAt(msg.getSentAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//             return dto;
//         }).collect(Collectors.toList());
//     }
}
