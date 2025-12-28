// ReturnRequest.java - 归还请求DTO
package com.item.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Schema(description = "归还请求")
public class ReturnRequest {

    @NotNull(message = "实际归还时间不能为空")
    @Schema(description = "实际归还时间", example = "2024-12-25", required = true)
    private LocalDate actualReturnDate;

    @Schema(description = "归还备注")
    private String remark;

    @Schema(description = "是否损坏", example = "false")
    private Boolean damaged = false;

    @Schema(description = "损坏描述")
    private String damageDescription;
}