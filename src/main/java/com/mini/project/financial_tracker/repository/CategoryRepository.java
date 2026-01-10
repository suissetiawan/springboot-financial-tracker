package com.mini.project.financial_tracker.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.mini.project.financial_tracker.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByType(String type);

    Optional<Category> findByName(String name);
}
