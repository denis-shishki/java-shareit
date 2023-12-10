package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
   // private Long id;
    @NotEmpty
    private String text;
//    @NotEmpty
//    private String authorName;
//    @FutureOrPresent
//    private LocalDateTime created;
}
