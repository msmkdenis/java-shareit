package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findItemsByOwnerId(int userId);

    @Query("select i from Item i " +
           "where i.available = true and (upper(i.name) like upper(concat('%', ?1, '%')) " +
           "or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> searchItemsByText(String text);

    @Query(" select i from Item i " +
            "where i.request.id = ?1 " +
            "order by i.id desc")
    List<Item> findAllByRequesterId(int id);
}
