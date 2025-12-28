// ItemUpdateRequest.java - 更新物品请求DTO
package com.item.management.dto.request;

import com.item.management.enums.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Schema(description = "更新物品请求")
public class ItemUpdateRequest {

    @Size(min = 1, max = 100, message = "物品名称长度必须在1-100个字符之间")
    @Schema(description = "物品名称", example = "Java编程思想（第五版）")
    private String name;

    @Schema(description = "物品描述", example = "最新的Java编程书籍，包含Java 17新特性")
    private String description;

    @Schema(description = "物品类别", example = "技术图书")
    private String category;

    @Schema(description = "物品状态", example = "AVAILABLE")
    private ItemStatus status;
}