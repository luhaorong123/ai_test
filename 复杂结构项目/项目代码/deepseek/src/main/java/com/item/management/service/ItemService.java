// ItemService.java - 物品服务接口
package com.item.management.service;

import com.item.management.dto.request.ItemQueryRequest;
import com.item.management.entity.Item;
import com.item.management.enums.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {

    // 物品CRUD操作
    Item createItem(Item item);

    Item updateItem(Long itemId, Item item);

    Item getItemById(Long itemId);

    Item getItemByName(String name);

    void deleteItem(Long itemId);

    // 物品查询
    Page<Item> getAllItems(Pageable pageable);

    Page<Item> getAvailableItems(Pageable pageable);

    Page<Item> searchItems(ItemQueryRequest queryRequest);

    Page<Item> searchByKeyword(String keyword, Pageable pageable);

    Page<Item> getItemsByCategory(String category, Pageable pageable);

    Page<Item> getItemsByStatus(ItemStatus status, Pageable pageable);

    Page<Item> getItemsByCategoryAndStatus(String category, ItemStatus status, Pageable pageable);

    // 物品统计
    long countAllItems();

    long countByStatus(ItemStatus status);

    long countByCategory(String category);

    // 状态管理
    Item updateItemStatus(Long itemId, ItemStatus status);

    Item borrowItem(Long itemId);

    Item returnItem(Long itemId);

    Item putUnderMaintenance(Long itemId);

    Item restoreFromMaintenance(Long itemId);

    // 批量操作
    List<Item> createItems(List<Item> items);

    void batchUpdateStatus(List<Long> itemIds, ItemStatus status);

    void batchDeleteItems(List<Long> itemIds);

    // 验证
    boolean existsByName(String name);

    boolean isItemAvailable(Long itemId);
}