package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class User {
    private int id;
    @Email
    @NotNull
    @NonNull
    private String email;
    @NonNull
    @Pattern(regexp = "^\\S+$")
    private String login;
    @NonNull
    private String name;
    @NonNull
    @Past
    private LocalDate birthday;
}
