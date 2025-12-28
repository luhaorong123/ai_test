package com.library.dto.borrow;

import com.library.entity.BorrowRecord;
import com.library.enums.BorrowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowResponse {

    private Long id;
    private Long userId;
    private String username;
    private Long itemId;
    private String itemTitle;
    private String itemIsbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowStatus status;
    private Integer renewCount;
    private long overdueDays; // 计算得到的逾期天数

    public static BorrowResponse from(BorrowRecord record, long overdueDays) {
        return new BorrowResponse(
                record.getId(),
                record.getUser().getId(),
                record.getUser().getUsername(),
                record.getItem().getId(),
                record.getItem().getTitle(),
                record.getItem().getIsbn(),
                record.getBorrowDate(),
                record.getDueDate(),
                record.getReturnDate(),
                record.getStatus(),
                record.getRenewCount(),
                overdueDays
        );
    }
}