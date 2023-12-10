package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.group.BasicInfo;
import ru.practicum.shareit.group.InfoForUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    private Long id;
    @NotNull(groups = BasicInfo.class)
    private String name;
    @NotNull(groups = BasicInfo.class)
    @Email(groups = BasicInfo.class)
    @Email(groups = InfoForUpdate.class)
    private String email;
}
