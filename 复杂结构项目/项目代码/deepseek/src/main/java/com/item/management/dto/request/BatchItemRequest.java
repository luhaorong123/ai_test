// BatchItemRequest.java - 批量操作请求DTO
package com.item.management.dto.request;

import com.item.management.enums.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "批量操作请求")
public class BatchItemRequest {

    @NotEmpty(message = "物品ID列表不能为空")
    @Schema(description = "物品ID列表", required = true)
    private List<Long> itemIds;

    @Schema(description = "批量操作类型", example = "UPDATE_STATUS", allowableValues = {"UPDATE_STATUS", "DELETE"})
    private String operationType;

    @Schema(description = "新状态（更新状态时使用）")
    private ItemStatus newStatus;
}