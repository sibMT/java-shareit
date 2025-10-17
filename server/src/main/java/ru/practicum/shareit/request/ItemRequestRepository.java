package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequester_IdOrderByCreatedDesc(Long requesterId);

    Page<ItemRequest> findByRequester_IdNotOrderByCreatedDesc(Long requesterId, Pageable pageable);
}

