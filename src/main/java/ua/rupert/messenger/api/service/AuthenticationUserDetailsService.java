package ua.rupert.messenger.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.rupert.messenger.api.exception.BadRequestException;
import ua.rupert.messenger.api.exception.NotFoundException;
import ua.rupert.messenger.store.dto.RegisterUserDto;
import ua.rupert.messenger.store.dto.UserDto;
import ua.rupert.messenger.store.entities.Users;
import ua.rupert.messenger.store.mapper.UserMapper;
import ua.rupert.messenger.store.model.UserPrincipal;
import ua.rupert.messenger.store.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationUserDetailsService implements UserDetailsService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new UserPrincipal(user);
    }

    public UserDto createUser(RegisterUserDto registerUserDto) {
        userRepository
                .findByUsername(registerUserDto.getUsername())
                .ifPresent(users -> {
                    throw new BadRequestException(String.format("Username %s already exists", registerUserDto.getUsername()));
                });

        var user = Users.builder()
                .username(registerUserDto.getUsername())
                .firstName(registerUserDto.getFirstName())
                .lastName(registerUserDto.getLastName())
                .password(encoder.encode(registerUserDto.getPassword()))
                .build();

        log.info("User created: {}", user);
        userRepository.saveAndFlush(user);

        return userMapper.toProjectDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserDetails(String userName) {
        var user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new NotFoundException(userName));

        return userMapper.toProjectDto(user);
    }
}

