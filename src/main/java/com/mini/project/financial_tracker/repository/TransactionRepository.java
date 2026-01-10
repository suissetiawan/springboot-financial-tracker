package com.mini.project.financial_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.mini.project.financial_tracker.entity.CategoryType;
import com.mini.project.financial_tracker.entity.Transaction;
import com.mini.project.financial_tracker.entity.User;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByUser(User user);
    List<Transaction> findAllByCategoryId(UUID categoryId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.category.type = :type")
    Double sumAmountByUserIdAndCategoryType(@Param("userId") UUID userId, @Param("type") CategoryType type);

}
