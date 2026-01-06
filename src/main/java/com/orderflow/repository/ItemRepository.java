package com.orderflow.repository;

import com.orderflow.domain.Item;
import com.orderflow.domain.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Item entity operations.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findBySku(String sku);

    List<Item> findByCategory(ItemCategory category);

    boolean existsBySku(String sku);
}
