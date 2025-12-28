// ItemServiceImpl.java - 物品服务实现类
package com.item.management.service.impl;

import com.item.management.dto.request.ItemQueryRequest;
import com.item.management.entity.Item;
import com.item.management.enums.ItemStatus;
import com.item.management.exception.BusinessException;
import com.item.management.repository.ItemRepository;
import com.item.management.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Item createItem(Item item) {
        // 验证物品名称是否已存在
        if (StringUtils.hasText(item.getName()) && itemRepository.findByName(item.getName()).isPresent()) {
            throw new BusinessException("物品名称已存在: " + item.getName());
        }

        // 设置默认状态
        if (item.getStatus() == null) {
            item.setStatus(ItemStatus.AVAILABLE);
        }

        Item savedItem = itemRepository.save(item);
        log.info("创建物品成功: {} (ID: {})", savedItem.getName(), savedItem.getId());
        return savedItem;
    }

    @Override
    @Transactional
    public Item updateItem(Long itemId, Item itemUpdate) {
        Item item = getItemById(itemId);

        // 如果名称被修改，检查新名称是否已存在
        if (StringUtils.hasText(itemUpdate.getName()) &&
                !itemUpdate.getName().equals(item.getName()) &&
                itemRepository.findByName(itemUpdate.getName()).isPresent()) {
            throw new BusinessException("物品名称已存在: " + itemUpdate.getName());
        }

        // 更新可修改的字段
        if (StringUtils.hasText(itemUpdate.getName())) {
            item.setName(itemUpdate.getName());
        }

        if (StringUtils.hasText(itemUpdate.getDescription())) {
            item.setDescription(itemUpdate.getDescription());
        }

        if (StringUtils.hasText(itemUpdate.getCategory())) {
            item.setCategory(itemUpdate.getCategory());
        }

        // 注意：状态需要通过专门的接口更新，这里不更新状态

        Item updatedItem = itemRepository.save(item);
        log.info("更新物品信息成功: {} (ID: {})", updatedItem.getName(), updatedItem.getId());
        return updatedItem;
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("物品不存在，ID: " + itemId));
    }

    @Override
    public Item getItemByName(String name) {
        return itemRepository.findByName(name)
                .orElseThrow(() -> new BusinessException("物品不存在，名称: " + name));
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        Item item = getItemById(itemId);

        // 检查物品是否正在被借阅
        if (item.isBorrowed()) {
            throw new BusinessException("物品正在被借阅，无法删除");
        }

        // 软删除物品
        item.softDelete();
        itemRepository.save(item);
        log.info("删除物品成功: {} (ID: {})", item.getName(), item.getId());
    }

    @Override
    public Page<Item> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    @Override
    public Page<Item> getAvailableItems(Pageable pageable) {
        return itemRepository.findAvailableItems(pageable);
    }

    @Override
    public Page<Item> searchItems(ItemQueryRequest queryRequest) {
        return itemRepository.searchItems(
                queryRequest.getName(),
                queryRequest.getCategory(),
                queryRequest.getStatus(),
                queryRequest.toPageable()
        );
    }

    @Override
    public Page<Item> searchByKeyword(String keyword, Pageable pageable) {
        // 尝试使用全文搜索，如果失败则使用模糊查询
        try {
            return itemRepository.fullTextSearch(
                    keyword,
                    null, // 不过滤状态
                    pageable
            );
        } catch (Exception e) {
            log.warn("全文搜索失败，使用模糊查询: {}", e.getMessage());
            return itemRepository.searchItems(
                    keyword,
                    keyword, // 同时搜索类别
                    null,
                    pageable
            );
        }
    }

    @Override
    public Page<Item> getItemsByCategory(String category, Pageable pageable) {
        return itemRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<Item> getItemsByStatus(ItemStatus status, Pageable pageable) {
        return itemRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Item> getItemsByCategoryAndStatus(String category, ItemStatus status, Pageable pageable) {
        return itemRepository.findByCategoryAndStatus(category, status, pageable);
    }

    @Override
    public long countAllItems() {
        return itemRepository.count();
    }

    @Override
    public long countByStatus(ItemStatus status) {
        return itemRepository.countByStatus(status);
    }

    @Override
    public long countByCategory(String category) {
        return itemRepository.findByCategory(category, Pageable.unpaged()).getTotalElements();
    }

    @Override
    @Transactional
    public Item updateItemStatus(Long itemId, ItemStatus status) {
        Item item = getItemById(itemId);

        // 状态转换验证
        validateStatusTransition(item.getStatus(), status);

        item.setStatus(status);
        Item updatedItem = itemRepository.save(item);
        log.info("更新物品状态成功: {} (ID: {}) -> {}",
                updatedItem.getName(), updatedItem.getId(), status);
        return updatedItem;
    }

    @Override
    @Transactional
    public Item borrowItem(Long itemId) {
        Item item = getItemById(itemId);

        if (!item.isAvailable()) {
            throw new BusinessException("物品当前不可借出，状态为: " + item.getStatus().getDescription());
        }

        item.borrow();
        Item updatedItem = itemRepository.save(item);
        log.info("物品借出成功: {} (ID: {})", updatedItem.getName(), updatedItem.getId());
        return updatedItem;
    }

    @Override
    @Transactional
    public Item returnItem(Long itemId) {
        Item item = getItemById(itemId);

        if (!item.isBorrowed()) {
            throw new BusinessException("物品当前不是借出状态，无法归还");
        }

        item.returnItem();
        Item updatedItem = itemRepository.save(item);
        log.info("物品归还成功: {} (ID: {})", updatedItem.getName(), updatedItem.getId());
        return updatedItem;
    }

    @Override
    @Transactional
    public Item putUnderMaintenance(Long itemId) {
        Item item = getItemById(itemId);

        if (item.isBorrowed()) {
            throw new BusinessException("物品正在被借阅，无法设置为维护中");
        }

        item.putUnderMaintenance();
        Item updatedItem = itemRepository.save(item);
        log.info("物品设为维护中成功: {} (ID: {})", updatedItem.getName(), updatedItem.getId());
        return updatedItem;
    }

    @Override
    @Transactional
    public Item restoreFromMaintenance(Long itemId) {
        Item item = getItemById(itemId);

        if (!item.isUnderMaintenance()) {
            throw new BusinessException("物品当前不是维护中状态");
        }

        item.returnItem(); // 恢复为可借状态
        Item updatedItem = itemRepository.save(item);
        log.info("物品从维护中恢复成功: {} (ID: {})", updatedItem.getName(), updatedItem.getId());
        return updatedItem;
    }

    @Override
    @Transactional
    public List<Item> createItems(List<Item> items) {
        // 验证所有物品名称是否唯一
        List<String> itemNames = items.stream()
                .map(Item::getName)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());

        for (String name : itemNames) {
            if (itemRepository.findByName(name).isPresent()) {
                throw new BusinessException("物品名称已存在: " + name);
            }
        }

        // 设置默认状态和时间
        items.forEach(item -> {
            if (item.getStatus() == null) {
                item.setStatus(ItemStatus.AVAILABLE);
            }
            if (item.getCreatedAt() == null) {
                item.setCreatedAt(LocalDateTime.now());
            }
        });

        List<Item> savedItems = itemRepository.saveAll(items);
        log.info("批量创建物品成功，共 {} 件", savedItems.size());
        return savedItems;
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<Long> itemIds, ItemStatus status) {
        if (itemIds == null || itemIds.isEmpty()) {
            return;
        }

        for (Long itemId : itemIds) {
            try {
                updateItemStatus(itemId, status);
            } catch (Exception e) {
                log.warn("批量更新物品状态失败: ID={}, 错误: {}", itemId, e.getMessage());
                // 继续处理其他物品
            }
        }

        log.info("批量更新物品状态完成，共处理 {} 件物品", itemIds.size());
    }

    @Override
    @Transactional
    public void batchDeleteItems(List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return;
        }

        int successCount = 0;
        int failCount = 0;

        for (Long itemId : itemIds) {
            try {
                deleteItem(itemId);
                successCount++;
            } catch (Exception e) {
                log.warn("批量删除物品失败: ID={}, 错误: {}", itemId, e.getMessage());
                failCount++;
            }
        }

        log.info("批量删除物品完成，成功 {} 件，失败 {} 件", successCount, failCount);
    }

    @Override
    public boolean existsByName(String name) {
        return itemRepository.findByName(name).isPresent();
    }

    @Override
    public boolean isItemAvailable(Long itemId) {
        try {
            Item item = getItemById(itemId);
            return item.isAvailable();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证状态转换是否合法
     */
    private void validateStatusTransition(ItemStatus currentStatus, ItemStatus newStatus) {
        // 定义允许的状态转换
        switch (currentStatus) {
            case AVAILABLE:
                if (newStatus != ItemStatus.BORROWED && newStatus != ItemStatus.MAINTENANCE) {
                    throw new BusinessException("无效的状态转换: " + currentStatus + " -> " + newStatus);
                }
                break;

            case BORROWED:
                if (newStatus != ItemStatus.AVAILABLE && newStatus != ItemStatus.MAINTENANCE) {
                    throw new BusinessException("无效的状态转换: " + currentStatus + " -> " + newStatus);
                }
                break;

            case MAINTENANCE:
                if (newStatus != ItemStatus.AVAILABLE) {
                    throw new BusinessException("无效的状态转换: " + currentStatus + " -> " + newStatus);
                }
                break;
        }
    }

    /**
     * 自动更新物品状态（定时任务调用）
     */
    @Transactional
    public void autoUpdateItemStatus() {
        // 这里可以添加自动状态更新的逻辑
        // 例如：长时间未被借阅的物品自动标记为维护中等
        log.debug("执行物品状态自动更新任务");
    }
}