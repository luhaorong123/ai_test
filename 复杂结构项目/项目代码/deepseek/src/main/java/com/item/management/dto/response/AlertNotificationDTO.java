// AlertNotificationDTO.java - 预警通知DTO
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
@Schema(description = "预警通知信息")
public class AlertNotificationDTO {

    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "通知类型", example = "EMAIL, SMS, PUSH, SYSTEM")
    private String notificationType;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "接收用户ID")
    private Long recipientId;

    @Schema(description = "接收用户名")
    private String recipientName;

    @Schema(description = "关联的借阅记录ID")
    private Long borrowId;

    @Schema(description = "发送状态", example = "PENDING, SENT, FAILED")
    private String status;

    @Schema(description = "发送时间")
    private String sentAt;

    @Schema(description = "是否已读")
    private Boolean read;
}