// BorrowStatsDTO.java - 借阅统计DTO
package com.item.management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "借阅统计信息")
public class BorrowStatsDTO {

    @Schema(description = "总借阅次数")
    private long totalBorrows;

    @Schema(description = "当前借阅中")
    private long activeBorrows;

    @Schema(description = "逾期借阅")
    private long overdueBorrows;

    @Schema(description = "已归还")
    private long returnedBorrows;

    @Schema(description = "归还率")
    private String returnRate;

    @Schema(description = "逾期率")
    private String overdueRate;

    @Schema(description = "平均借阅时长")
    private String averageBorrowDays;

    @Schema(description = "最热门物品")
    private String mostPopularItem;

    @Schema(description = "最活跃用户")
    private String mostActiveUser;
}