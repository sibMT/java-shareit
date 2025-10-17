package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = "author")
    List<Comment> findAllByItem_IdOrderByCreatedDesc(Long itemId);

}
