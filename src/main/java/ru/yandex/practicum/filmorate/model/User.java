package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private long id;
    @Email
    @NotNull
    @NonNull
    private String email;
    @NonNull
    @Pattern(regexp = "^\\S+$")
    private String login;
    private String name;
    @NonNull
    @Past
    private LocalDate birthday;
}
