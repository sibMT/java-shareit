package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findById(Long id);

    List<Item> findItemByOwner_Id(Long ownerId);

    @Query("""
            SELECT i FROM Item i
             WHERE i.available = true
               AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%'))
                    OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))
             ORDER BY i.id DESC
            """)
    List<Item> searchItem(String text);

    List<Item> findByRequest_Id(Long requestId);

    List<Item> findByRequest_IdIn(List<Long> requestIds);
}
