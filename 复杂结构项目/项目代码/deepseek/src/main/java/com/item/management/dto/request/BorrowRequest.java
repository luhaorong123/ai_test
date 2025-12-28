// BorrowRequest.java - 借阅请求DTO
package com.item.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Schema(description = "借阅请求")
public class BorrowRequest {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1", required = true)
    private Long userId;

    @NotNull(message = "物品ID不能为空")
    @Schema(description = "物品ID", example = "1", required = true)
    private Long itemId;

    @Future(message = "预计归还时间必须是未来日期")
    @Schema(description = "预计归还时间，不指定则默认为7天后", example = "2024-12-31")
    private LocalDate expectedReturnDate;

    @Schema(description = "备注")
    private String remark;
}