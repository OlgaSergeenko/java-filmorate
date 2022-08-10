package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    private int id;
    @NotNull
    @NotBlank
    @NotEmpty
    @Email
    private String email;
    @NotNull
    @NotEmpty
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

}
