// ItemStatsDTO.java - 物品统计DTO
package com.item.management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "物品统计信息")
public class ItemStatsDTO {

    @Schema(description = "总物品数量")
    private long totalItems;

    @Schema(description = "可借物品数量")
    private long availableItems;

    @Schema(description = "已借出物品数量")
    private long borrowedItems;

    @Schema(description = "维护中物品数量")
    private long maintenanceItems;

    @Schema(description = "按类别统计")
    private Map<String, Long> categoryStats;

    @Schema(description = "最近新增物品数量（最近30天）")
    private long recentItems;

    @Schema(description = "最热门物品类别")
    private String topCategory;

    @Schema(description = "物品借用率")
    public String getUsageRate() {
        if (totalItems == 0) {
            return "0.00%";
        }
        double rate = (double) borrowedItems / totalItems * 100;
        return String.format("%.2f%%", rate);
    }

    @Schema(description = "物品可用率")
    public String getAvailabilityRate() {
        if (totalItems == 0) {
            return "0.00%";
        }
        double rate = (double) availableItems / totalItems * 100;
        return String.format("%.2f%%", rate);
    }
}