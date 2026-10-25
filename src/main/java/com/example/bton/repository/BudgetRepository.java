package com.example.bton.repository;

import com.example.bton.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findBySpaceIdAndMonthAndYear(Long spaceId, int month, int year);

    Optional<Budget> findBySpaceIdAndCategoryIdAndMonthAndYear(Long spaceId, Long categoryId,
                                                                int month, int year);

    List<Budget> findBySpaceId(Long spaceId);

    boolean existsBySpaceIdAndCategoryIdAndMonthAndYear(Long spaceId, Long categoryId,
                                                         int month, int year);

    @Query("""
            SELECT b FROM Budget b
            JOIN FETCH b.category
            WHERE b.space.id = :spaceId
            AND b.month = :month AND b.year = :year
            ORDER BY b.category.name ASC
            """)
    List<Budget> findBySpaceAndMonthWithCategory(@Param("spaceId") Long spaceId,
                                                  @Param("month") int month,
                                                  @Param("year") int year);
}
