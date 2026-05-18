package ua.rupert.messenger.store.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class UserDto {
    @NotNull
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private List<ChatDto> chats;

    @NotNull
    @JsonProperty("create_date")
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "UTC")
    private Instant created_at;

    @NotBlank
    private String imagePath;
}
