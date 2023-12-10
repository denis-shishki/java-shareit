package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public abstract class CommentMapper {

    public static Comment toComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = new Item();
        User author = new User();

        item.setId(itemId);
        author.setId(userId);

        return new Comment(commentDto.getId(),
                commentDto.getText(),
                item,
                author,
                commentDto.getCreated());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}
