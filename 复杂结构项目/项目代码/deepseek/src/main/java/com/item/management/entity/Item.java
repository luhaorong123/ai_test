// Item.java - 物品实体类
package com.item.management.entity;

import com.item.management.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items",
        indexes = {
                @Index(name = "idx_category", columnList = "category"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_name", columnList = "name"),
                @Index(name = "idx_created_at", columnList = "created_at")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Where(clause = "deleted = false")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "物品名称不能为空")
    @Size(min = 1, max = 100, message = "物品名称长度必须在1-100个字符之间")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 50, message = "类别长度不能超过50个字符")
    @Column(length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemStatus status = ItemStatus.AVAILABLE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    // 一对多关系：物品与借阅记录
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    // 业务方法
    public boolean isAvailable() {
        return this.status == ItemStatus.AVAILABLE;
    }

    public boolean isBorrowed() {
        return this.status == ItemStatus.BORROWED;
    }

    public boolean isUnderMaintenance() {
        return this.status == ItemStatus.MAINTENANCE;
    }

    public void borrow() {
        if (!isAvailable()) {
            throw new IllegalStateException("物品当前不可借出，状态为: " + this.status.getDescription());
        }
        this.status = ItemStatus.BORROWED;
    }

    public void returnItem() {
        this.status = ItemStatus.AVAILABLE;
    }

    public void putUnderMaintenance() {
        this.status = ItemStatus.MAINTENANCE;
    }

    public void softDelete() {
        this.deleted = true;
        this.status = ItemStatus.MAINTENANCE;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}