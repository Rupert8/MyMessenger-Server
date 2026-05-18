package ua.rupert.messenger.store.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.rupert.messenger.store.dto.ChatDto;
import ua.rupert.messenger.store.entities.Chat;
import ua.rupert.messenger.store.entities.Users;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatMapper {
    private final ChatMessageMapper mapper;

    public List<ChatDto> chatToChatDto(List<Chat> chats) {
        var chatsDto = new ArrayList<ChatDto>();

        for(Chat chat : chats){
            var chatDto = ChatDto.builder()
                    .id(chat.getId())
                    .keyName(chat.getKeyName())
                    .displayName(chat.getDisplayName())
                    .usersId(chat.getUsers().stream()
                            .map(Users::getId)
                            .toList())
                    .messages(mapper.chatMessageToChatMessageDto(chat.getMessage()))
                    .build();

            chatsDto.add(chatDto);
        }

        return chatsDto;
    }
}
