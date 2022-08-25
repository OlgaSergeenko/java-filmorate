package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    public void addLike(long id) {
        likes.add(id);
    }

    public int getLikesSize() {
        return likes.size();
    }
}
