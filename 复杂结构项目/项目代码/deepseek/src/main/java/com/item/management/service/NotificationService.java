// NotificationService.java - 通知服务接口
package com.item.management.service;

public interface NotificationService {

    void sendUpcomingReturnNotification(Long borrowId);

    void sendOverdueNotification(Long borrowId);

    void sendSevereOverdueNotification(Long borrowId);

    void sendDailyAlertSummary(Long userId);

    void sendAlertToAdmin(String title, String content);
}