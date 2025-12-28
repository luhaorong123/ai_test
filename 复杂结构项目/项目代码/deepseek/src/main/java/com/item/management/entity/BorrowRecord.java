// BorrowRecord.java - 借阅记录实体类
package com.item.management.entity;

import com.item.management.enums.BorrowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_records",
        indexes = {
                @Index(name = "idx_user_item", columnList = "user_id, item_id"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_expected_date", columnList = "expected_return_date"),
                @Index(name = "idx_borrow_date", columnList = "borrow_date"),
                @Index(name = "idx_actual_return_date", columnList = "actual_return_date")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Where(clause = "deleted = false")
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "用户不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_borrow_record_user"))
    private User user;

    @NotNull(message = "物品不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_borrow_record_item"))
    private Item item;

    @CreationTimestamp
    @Column(name = "borrow_date", nullable = false, updatable = false)
    private LocalDateTime borrowDate;

    @NotNull(message = "预计归还日期不能为空")
    @Future(message = "预计归还日期必须是未来日期")
    @Column(name = "expected_return_date", nullable = false)
    private LocalDate expectedReturnDate;

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BorrowStatus status = BorrowStatus.ACTIVE;

    @Column(name = "overdue_days")
    private Integer overdueDays = 0;

    @Column(length = 500)
    private String remark;

    @Column(name = "damaged")
    private Boolean damaged = false;

    @Column(name = "damage_description", length = 1000)
    private String damageDescription;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;



    @Builder.Default
    @Column(name = "deleted", nullable = false)


    private Boolean deleted = false;

    // 业务方法
    public boolean isActive() {
        return this.status == BorrowStatus.ACTIVE;
    }

    public boolean isReturned() {
        return this.status == BorrowStatus.RETURNED;
    }

    public boolean isOverdue() {
        return this.status == BorrowStatus.OVERDUE;
    }

    public boolean isExpired() {
        if (isReturned()) {
            return false;
        }
        return LocalDate.now().isAfter(expectedReturnDate);
    }

    public void calculateOverdueDays() {
        if (isReturned()) {
            if (actualReturnDate.isAfter(expectedReturnDate)) {
                this.overdueDays = (int) java.time.temporal.ChronoUnit.DAYS.between(
                        expectedReturnDate, actualReturnDate
                );
            }
        } else if (isExpired()) {
            this.overdueDays = (int) java.time.temporal.ChronoUnit.DAYS.between(
                    expectedReturnDate, LocalDate.now()
            );
            if (this.status != BorrowStatus.OVERDUE) {
                this.status = BorrowStatus.OVERDUE;
            }
        }
    }

    public void returnItem(LocalDate actualReturnDate) {
        if (isReturned()) {
            throw new IllegalStateException("借阅记录已经归还");
        }

        this.actualReturnDate = actualReturnDate;
        this.status = BorrowStatus.RETURNED;
        calculateOverdueDays();

        // 更新物品状态
        if (this.item != null) {
            this.item.returnItem();
        }
    }

    public void softDelete() {
        this.deleted = true;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.expectedReturnDate == null) {
            // 默认借阅7天
            this.expectedReturnDate = LocalDate.now().plusDays(7);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateOverdueDays();
    }
}