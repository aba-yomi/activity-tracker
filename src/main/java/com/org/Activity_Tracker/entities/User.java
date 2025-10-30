package com.org.Activity_Tracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.org.Activity_Tracker.enums.Gender;
import com.org.Activity_Tracker.enums.Role;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Builder
@AllArgsConstructor
//@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    // constructors, getters, setters
    public User() {}
    public User(String username, String email, String password, Set<com.org.Activity_Tracker.enums.Role> roles) {
        this.username = username; this.email = email; this.password = password; this.roles = roles;
    }
    // getters and setters omitted for brevity
}
