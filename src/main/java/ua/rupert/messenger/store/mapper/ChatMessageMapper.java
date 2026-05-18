package ua.rupert.messenger.store.mapper;

import org.springframework.stereotype.Component;
import ua.rupert.messenger.store.dto.ChatMessageDto;
import ua.rupert.messenger.store.entities.ChatMessage;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChatMessageMapper {

    public List<ChatMessageDto> chatMessageToChatMessageDto(List<ChatMessage> chatMessage) {
        var messagesDto = new ArrayList<ChatMessageDto>();

        for(ChatMessage message : chatMessage) {
            var messageDto = ChatMessageDto.builder()
                    .senderName(message.getSenderName())
                    .recipientName(message.getRecipientName())
                    .content(message.getContent())
                    .status(message.getStatus())
                    .timestamp(message.getTimestamp())
                    .build();

            messagesDto.add(messageDto);
        }

        return messagesDto;
    }
}
