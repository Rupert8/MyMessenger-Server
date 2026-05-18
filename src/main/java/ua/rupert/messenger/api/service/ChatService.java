package ua.rupert.messenger.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.rupert.messenger.api.exception.NotFoundException;
import ua.rupert.messenger.store.dto.ChatMessageDto;
import ua.rupert.messenger.store.dto.SearchUserDto;
import ua.rupert.messenger.store.entities.Chat;
import ua.rupert.messenger.store.entities.ChatMessage;
import ua.rupert.messenger.store.entities.MessageStatus;
import ua.rupert.messenger.store.entities.Users;
import ua.rupert.messenger.store.mapper.ChatMapper;
import ua.rupert.messenger.store.mapper.ChatMessageMapper;
import ua.rupert.messenger.store.mapper.UserMapper;
import ua.rupert.messenger.store.repository.ChatRepository;
import ua.rupert.messenger.store.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final UserMapper userMapper;


    public void saveMessage(ChatMessageDto requestMassage, String senderName) {
        var sender = this.userRepository.findByUsername(senderName)
                .orElseThrow(() -> new NotFoundException("Sender not found"));
        log.info(requestMassage.recipientName());
        var recipient = this.userRepository.findByUsername(requestMassage.recipientName())
                .orElseThrow(() -> new NotFoundException("Recipient not found"));

        String keyName = sender.getId() < recipient.getId() ? sender.getId() + "_" + recipient.getId() : recipient.getId() + "_" + sender.getId();

        var chat = getExistChatOrCreateNewChat(keyName, sender, recipient);

        var chatMessage = ChatMessage.builder()
                .senderName(senderName)
                .recipientName(requestMassage.recipientName())
                .content(requestMassage.content())
                .status(MessageStatus.RECEIVED)
                .build();

        chat.getMessage().add(chatMessage);
        chatRepository.saveAndFlush(chat);
    }

    private Chat getExistChatOrCreateNewChat(String keyName, Users sender, Users recipient) {
        return chatRepository.findBykeyName(keyName)
                .orElseGet(() -> {
                    Chat newChat = Chat.builder()
                            .keyName(keyName)
                            .displayName(sender.getUsername() + "&" + recipient.getUsername())
                            .users(new ArrayList<>(List.of(sender, recipient)))
                            .build();
                    sender.getChats().add(newChat);
                    recipient.getChats().add(newChat);
                    userRepository.save(sender);
                    userRepository.save(recipient);

                    return chatRepository.save(newChat);
                });
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getChatHistory(String senderUsername, String recipientUsername) {
        var senderId = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new UsernameNotFoundException(senderUsername))
                .getId();
        var recipientId =  userRepository.findByUsername(recipientUsername)
                .orElseThrow(() -> new UsernameNotFoundException(recipientUsername))
                .getId();

        var keyName = senderId < recipientId ? senderId + "_" + recipientId : recipientId + "_" + senderId;
        var chat = chatRepository.findBykeyName(keyName)
                .orElseThrow(() -> new NotFoundException(("Chat doesn't exist")));

        return chatMessageMapper.chatMessageToChatMessageDto(chat.getMessage());
    }

    @Transactional(readOnly = true)
    public List<SearchUserDto> searchUserByName(String userName) {
        var user = userRepository.findAllByUsernameStartingWithIgnoreCase(userName);

        if(user.isEmpty()) {
            throw new NotFoundException(userName);
        }

        return userMapper.toSearchUserDto(user);
    }

    @Transactional
    public String saveFile(MultipartFile file, String username) throws IOException {
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String fileName = UUID.randomUUID() + extension;

        Path path = Paths.get("uploads/avatars");
        if(!Files.exists(path)) {
            Files.createDirectories(path);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = path.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setImagePath(fileName);

        userRepository.save(user);

        return fileName;
    }
}
