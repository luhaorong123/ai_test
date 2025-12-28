package com.library.dto.alert;

import com.library.entity.BorrowRecord;
import com.library.enums.BorrowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponse {

    private Long recordId;
    private Long userId;
    private String username;
    private String userName;
    private String userEmail;
    private Long itemId;
    private String itemTitle;
    private String itemIsbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowStatus status;
    private long overdueDays;        // 已逾期天数（逾期接口返回正数，未逾期返回0）
    private long daysUntilDue;      // 距离到期天数（即将到期接口返回正数，已逾期返回负数）

    public static AlertResponse from(BorrowRecord record, long overdueDays, long daysUntilDue) {
        return new AlertResponse(
                record.getId(),
                record.getUser().getId(),
                record.getUser().getUsername(),
                record.getUser().getName(),
                record.getUser().getEmail(),
                record.getItem().getId(),
                record.getItem().getTitle(),
                record.getItem().getIsbn(),
                record.getBorrowDate(),
                record.getDueDate(),
                record.getReturnDate(),
                record.getStatus(),
                overdueDays,
                daysUntilDue
        );
    }
}