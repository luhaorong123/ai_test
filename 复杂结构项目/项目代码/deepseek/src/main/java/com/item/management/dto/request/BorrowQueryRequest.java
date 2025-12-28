// BorrowQueryRequest.java - 借阅查询请求DTO
package com.item.management.dto.request;

import com.item.management.enums.BorrowStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

@Data
@Schema(description = "借阅查询请求")
public class BorrowQueryRequest {

    @Schema(description = "页码，从0开始", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "排序字段", example = "borrowDate", allowableValues = {"id", "borrowDate", "expectedReturnDate", "actualReturnDate"}, defaultValue = "borrowDate")
    private String sortBy = "borrowDate";

    @Schema(description = "排序方向", example = "DESC", allowableValues = {"ASC", "DESC"}, defaultValue = "DESC")
    private Sort.Direction direction = Sort.Direction.DESC;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "物品ID")
    private Long itemId;

    @Schema(description = "借阅状态", example = "ACTIVE")
    private BorrowStatus status;

    @Schema(description = "是否逾期", example = "false")
    private Boolean overdue = false;

    @Schema(description = "借出时间开始范围")
    private LocalDate borrowDateStart;

    @Schema(description = "借出时间结束范围")
    private LocalDate borrowDateEnd;

    @Schema(description = "预计归还时间开始范围")
    private LocalDate expectedReturnDateStart;

    @Schema(description = "预计归还时间结束范围")
    private LocalDate expectedReturnDateEnd;

    @Schema(description = "是否即将到期", example = "false")
    private Boolean upcoming = false;

    @Schema(description = "即将到期的天数阈值", example = "3")
    private Integer upcomingDays = 3;

    public Pageable toPageable() {
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }
}