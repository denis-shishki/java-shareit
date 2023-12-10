package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
  //  @Positive
  //  private long ownerId;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NonNull
    private Boolean available;
    @Positive
    private Long requestId;
}
