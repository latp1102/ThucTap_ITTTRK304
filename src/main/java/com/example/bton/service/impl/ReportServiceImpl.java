package com.example.bton.service.impl;

import com.example.bton.dto.response.DashboardResponse;
import com.example.bton.dto.response.ReportResponse;
import com.example.bton.enums.TransactionType;
import com.example.bton.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl {

    private final TransactionRepository transactionRepository;
    private final BudgetServiceImpl budgetService;
    private final SavingsGoalServiceImpl savingsGoalService;
    private final FinancialSpaceServiceImpl spaceService;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long spaceId, Long userId, int month, int year) {
        spaceService.verifySpaceAccess(spaceId, userId);

        BigDecimal totalIncome = transactionRepository
                .sumBySpaceAndTypeAndMonth(spaceId, TransactionType.INCOME, year, month);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;

        BigDecimal totalExpense = transactionRepository
                .sumBySpaceAndTypeAndMonth(spaceId, TransactionType.EXPENSE, year, month);
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal balance = totalIncome.subtract(totalExpense);

        // Expense by category
        List<Object[]> rawCategoryExpenses = transactionRepository
                .findExpenseByCategory(spaceId, year, month);
        List<DashboardResponse.CategoryExpenseResponse> categoryExpenses = buildCategoryExpenses(
                rawCategoryExpenses, totalExpense);

        // Budgets with warnings
        var budgets = budgetService.getBudgets(spaceId, userId, month, year);
        var warnings = budgets.stream()
                .filter(b -> !"NORMAL".equals(b.getStatus()))
                .collect(Collectors.toList());

        // Savings goals
        var goals = savingsGoalService.getGoals(spaceId, userId);

        // Recent 10 transactions
        var recent = transactionRepository.findRecentBySpace(spaceId, PageRequest.of(0, 10));
        var recentResponses = recent.stream()
                .map(TransactionServiceImpl::mapToResponseStatic)
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .expenseByCategory(categoryExpenses)
                .budgets(budgets)
                .budgetWarnings(warnings)
                .savingsGoals(goals)
                .recentTransactions(recentResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(Long spaceId, Long userId,
                                    LocalDate startDate, LocalDate endDate) {
        spaceService.verifySpaceAccess(spaceId, userId);

        List<Object[]> monthlyData = transactionRepository.findMonthlyTotals(spaceId, startDate, endDate);

        List<ReportResponse.MonthlySummary> summaries = new ArrayList<>();
        List<ReportResponse.TrendData> trends = new ArrayList<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Object[] row : monthlyData) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            BigDecimal income = row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO;
            BigDecimal expense = row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO;

            totalIncome = totalIncome.add(income);
            totalExpense = totalExpense.add(expense);

            summaries.add(ReportResponse.MonthlySummary.builder()
                    .year(year).month(month).income(income)
                    .expense(expense).balance(income.subtract(expense)).build());

            trends.add(ReportResponse.TrendData.builder()
                    .label(year + "-" + String.format("%02d", month))
                    .income(income).expense(expense)
                    .balance(income.subtract(expense)).build());
        }

        // Expense by category for the whole period
        // Query most recent month for category breakdown
        List<Object[]> rawCategoryExpenses = transactionRepository.findExpenseByCategory(
                spaceId, endDate.getYear(), endDate.getMonthValue());
        List<ReportResponse.CategoryBreakdown> catBreakdowns = buildCategoryBreakdowns(
                rawCategoryExpenses, totalExpense);

        return ReportResponse.builder()
                .monthlySummaries(summaries)
                .trends(trends)
                .expenseByCategory(catBreakdowns)
                .incomeByCategory(List.of())
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalBalance(totalIncome.subtract(totalExpense))
                .build();
    }

    private List<DashboardResponse.CategoryExpenseResponse> buildCategoryExpenses(
            List<Object[]> rawData, BigDecimal totalExpense) {
        return rawData.stream().map(row -> {
            Long catId = ((Number) row[0]).longValue();
            String name = (String) row[1];
            String nameVi = (String) row[2];
            String color = (String) row[3];
            String icon = (String) row[4];
            BigDecimal amount = (BigDecimal) row[5];
            BigDecimal pct = totalExpense.compareTo(BigDecimal.ZERO) > 0
                    ? amount.multiply(new BigDecimal("100"))
                    .divide(totalExpense, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            return DashboardResponse.CategoryExpenseResponse.builder()
                    .categoryId(catId).categoryName(name).categoryNameVi(nameVi)
                    .color(color).icon(icon).amount(amount).percentage(pct).build();
        }).collect(Collectors.toList());
    }

    private List<ReportResponse.CategoryBreakdown> buildCategoryBreakdowns(
            List<Object[]> rawData, BigDecimal total) {
        return rawData.stream().map(row -> {
            Long catId = ((Number) row[0]).longValue();
            String name = (String) row[1];
            String nameVi = (String) row[2];
            String color = (String) row[3];
            String icon = (String) row[4];
            BigDecimal amount = (BigDecimal) row[5];
            BigDecimal pct = total.compareTo(BigDecimal.ZERO) > 0
                    ? amount.multiply(new BigDecimal("100"))
                    .divide(total, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            return ReportResponse.CategoryBreakdown.builder()
                    .categoryId(catId).categoryName(name).categoryNameVi(nameVi)
                    .color(color).icon(icon).amount(amount).percentage(pct).build();
        }).collect(Collectors.toList());
    }
}
