// AlertStatsDTO.java - 预警统计DTO
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
@Schema(description = "预警统计信息")
public class AlertStatsDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "即将到期数量")
    private long upcomingCount;

    @Schema(description = "已逾期数量")
    private long overdueCount;

    @Schema(description = "今天到期数量")
    private long todayDueCount;

    @Schema(description = "严重逾期数量（超过7天）")
    private long severeOverdueCount;

    @Schema(description = "预警率")
    private String alertRate;

    @Schema(description = "用户逾期统计")
    private Map<String, Long> userOverdueStats;

    @Schema(description = "物品类别逾期统计")
    private Map<String, Long> categoryOverdueStats;

    @Schema(description = "最严重逾期用户")
    private String worstUser;

    @Schema(description = "最常逾期的物品类别")
    private String worstCategory;
}