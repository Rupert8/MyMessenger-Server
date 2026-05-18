package ua.rupert.messenger.api.controller.stomp;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ua.rupert.messenger.api.service.ChatService;
import ua.rupert.messenger.store.dto.ChatMessageDto;
import ua.rupert.messenger.store.dto.ResponseMessage;

import java.security.Principal;
import java.time.Instant;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @Transactional
    @MessageMapping("/chat.send")
    public void processMessage(@Payload ChatMessageDto requestMassage, Principal principal) {
        String senderName = principal.getName();

        log.info("Sending message to chat: {}", requestMassage);
        chatService.saveMessage(requestMassage, senderName);

        var responseMessage = new ResponseMessage(
                senderName,
                requestMassage.content(),
                Instant.now()
        );

        messagingTemplate.convertAndSendToUser(
                requestMassage.recipientName(),
                "/queue/messages",
                responseMessage);
    }

}
