package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findRequestByRequestorIdOrderByCreatedDesc(int requestor);

    @Query("select r from requests r where r.requestor.id <> ?1")
    Page<ItemRequest> findAllForUser(int userId, Pageable pageable);
}
