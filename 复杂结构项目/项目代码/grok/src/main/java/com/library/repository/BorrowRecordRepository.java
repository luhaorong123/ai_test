package com.library.repository;

import com.library.entity.BorrowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByUserId(Long userId);

    Page<BorrowRecord> findByReturnDateIsNullAndDueDateBefore(LocalDate date, Pageable pageable);

    Page<BorrowRecord> findByReturnDateIsNullAndDueDateBetween(LocalDate start, LocalDate end, Pageable pageable);

    // 新增：支持按用户查询
    Page<BorrowRecord> findByUserIdAndReturnDateIsNullAndDueDateBefore(Long userId, LocalDate date, Pageable pageable);

    Page<BorrowRecord> findByUserIdAndReturnDateIsNullAndDueDateBetween(Long userId, LocalDate start, LocalDate end, Pageable pageable);
}