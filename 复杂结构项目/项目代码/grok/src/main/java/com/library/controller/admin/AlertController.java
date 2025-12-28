package com.library.controller.admin; // 预警模块建议仅管理员可见

import com.library.common.ApiResponse;
import com.library.dto.alert.AlertResponse;
import com.library.entity.BorrowRecord;
import com.library.service.AlertService;
import com.library.service.BorrowService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/admin/alerts")
@PreAuthorize("hasRole('ADMIN')") // 仅管理员可查看全库预警
public class AlertController {

    private final AlertService alertService;
    private final BorrowService borrowService;

    public AlertController(AlertService alertService, BorrowService borrowService) {
        this.alertService = alertService;
        this.borrowService = borrowService;
    }

    /**
     * 已逾期记录（全部用户）
     */
    @GetMapping("/overdue")
    public ApiResponse<Page<AlertResponse>> getOverdueAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<BorrowRecord> records = alertService.getOverdueRecords(page, size);

        Page<AlertResponse> response = records.map(record -> {
            long overdueDays = borrowService.calculateOverdueDays(record);
            long daysUntilDue = 0; // 已逾期，无需显示
            return AlertResponse.from(record, overdueDays, daysUntilDue);
        });

        return ApiResponse.success(response);
    }

    /**
     * 3天内即将到期记录（全部用户）
     */
    @GetMapping("/upcoming")
    public ApiResponse<Page<AlertResponse>> getUpcomingAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<BorrowRecord> records = alertService.getUpcomingDueRecords(page, size);

        Page<AlertResponse> response = records.map(record -> {
            long overdueDays = 0;
            long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), record.getDueDate());
            return AlertResponse.from(record, overdueDays, daysUntilDue);
        });

        return ApiResponse.success(response);
    }

    /**
     * 指定用户的逾期记录
     */
    @GetMapping("/overdue/user/{userId}")
    public ApiResponse<Page<AlertResponse>> getUserOverdueAlerts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<BorrowRecord> records = alertService.getOverdueRecordsByUser(userId, page, size);

        Page<AlertResponse> response = records.map(record -> {
            long overdueDays = borrowService.calculateOverdueDays(record);
            return AlertResponse.from(record, overdueDays, 0);
        });

        return ApiResponse.success(response);
    }

    /**
     * 指定用户的即将到期记录
     */
    @GetMapping("/upcoming/user/{userId}")
    public ApiResponse<Page<AlertResponse>> getUserUpcomingAlerts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<BorrowRecord> records = alertService.getUpcomingDueRecordsByUser(userId, page, size);

        Page<AlertResponse> response = records.map(record -> {
            long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), record.getDueDate());
            return AlertResponse.from(record, 0, daysUntilDue);
        });

        return ApiResponse.success(response);
    }
}