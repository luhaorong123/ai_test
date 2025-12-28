// BorrowSummaryDTO.java - 借阅记录摘要DTO
package com.item.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.item.management.enums.BorrowStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "借阅记录摘要信息")
public class BorrowSummaryDTO {

    @Schema(description = "借阅ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "张三")
    private String username;

    @Schema(description = "物品ID", example = "1")
    private Long itemId;

    @Schema(description = "物品名称", example = "Java编程思想")
    private String itemName;

    @Schema(description = "物品类别", example = "图书")
    private String itemCategory;

    @Schema(description = "借出时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime borrowDate;

    @Schema(description = "预计归还时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedReturnDate;

    @Schema(description = "实际归还时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualReturnDate;

    @Schema(description = "借阅状态", example = "ACTIVE")
    private BorrowStatus status;

    @Schema(description = "逾期天数", example = "0")
    private Integer overdueDays;

    @Schema(description = "是否即将到期")
    private Boolean isUpcoming;

    @Schema(description = "是否已逾期")
    private Boolean isOverdue;

    @Schema(description = "距离归还天数（负数表示已逾期）")
    public Long getDaysUntilReturn() {
        if (actualReturnDate != null || expectedReturnDate == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expectedReturnDate);
    }
}