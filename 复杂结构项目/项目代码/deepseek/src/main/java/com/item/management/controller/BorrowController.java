// BorrowController.java - 借阅管理控制器
package com.item.management.controller;

import com.item.management.converter.BorrowConverter;
import com.item.management.dto.request.BorrowQueryRequest;
import com.item.management.dto.request.BorrowRequest;
import com.item.management.dto.request.RenewRequest;
import com.item.management.dto.request.ReturnRequest;
import com.item.management.dto.response.ApiResponse;
import com.item.management.dto.response.BorrowDetailDTO;
import com.item.management.dto.response.BorrowStatsDTO;
import com.item.management.dto.response.BorrowSummaryDTO;
import com.item.management.entity.BorrowRecord;
import com.item.management.entity.User;
import com.item.management.enums.BorrowStatus;
import com.item.management.service.BorrowService;
import com.item.management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
@Tag(name = "借阅管理", description = "物品借阅、归还、续借等操作API")
@Validated
public class BorrowController {

    private final BorrowService borrowService;
    private final UserService userService;
    private final BorrowConverter borrowConverter;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "借出物品", description = "用户借出物品")
    public ResponseEntity<ApiResponse<BorrowDetailDTO>> borrowItem(
            @Valid @RequestBody BorrowRequest request) {

        // 普通用户只能借出自己的物品
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isAdmin() && !currentUser.getId().equals(request.getUserId())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("无权为其他用户借阅物品", 403));
        }

        BorrowRecord borrowRecord = borrowService.borrowItem(request);
        BorrowDetailDTO result = borrowConverter.toDetailDTO(borrowRecord);

        return ResponseEntity.ok(ApiResponse.success(result, "物品借出成功"));
    }

    @PostMapping("/{borrowId}/return")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "归还物品", description = "归还借出的物品")
    public ResponseEntity<ApiResponse<BorrowDetailDTO>> returnItem(
            @PathVariable @Parameter(description = "借阅记录ID", required = true) Long borrowId,
            @Valid @RequestBody ReturnRequest request) {

        BorrowRecord borrowRecord = borrowService.getBorrowRecordById(borrowId);

        // 权限验证：普通用户只能归还自己的物品
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isAdmin() && !currentUser.getId().equals(borrowRecord.getUser().getId())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("无权归还此物品", 403));
        }

        BorrowRecord returnedRecord = borrowService.returnItem(borrowId, request);
        BorrowDetailDTO result = borrowConverter.toDetailDTO(returnedRecord);

        return ResponseEntity.ok(ApiResponse.success(result, "物品归还成功"));
    }

    @PostMapping("/{borrowId}/renew")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "续借物品", description = "续借已借出的物品")
    public ResponseEntity<ApiResponse<BorrowDetailDTO>> renewBorrow(
            @PathVariable @Parameter(description = "借阅记录ID", required = true) Long borrowId,
            @Valid @RequestBody RenewRequest request) {

        BorrowRecord borrowRecord = borrowService.getBorrowRecordById(borrowId);

        // 权限验证：普通用户只能续借自己的物品
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isAdmin() && !currentUser.getId().equals(borrowRecord.getUser().getId())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("无权续借此物品", 403));
        }

        BorrowRecord renewedRecord = borrowService.renewBorrow(borrowId, request.getNewExpectedReturnDate());
        BorrowDetailDTO result = borrowConverter.toDetailDTO(renewedRecord);

        return ResponseEntity.ok(ApiResponse.success(result, "物品续借成功"));
    }

    @PostMapping("/{borrowId}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "取消借阅", description = "取消进行中的借阅")
    public ResponseEntity<ApiResponse<BorrowDetailDTO>> cancelBorrow(
            @PathVariable @Parameter(description = "借阅记录ID", required = true) Long borrowId) {

        BorrowRecord borrowRecord = borrowService.getBorrowRecordById(borrowId);

        // 权限验证：普通用户只能取消自己的借阅
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isAdmin() && !currentUser.getId().equals(borrowRecord.getUser().getId())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("无权取消此借阅", 403));
        }

        BorrowRecord cancelledRecord = borrowService.cancelBorrow(borrowId);
        BorrowDetailDTO result = borrowConverter.toDetailDTO(cancelledRecord);

        return ResponseEntity.ok(ApiResponse.success(result, "借阅取消成功"));
    }

    @GetMapping("/{borrowId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取借阅记录详情", description = "根据ID获取借阅记录详细信息")
    public ResponseEntity<ApiResponse<BorrowDetailDTO>> getBorrowRecord(
            @PathVariable @Parameter(description = "借阅记录ID", required = true) Long borrowId) {

        BorrowRecord borrowRecord = borrowService.getBorrowRecordById(borrowId);

        // 权限验证：普通用户只能查看自己的借阅记录
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isAdmin() && !currentUser.getId().equals(borrowRecord.getUser().getId())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("无权查看此借阅记录", 403));
        }

        BorrowDetailDTO result = borrowConverter.toDetailDTO(borrowRecord);
        return ResponseEntity.ok(ApiResponse.success(result, "获取借阅记录成功"));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "分页查询借阅记录", description = "分页查询借阅记录，支持多种筛选条件")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getBorrowRecords(
            @Valid BorrowQueryRequest queryRequest) {

        User currentUser = userService.getCurrentUser();

        // 普通用户只能查看自己的借阅记录
        if (!currentUser.isAdmin()) {
            queryRequest.setUserId(currentUser.getId());
        }

        Page<BorrowRecord> borrowPage;

        // 根据查询条件执行不同的查询
        if (queryRequest.getUpcoming() != null && queryRequest.getUpcoming()) {
            // 查询即将到期的借阅
            borrowPage = borrowService.getUpcomingReturns(LocalDate.now(), queryRequest.toPageable());
        } else if (queryRequest.getOverdue() != null && queryRequest.getOverdue()) {
            // 查询逾期的借阅
            borrowPage = borrowService.getOverdueBorrowRecords(queryRequest.toPageable());
        } else if (queryRequest.getUserId() != null && queryRequest.getStatus() != null) {
            // 查询指定用户和状态的借阅
            borrowPage = borrowService.getBorrowRecordsByUserAndStatus(
                    queryRequest.getUserId(), queryRequest.getStatus(), queryRequest.toPageable());
        } else if (queryRequest.getUserId() != null) {
            // 查询指定用户的借阅
            borrowPage = borrowService.getBorrowRecordsByUser(
                    queryRequest.getUserId(), queryRequest.toPageable());
        } else if (queryRequest.getStatus() != null) {
            // 查询指定状态的借阅
            borrowPage = borrowService.getBorrowRecordsByStatus(
                    queryRequest.getStatus(), queryRequest.toPageable());
        } else {
            // 查询所有借阅（管理员）
            borrowPage = borrowService.getAllBorrowRecords(queryRequest.toPageable());
        }

        Page<BorrowSummaryDTO> resultPage = borrowPage.map(borrowConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "查询用户借阅记录", description = "查询指定用户的所有借阅记录")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getUserBorrowRecords(
            @PathVariable @Parameter(description = "用户ID", required = true) Long userId,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        // 权限验证：普通用户只能查看自己的借阅记录
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isAdmin() && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("无权查看其他用户的借阅记录", 403));
        }

        Page<BorrowRecord> borrowPage = borrowService.getBorrowRecordsByUser(
                userId, org.springframework.data.domain.PageRequest.of(page, size));

        Page<BorrowSummaryDTO> resultPage = borrowPage.map(borrowConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }

    @GetMapping("/item/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询物品借阅历史", description = "查询指定物品的所有借阅记录（仅管理员）")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getItemBorrowHistory(
            @PathVariable @Parameter(description = "物品ID", required = true) Long itemId,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<BorrowRecord> borrowPage = borrowService.getBorrowRecordsByItem(
                itemId, org.springframework.data.domain.PageRequest.of(page, size));

        Page<BorrowSummaryDTO> resultPage = borrowPage.map(borrowConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询逾期借阅记录", description = "查询所有逾期借阅记录（仅管理员）")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getOverdueBorrowRecords(
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<BorrowRecord> borrowPage = borrowService.getOverdueBorrowRecords(
                org.springframework.data.domain.PageRequest.of(page, size));

        Page<BorrowSummaryDTO> resultPage = borrowPage.map(borrowConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询即将到期借阅", description = "查询即将到期的借阅记录（仅管理员）")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getUpcomingReturns(
            @RequestParam(defaultValue = "3") @Parameter(description = "天数阈值") Integer days,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<BorrowRecord> borrowPage = borrowService.getUpcomingReturns(
                LocalDate.now(), org.springframework.data.domain.PageRequest.of(page, size));

        Page<BorrowSummaryDTO> resultPage = borrowPage.map(borrowConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取借阅统计信息", description = "获取系统借阅统计信息（仅管理员）")
    public ResponseEntity<ApiResponse<BorrowStatsDTO>> getBorrowStats() {
        BorrowStatsDTO stats = borrowService.getBorrowStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "获取借阅统计信息成功"));
    }

    @GetMapping("/user/{userId}/stats")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取用户借阅统计", description = "获取指定用户的借阅统计信息")
    public ResponseEntity<ApiResponse<BorrowStatsDTO>> getUserBorrowStats(
            @PathVariable @Parameter(description = "用户ID", required = true) Long userId) {

        // 权限验证：普通用户只能查看自己的统计信息
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isAdmin() && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("无权查看其他用户的统计信息", 403));
        }

        BorrowStatsDTO stats = borrowService.getUserBorrowStats(userId);
        return ResponseEntity.ok(ApiResponse.success(stats, "获取用户借阅统计成功"));
    }

    @GetMapping("/check/available/{itemId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "检查物品是否可借", description = "检查指定物品是否可借出")
    public ResponseEntity<ApiResponse<Boolean>> checkItemAvailable(
            @PathVariable @Parameter(description = "物品ID", required = true) Long itemId) {

        boolean isAvailable = borrowService.isItemAvailableForBorrow(itemId);
        return ResponseEntity.ok(ApiResponse.success(isAvailable, "检查物品可借状态成功"));
    }

    @GetMapping("/check/user/{userId}/can-borrow")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "检查用户能否借阅", description = "检查用户是否能借阅新物品（仅管理员）")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkUserCanBorrow(
            @PathVariable @Parameter(description = "用户ID", required = true) Long userId) {

        Map<String, Object> result = new HashMap<>();

        boolean canBorrowMore = borrowService.canUserBorrowMore(userId);
        boolean hasOverdue = borrowService.hasOverdueBorrowRecords(userId);
        long currentBorrows = borrowService.countBorrowRecordsByStatus(BorrowStatus.ACTIVE);

        result.put("canBorrowMore", canBorrowMore);
        result.put("hasOverdue", hasOverdue);
        result.put("currentBorrows", currentBorrows);
        result.put("maxBorrowItems", 5); // 从配置中获取

        return ResponseEntity.ok(ApiResponse.success(result, "检查用户借阅资格成功"));
    }

    @PostMapping("/batch/update-overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "批量更新逾期状态", description = "批量更新借阅记录的逾期状态（仅管理员）")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateOverdueStatus() {
        borrowService.batchUpdateOverdueStatus();

        Map<String, Object> result = new HashMap<>();
        result.put("message", "批量更新逾期状态完成");

        return ResponseEntity.ok(ApiResponse.success(result, "批量更新成功"));
    }
}