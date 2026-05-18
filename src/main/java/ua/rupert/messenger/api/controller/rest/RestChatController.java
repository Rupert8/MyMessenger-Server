package ua.rupert.messenger.api.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.rupert.messenger.api.service.ChatService;
import ua.rupert.messenger.store.dto.ChatMessageDto;
import ua.rupert.messenger.store.dto.SearchUserDto;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RestChatController {
    private final ChatService chatService;

    @GetMapping({RestChatController.ApiPath.GET_NEW_USER})
    private ResponseEntity<List<SearchUserDto>> getNewUser(@RequestParam String username) {
        return ResponseEntity.ok(chatService.searchUserByName(username));
    }

    @GetMapping(RestChatController.ApiPath.GET_CHAT_HISTORY)
    private ResponseEntity<List<ChatMessageDto>> getChatHistory(@RequestParam String senderName,
                                                                @RequestParam String recipientName) {
        return ResponseEntity.ok(chatService.getChatHistory(senderName, recipientName));
    }

    @PostMapping(value = ApiPath.CHANGE_IMAGE)
    private ResponseEntity<String> changeImage(@RequestParam MultipartFile file,
                                               Authentication authentication)  {

        log.info("Changing image from {} to {}", file.getOriginalFilename(), file.getContentType());
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Файл не вибрано");
            }

            String fileName = chatService.saveFile(file, authentication.getName());


            return ResponseEntity.ok(fileName);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    private static class ApiPath {
        private static final String GET_CHAT_HISTORY = "/chatHistory";
        private static final String GET_NEW_USER = "/user";
        private static final String CHANGE_IMAGE = "/image/change";
    }
}
