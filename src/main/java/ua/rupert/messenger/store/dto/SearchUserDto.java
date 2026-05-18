package ua.rupert.messenger.store.dto;

import lombok.Builder;

@Builder
public record SearchUserDto(String imagePath,
                            String username) {

}
