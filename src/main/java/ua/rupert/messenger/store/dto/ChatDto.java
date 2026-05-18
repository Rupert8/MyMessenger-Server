package ua.rupert.messenger.store.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class ChatDto {
    @NotBlank
    private Long id;

    @NotBlank
    private String keyName;

    @NotBlank
    private List<Long> usersId;

    @NotBlank
    private String displayName;

    @NotBlank
    private List<ChatMessageDto> messages;

    @NotBlank
    private Instant createdAt;
}
