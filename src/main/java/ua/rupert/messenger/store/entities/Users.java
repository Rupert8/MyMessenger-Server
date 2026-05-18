package ua.rupert.messenger.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagePath;

    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String password;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    @ManyToMany(mappedBy = "users",  fetch = FetchType.LAZY)
    private List<Chat> chats =  new ArrayList<>();
}
