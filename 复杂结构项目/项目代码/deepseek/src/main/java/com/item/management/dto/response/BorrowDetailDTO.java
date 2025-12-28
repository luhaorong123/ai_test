// BorrowDetailDTO.java - 借阅记录详情DTO
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
@Schema(description = "借阅记录详情信息")
public class BorrowDetailDTO {

    @Schema(description = "借阅ID", example = "1")
    private Long id;

    @Schema(description = "用户信息")
    private UserSummaryDTO user;

    @Schema(description = "物品信息")
    private ItemSummaryDTO item;

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

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否损坏")
    private Boolean damaged;

    @Schema(description = "损坏描述")
    private String damageDescription;

    @Schema(description = "是否即将到期")
    public Boolean getIsUpcoming() {
        if (status != BorrowStatus.ACTIVE || expectedReturnDate == null) {
            return false;
        }
        long daysUntilReturn = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), expectedReturnDate);
        return daysUntilReturn >= 0 && daysUntilReturn <= 3;
    }

    @Schema(description = "是否已逾期")
    public Boolean getIsOverdue() {
        return status == BorrowStatus.OVERDUE ||
                (status == BorrowStatus.ACTIVE && expectedReturnDate != null &&
                        expectedReturnDate.isBefore(LocalDate.now()));
    }

    @Schema(description = "距离归还天数")
    public Long getDaysUntilReturn() {
        if (actualReturnDate != null || expectedReturnDate == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expectedReturnDate);
    }
}