// AlertController.java - 预警管理控制器
package com.item.management.controller;

import com.item.management.dto.request.AlertQueryRequest;
import com.item.management.dto.response.AlertStatsDTO;
import com.item.management.dto.response.AlertSummaryDTO;
import com.item.management.dto.response.ApiResponse;
import com.item.management.dto.response.BorrowSummaryDTO;
import com.item.management.entity.User;
import com.item.management.service.AlertService;
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
import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "预警管理", description = "预警查询和通知API")
@Validated
public class AlertController {

    private final AlertService alertService;
    private final UserService userService;

    @GetMapping("/upcoming-returns")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询即将到期记录", description = "查询即将到期的借阅记录（仅管理员）")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getUpcomingReturns(
            @RequestParam(required = false, defaultValue = "3")
            @Parameter(description = "天数阈值，默认3天") Integer days,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<BorrowSummaryDTO> result = alertService.getUpcomingReturns(days,
                org.springframework.data.domain.PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/upcoming-returns/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询用户即将到期记录", description = "查询指定用户即将到期的借阅记录（仅管理员）")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getUpcomingReturnsByUser(
            @PathVariable @Parameter(description = "用户ID", required = true) Long userId,
            @RequestParam(required = false, defaultValue = "3")
            @Parameter(description = "天数阈值，默认3天") Integer days,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<BorrowSummaryDTO> result = alertService.getUpcomingReturnsByUser(userId, days,
                org.springframework.data.domain.PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询逾期记录", description = "查询所有逾期借阅记录（仅管理员）")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getOverdueBorrowRecords(
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<BorrowSummaryDTO> result = alertService.getOverdueBorrowRecords(
                org.springframework.data.domain.PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/overdue/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "查询用户逾期记录", description = "查询指定用户的逾期借阅记录")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getOverdueBorrowRecordsByUser(
            @PathVariable @Parameter(description = "用户ID", required = true) Long userId,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        // 权限验证：普通用户只能查看自己的逾期记录
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isAdmin() && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("无权查看其他用户的逾期记录", 403));
        }

        Page<BorrowSummaryDTO> result = alertService.getOverdueBorrowRecordsByUser(userId,
                org.springframework.data.domain.PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询所有预警记录", description = "查询所有预警记录（包括即将到期和逾期）（仅管理员）")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getAllAlerts(
            @Valid AlertQueryRequest queryRequest) {

        Page<BorrowSummaryDTO> result;

        if (queryRequest.getAlertType() != null) {
            switch (queryRequest.getAlertType().toUpperCase()) {
                case "UPCOMING":
                    result = alertService.getUpcomingReturns(queryRequest.getDaysThreshold(),
                            queryRequest.toPageable());
                    break;
                case "OVERDUE":
                    result = alertService.getOverdueBorrowRecords(queryRequest.toPageable());
                    break;
                case "ALL":
                default:
                    result = alertService.getAllAlerts(queryRequest.toPageable());
                    break;
            }
        } else {
            result = alertService.getAllAlerts(queryRequest.toPageable());
        }

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "查询用户所有预警记录", description = "查询指定用户的所有预警记录")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getAlertsByUser(
            @PathVariable @Parameter(description = "用户ID", required = true) Long userId,
            @Valid AlertQueryRequest queryRequest) {

        // 权限验证：普通用户只能查看自己的预警记录
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isAdmin() && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("无权查看其他用户的预警记录", 403));
        }

        Page<BorrowSummaryDTO> result = alertService.getAlertsByUser(userId, queryRequest.toPageable());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取预警统计信息", description = "获取系统预警统计信息（仅管理员）")
    public ResponseEntity<ApiResponse<AlertStatsDTO>> getAlertStats() {
        AlertStatsDTO stats = alertService.getAlertStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "获取预警统计信息成功"));
    }

    @GetMapping("/stats/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取用户预警统计", description = "获取指定用户的预警统计信息")
    public ResponseEntity<ApiResponse<AlertStatsDTO>> getUserAlertStats(
            @PathVariable @Parameter(description = "用户ID", required = true) Long userId) {

        // 权限验证：普通用户只能查看自己的统计信息
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isAdmin() && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("无权查看其他用户的统计信息", 403));
        }

        AlertStatsDTO stats = alertService.getUserAlertStats(userId);
        return ResponseEntity.ok(ApiResponse.success(stats, "获取用户预警统计成功"));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取预警摘要", description = "获取预警摘要信息（仅管理员）")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAlertSummary() {
        Map<String, Object> summary = alertService.getAlertSummary();
        return ResponseEntity.ok(ApiResponse.success(summary, "获取预警摘要成功"));
    }

    @GetMapping("/daily")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取每日预警", description = "获取每日生成的预警信息（仅管理员）")
    public ResponseEntity<ApiResponse<List<AlertSummaryDTO>>> getDailyAlerts() {
        List<AlertSummaryDTO> dailyAlerts = alertService.getDailyAlerts();
        return ResponseEntity.ok(ApiResponse.success(dailyAlerts, "获取每日预警成功"));
    }

    @PostMapping("/notifications/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "发送逾期通知", description = "发送逾期通知给相关用户（仅管理员）")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendOverdueNotifications() {
        alertService.sendOverdueNotifications();

        Map<String, Object> result = new HashMap<>();
        result.put("message", "逾期通知发送任务已启动");
        result.put("timestamp", java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(ApiResponse.success(result, "逾期通知发送任务已启动"));
    }

    @PostMapping("/notifications/upcoming")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "发送即将到期通知", description = "发送即将到期通知给相关用户（仅管理员）")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendUpcomingNotifications() {
        alertService.sendUpcomingNotifications();

        Map<String, Object> result = new HashMap<>();
        result.put("message", "即将到期通知发送任务已启动");
        result.put("timestamp", java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(ApiResponse.success(result, "即将到期通知发送任务已启动"));
    }

    @GetMapping("/check/upcoming")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "检查是否有即将到期记录", description = "检查当前用户是否有即将到期的借阅记录")
    public ResponseEntity<ApiResponse<Boolean>> checkUpcomingReturns(
            @RequestParam(required = false, defaultValue = "3")
            @Parameter(description = "天数阈值，默认3天") Integer days) {

        User currentUser = userService.getCurrentUser();
        boolean hasUpcoming = alertService.hasUpcomingReturns(currentUser.getId(), days);

        return ResponseEntity.ok(ApiResponse.success(hasUpcoming, "检查即将到期记录成功"));
    }

    @GetMapping("/check/overdue")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "检查是否有逾期记录", description = "检查当前用户是否有逾期借阅记录")
    public ResponseEntity<ApiResponse<Boolean>> checkOverdueRecords() {
        User currentUser = userService.getCurrentUser();
        boolean hasOverdue = alertService.hasOverdueRecords(currentUser.getId());

        return ResponseEntity.ok(ApiResponse.success(hasOverdue, "检查逾期记录成功"));
    }

    @GetMapping("/count/upcoming")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "统计即将到期记录数量", description = "统计即将到期的借阅记录数量（仅管理员）")
    public ResponseEntity<ApiResponse<Map<String, Object>>> countUpcomingReturns(
            @RequestParam(required = false, defaultValue = "3")
            @Parameter(description = "天数阈值，默认3天") Integer days) {

        int count = alertService.countUpcomingReturns(days);

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("daysThreshold", days);

        return ResponseEntity.ok(ApiResponse.success(result, "统计即将到期记录成功"));
    }

    @GetMapping("/count/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "统计逾期记录数量", description = "统计逾期借阅记录数量（仅管理员）")
    public ResponseEntity<ApiResponse<Map<String, Object>>> countOverdueRecords() {
        int count = alertService.countOverdueRecords();

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);

        return ResponseEntity.ok(ApiResponse.success(result, "统计逾期记录成功"));
    }

    @GetMapping("/user/me/upcoming")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取当前用户即将到期记录", description = "获取当前登录用户的即将到期借阅记录")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getMyUpcomingReturns(
            @RequestParam(required = false, defaultValue = "3")
            @Parameter(description = "天数阈值，默认3天") Integer days,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        User currentUser = userService.getCurrentUser();
        Page<BorrowSummaryDTO> result = alertService.getUpcomingReturnsByUser(
                currentUser.getId(), days,
                org.springframework.data.domain.PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/user/me/overdue")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取当前用户逾期记录", description = "获取当前登录用户的逾期借阅记录")
    public ResponseEntity<ApiResponse<Page<BorrowSummaryDTO>>> getMyOverdueRecords(
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        User currentUser = userService.getCurrentUser();
        Page<BorrowSummaryDTO> result = alertService.getOverdueBorrowRecordsByUser(
                currentUser.getId(),
                org.springframework.data.domain.PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/user/me/stats")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取当前用户预警统计", description = "获取当前登录用户的预警统计信息")
    public ResponseEntity<ApiResponse<AlertStatsDTO>> getMyAlertStats() {
        User currentUser = userService.getCurrentUser();
        AlertStatsDTO stats = alertService.getUserAlertStats(currentUser.getId());

        return ResponseEntity.ok(ApiResponse.success(stats, "获取用户预警统计成功"));
    }
}