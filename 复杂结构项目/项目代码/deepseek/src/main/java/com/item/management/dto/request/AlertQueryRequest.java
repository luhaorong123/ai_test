// AlertQueryRequest.java - 预警查询请求DTO
package com.item.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Schema(description = "预警查询请求")
public class AlertQueryRequest {

    @Schema(description = "页码，从0开始", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "排序字段", example = "expectedReturnDate", allowableValues = {"expectedReturnDate", "borrowDate", "overdueDays"}, defaultValue = "expectedReturnDate")
    private String sortBy = "expectedReturnDate";

    @Schema(description = "排序方向", example = "ASC", allowableValues = {"ASC", "DESC"}, defaultValue = "ASC")
    private Sort.Direction direction = Sort.Direction.ASC;

    @Schema(description = "预警类型", example = "OVERDUE", allowableValues = {"UPCOMING", "OVERDUE", "ALL"})
    private String alertType;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "物品ID")
    private Long itemId;

    @Schema(description = "天数阈值（用于即将到期查询）", example = "3")
    private Integer daysThreshold = 3;

    @Schema(description = "是否只显示未读预警", example = "false")
    private Boolean unreadOnly = false;

    @Schema(description = "开始时间")
    private String startDate;

    @Schema(description = "结束时间")
    private String endDate;

    public Pageable toPageable() {
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }
}