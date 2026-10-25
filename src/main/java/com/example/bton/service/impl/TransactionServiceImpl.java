package com.example.bton.service.impl;

import com.example.bton.dto.request.TransactionRequest;
import com.example.bton.dto.response.PageResponse;
import com.example.bton.dto.response.TransactionResponse;
import com.example.bton.entity.Category;
import com.example.bton.entity.FinancialSpace;
import com.example.bton.entity.Transaction;
import com.example.bton.entity.User;
import com.example.bton.enums.TransactionType;
import com.example.bton.exception.AccessDeniedException;
import com.example.bton.exception.ResourceNotFoundException;
import com.example.bton.repository.CategoryRepository;
import com.example.bton.repository.FinancialSpaceRepository;
import com.example.bton.repository.TransactionRepository;
import com.example.bton.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl {

    private final TransactionRepository transactionRepository;
    private final FinancialSpaceRepository spaceRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final FinancialSpaceServiceImpl spaceService;

    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionRequest request) {
        spaceService.verifySpaceAccess(request.getSpaceId(), userId);

        FinancialSpace space = spaceRepository.findByIdAndIsActiveTrue(request.getSpaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Financial space", "id", request.getSpaceId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Transaction transaction = Transaction.builder()
                .space(space)
                .user(user)
                .category(category)
                .type(request.getType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .transactionDate(request.getTransactionDate())
                .note(request.getNote())
                .build();

        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getTransactions(
            Long spaceId, Long userId, TransactionType type,
            Long categoryId, LocalDate startDate, LocalDate endDate,
            String keyword, int page, int size) {

        spaceService.verifySpaceAccess(spaceId, userId);

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "transactionDate", "createdAt"));

        Page<Transaction> transactionPage = transactionRepository.findBySpaceWithFilters(
                spaceId, type, categoryId, startDate, endDate, keyword, pageable);

        return PageResponse.<TransactionResponse>builder()
                .content(transactionPage.getContent().stream()
                        .map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(transactionPage.getNumber())
                .pageSize(transactionPage.getSize())
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .first(transactionPage.isFirst())
                .last(transactionPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));

        spaceService.verifySpaceAccess(transaction.getSpace().getId(), userId);
        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long transactionId, Long userId, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));

        // Verify ownership of the original space
        spaceService.verifySpaceAccess(transaction.getSpace().getId(), userId);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        transaction.setCategory(category);
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setNote(request.getNote());

        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));

        spaceService.verifySpaceAccess(transaction.getSpace().getId(), userId);

        // Only the creator or space owner can delete
        if (!transaction.getUser().getId().equals(userId)) {
            FinancialSpace space = transaction.getSpace();
            if (!space.getOwner().getId().equals(userId)) {
                throw new AccessDeniedException("You don't have permission to delete this transaction");
            }
        }

        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getRecentTransactions(Long spaceId, Long userId, int limit) {
        spaceService.verifySpaceAccess(spaceId, userId);
        Pageable pageable = PageRequest.of(0, limit);
        return transactionRepository.findRecentBySpace(spaceId, pageable)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public TransactionResponse mapToResponse(Transaction t) {
        return mapToResponseStatic(t);
    }

    public static TransactionResponse mapToResponseStatic(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .spaceId(t.getSpace().getId())
                .spaceName(t.getSpace().getName())
                .userId(t.getUser().getId())
                .userName(t.getUser().getFullName())
                .category(CategoryServiceImpl.mapCategoryStatic(t.getCategory()))
                .type(t.getType())
                .amount(t.getAmount())
                .description(t.getDescription())
                .transactionDate(t.getTransactionDate())
                .note(t.getNote())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
