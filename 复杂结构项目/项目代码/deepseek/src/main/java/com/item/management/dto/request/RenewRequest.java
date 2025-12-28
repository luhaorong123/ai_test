// RenewRequest.java - 续借请求DTO
package com.item.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Schema(description = "续借请求")
public class RenewRequest {

    @NotNull(message = "新的预计归还时间不能为空")
    @Future(message = "新的预计归还时间必须是未来日期")
    @Schema(description = "新的预计归还时间", example = "2025-01-07", required = true)
    private LocalDate newExpectedReturnDate;

    @Schema(description = "续借原因")
    private String reason;
}