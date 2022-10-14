package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Builder
public class Film {
    private long id;
    @NonNull
    @NotEmpty
    @NotBlank
    @Length(max = 50)
    private String name;
    @NonNull
    @Length(max = 200)
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    @Positive
    private int duration;
    @NonNull
    private Mpa mpa;
    @Builder.Default
    private List<Genre> genres = new ArrayList<>();
    @Builder.Default
    private List<Director> directors = new ArrayList<>();
}
