package com.example.bton.repository;

import com.example.bton.entity.Transaction;
import com.example.bton.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
            SELECT t FROM Transaction t
            JOIN FETCH t.category
            WHERE t.space.id = :spaceId
            AND (:type IS NULL OR t.type = :type)
            AND (:categoryId IS NULL OR t.category.id = :categoryId)
            AND (:startDate IS NULL OR t.transactionDate >= :startDate)
            AND (:endDate IS NULL OR t.transactionDate <= :endDate)
            AND (:keyword IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY t.transactionDate DESC, t.createdAt DESC
            """)
    Page<Transaction> findBySpaceWithFilters(
            @Param("spaceId") Long spaceId,
            @Param("type") TransactionType type,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.space.id = :spaceId
            AND t.type = :type
            AND YEAR(t.transactionDate) = :year
            AND MONTH(t.transactionDate) = :month
            """)
    BigDecimal sumBySpaceAndTypeAndMonth(@Param("spaceId") Long spaceId,
                                         @Param("type") TransactionType type,
                                         @Param("year") int year,
                                         @Param("month") int month);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.space.id = :spaceId
            AND t.type = 'EXPENSE'
            AND t.category.id = :categoryId
            AND YEAR(t.transactionDate) = :year
            AND MONTH(t.transactionDate) = :month
            """)
    BigDecimal sumExpenseBySpaceCategoryAndMonth(@Param("spaceId") Long spaceId,
                                                  @Param("categoryId") Long categoryId,
                                                  @Param("year") int year,
                                                  @Param("month") int month);

    @Query("""
            SELECT t FROM Transaction t
            JOIN FETCH t.category
            WHERE t.space.id = :spaceId
            ORDER BY t.transactionDate DESC, t.createdAt DESC
            """)
    List<Transaction> findRecentBySpace(@Param("spaceId") Long spaceId, Pageable pageable);

    @Query("""
            SELECT t.category.id, t.category.name, t.category.nameVi, t.category.color, t.category.icon,
                   COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.space.id = :spaceId
            AND t.type = 'EXPENSE'
            AND YEAR(t.transactionDate) = :year
            AND MONTH(t.transactionDate) = :month
            GROUP BY t.category.id, t.category.name, t.category.nameVi, t.category.color, t.category.icon
            ORDER BY SUM(t.amount) DESC
            """)
    List<Object[]> findExpenseByCategory(@Param("spaceId") Long spaceId,
                                          @Param("year") int year,
                                          @Param("month") int month);

    @Query("""
            SELECT YEAR(t.transactionDate), MONTH(t.transactionDate),
                   COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0),
                   COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0)
            FROM Transaction t
            WHERE t.space.id = :spaceId
            AND t.transactionDate BETWEEN :startDate AND :endDate
            GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate)
            ORDER BY YEAR(t.transactionDate), MONTH(t.transactionDate)
            """)
    List<Object[]> findMonthlyTotals(@Param("spaceId") Long spaceId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    long countByDateRange(@Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT YEAR(t.transactionDate), MONTH(t.transactionDate), COUNT(t)
            FROM Transaction t
            WHERE t.transactionDate >= :since
            GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate)
            ORDER BY YEAR(t.transactionDate), MONTH(t.transactionDate)
            """)
    List<Object[]> countTransactionsByMonth(@Param("since") LocalDate since);
}
