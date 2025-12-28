package com.library.controller;

import com.library.common.ApiResponse;
import com.library.dto.borrow.BorrowRequest;
import com.library.dto.borrow.BorrowResponse;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/borrow")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')") // 用户和管理员均可借阅
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * 借出图书
     */
    @PostMapping
    public ApiResponse<BorrowResponse> borrowItem(Authentication authentication,
                                                  @Valid @RequestBody BorrowRequest request) {
        Long userId = getCurrentUserId(authentication);

        BorrowRecord record = borrowService.borrowItem(userId, request.getItemId());
        long overdueDays = borrowService.calculateOverdueDays(record);

        return ApiResponse.success(BorrowResponse.from(record, overdueDays));
    }

    /**
     * 归还图书
     */
    @PostMapping("/return/{recordId}")
    public ApiResponse<BorrowResponse> returnItem(Authentication authentication,
                                                  @PathVariable Long recordId) {
        // 可额外校验是否是本人或管理员
        BorrowRecord record = borrowService.returnItem(recordId);
        long overdueDays = borrowService.calculateOverdueDays(record);

        return ApiResponse.success(BorrowResponse.from(record, overdueDays));
    }

    /**
     * 查询我的借阅记录
     */
    @GetMapping("/my")
    public ApiResponse<List<BorrowResponse>> getMyBorrowRecords(Authentication authentication,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId(authentication);

        List<BorrowRecord> records = borrowService.getUserBorrowRecords(userId, page, size);
        List<BorrowResponse> responses = records.stream()
                .peek(borrowService::updateBorrowStatus) // 确保状态最新
                .map(r -> BorrowResponse.from(r, borrowService.calculateOverdueDays(r)))
                .collect(Collectors.toList());

        return ApiResponse.success(responses);
    }

    /**
     * 查询单条借阅记录（管理员或本人）
     */
    @GetMapping("/{recordId}")
    public ApiResponse<BorrowResponse> getBorrowRecord(Authentication authentication,
                                                       @PathVariable Long recordId) {
        BorrowRecord record = borrowService.getBorrowRecord(recordId);
        long overdueDays = borrowService.calculateOverdueDays(record);

        // 权限校验：本人或管理员
        Long currentUserId = getCurrentUserId(authentication);
        if (!currentUserId.equals(record.getUser().getId()) && !isAdmin(authentication)) {
            throw new RuntimeException("无权限查看该记录");
        }

        return ApiResponse.success(BorrowResponse.from(record, overdueDays));
    }

    private Long getCurrentUserId(Authentication authentication) {
        // 假设 UserDetails 中存储了 id（实际项目可通过 CustomUserDetails 扩展）
        // 这里简化，实际请从 authentication.getPrincipal() 获取
        return Long.valueOf(authentication.getName()); // 临时方案，实际需调整
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}