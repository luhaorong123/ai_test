// ItemSummaryDTO.java - 物品摘要DTO
package com.item.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.item.management.enums.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "物品摘要信息")
public class ItemSummaryDTO {

    @Schema(description = "物品ID", example = "1")
    private Long id;

    @Schema(description = "物品名称", example = "Java编程思想")
    private String name;

    @Schema(description = "物品描述摘要", example = "经典的Java编程书籍...")
    private String description;

    @Schema(description = "物品类别", example = "图书")
    private String category;

    @Schema(description = "物品状态", example = "AVAILABLE")
    private ItemStatus status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "借阅次数")
    private long borrowCount;

    @Schema(description = "是否可借")
    public boolean isAvailable() {
        return status == ItemStatus.AVAILABLE;
    }
}