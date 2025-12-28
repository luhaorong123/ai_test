// UserWithStatsDTO.java - 带统计信息的用户DTO
package com.item.management.dto.response;

import com.item.management.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "带统计信息的用户详情")
public class UserWithStatsDTO {

    @Schema(description = "用户信息")
    private User user;

    @Schema(description = "总借阅次数")
    private long totalBorrows;

    @Schema(description = "当前借阅中")
    private long activeBorrows;

    @Schema(description = "逾期借阅")
    private long overdueBorrows;

    @Schema(description = "已归还")
    private long returnedBorrows;

    @Schema(description = "良好率（按时归还的比例）")
    public Double getGoodRate() {
        if (totalBorrows == 0) {
            return 0.0;
        }
        double goodRate = (double) (totalBorrows - overdueBorrows) / totalBorrows * 100;
        return Math.round(goodRate * 100.0) / 100.0;
    }
}