// AlertSummaryDTO.java - 预警摘要DTO
package com.item.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Schema(description = "预警摘要信息")
public class AlertSummaryDTO {

    @Schema(description = "预警类型", example = "UPCOMING, OVERDUE, TODAY_DUE, SEVERE_OVERDUE")
    private String type;

    @Schema(description = "预警标题", example = "物品即将到期")
    private String title;

    @Schema(description = "预警消息", example = "物品《Java编程思想》还有3天到期")
    private String message;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "张三")
    private String username;

    @Schema(description = "物品ID", example = "1")
    private Long itemId;

    @Schema(description = "物品名称", example = "Java编程思想")
    private String itemName;

    @Schema(description = "借阅记录ID", example = "1")
    private Long borrowId;

    @Schema(description = "预计归还时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedReturnDate;

    @Schema(description = "逾期天数", example = "3")
    private Integer overdueDays;

    @Schema(description = "剩余天数", example = "2")
    private Integer remainingDays;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "是否已读", example = "false")
    private Boolean read;
}