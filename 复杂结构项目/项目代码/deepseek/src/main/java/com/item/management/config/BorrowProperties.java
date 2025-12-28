// BorrowProperties.java - 借阅配置属性类
package com.item.management.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.borrow")
@Data
public class BorrowProperties {

    private int defaultBorrowDays = 7;           // 默认借阅天数
    private int maxBorrowDays = 30;              // 最大借阅天数
    private int maxRenewDays = 7;                // 最大续借天数
    private int maxBorrowItems = 5;              // 最大同时借阅物品数
    private int alertDays = 3;                   // 到期前预警天数
    private double dailyFineRate = 1.0;          // 逾期每日罚款率
    private int maxOverdueDays = 90;             // 最大允许逾期天数

    // 是否启用自动逾期计算
    private boolean autoOverdueCalculation = true;

    // 是否允许续借逾期物品
    private boolean allowRenewOverdue = false;

    // 是否允许用户有逾期记录时借阅新物品
    private boolean allowBorrowWithOverdue = false;
}