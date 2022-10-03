package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
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
    private String name;
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    @Positive
    private int duration;
    private int rate;
    @NonNull
    private Mpa mpa;
    @Builder.Default
    private List<Genre> genres = new ArrayList<>();
    private List<Director> directors = new ArrayList<>();
}
