package ua.rupert.messenger.store.mapper;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.rupert.messenger.store.dto.SearchUserDto;
import ua.rupert.messenger.store.dto.UserDto;
import ua.rupert.messenger.store.entities.Users;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ChatMapper mapper;

    public UserDto toProjectDto(Users users) {
        return UserDto.builder()
                .id(users.getId())
                .username(users.getUsername())
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .chats(mapper.chatToChatDto(users.getChats()))
                .created_at(users.getCreatedAt())
                .imagePath(users.getImagePath())
                .build();
    }

    public List<SearchUserDto> toSearchUserDto(List<Users> users) {
        List<SearchUserDto> searchUserDtoList = new ArrayList<>();

        for(Users user : users){
            var searchUserDto = SearchUserDto.builder()
                    .username(user.getUsername())
                    .imagePath(user.getImagePath())
                    .build();

            searchUserDtoList.add(searchUserDto);
        }

        return searchUserDtoList;
    }
}
