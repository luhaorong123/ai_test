package com.library.service;

import com.library.entity.Item;
import com.library.enums.ItemStatus;
import com.library.repository.ItemRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * 创建图书（管理员专用）
     */
    @Transactional
    public Item createItem(Item item) {
        validateCopies(item);
        syncStatus(item);
        return itemRepository.save(item);
    }

    /**
     * 更新图书信息（管理员专用）
     */
    @Transactional
    public Item updateItem(Long id, Item updateItem) {
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图书不存在"));

        existing.setIsbn(updateItem.getIsbn());
        existing.setTitle(updateItem.getTitle());
        existing.setAuthor(updateItem.getAuthor());
        existing.setPublisher(updateItem.getPublisher());
        existing.setPublishYear(updateItem.getPublishYear());
        existing.setCategory(updateItem.getCategory());
        existing.setTotalCopies(updateItem.getTotalCopies());
        existing.setLocation(updateItem.getLocation());

        // 可用数量不能手动修改，由系统根据借阅记录自动维护
        // 这里仅允许管理员调整总数量
        validateCopies(existing);
        syncStatus(existing);

        return itemRepository.save(existing);
    }

    /**
     * 删除图书（管理员专用）
     * 借阅记录通过级联自动删除
     */
    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("图书不存在");
        }
        itemRepository.deleteById(id);
    }

    /**
     * 获取单个图书详情
     */
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图书不存在"));
    }

    /**
     * 分页多条件查询（公共接口）
     * 支持：标题/作者/ISBN模糊搜索、分类、状态筛选
     */
    public Page<Item> searchItems(String keyword,
                                  String category,
                                  ItemStatus status,
                                  int page,
                                  int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Item probe = new Item();
        if (keyword != null && !keyword.isBlank()) {
            // 多字段模糊匹配
            matcher = matcher.withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                    .withMatcher("author", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                    .withMatcher("isbn", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        }
        if (category != null && !category.isBlank()) {
            probe.setCategory(category);
        }
        if (status != null) {
            probe.setStatus(status);
        }

        Example<Item> example = Example.of(probe, matcher);
        return itemRepository.findAll(example, pageable);
    }

    /**
     * 用户专用：仅查询可借图书（availableCopies > 0 且状态为AVAILABLE）
     */
    public Page<Item> searchAvailableItems(String keyword,
                                           String category,
                                           int page,
                                           int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Item probe = new Item();
        probe.setStatus(ItemStatus.AVAILABLE);

        if (keyword != null && !keyword.isBlank()) {
            matcher = matcher.withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                    .withMatcher("author", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                    .withMatcher("isbn", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        }
        if (category != null && !category.isBlank()) {
            probe.setCategory(category);
        }

        Example<Item> example = Example.of(probe, matcher);
        Page<Item> allAvailableStatus = itemRepository.findAll(example, pageable);

        // 在内存中过滤 availableCopies > 0，并重建 Page
        List<Item> filteredContent = allAvailableStatus.getContent()
                .stream()
                .filter(item -> item.getAvailableCopies() > 0)
                .toList();

        return new PageImpl<>(filteredContent, pageable, allAvailableStatus.getTotalElements());
    }

    /**
     * 内部调用：同步图书状态与可用数量
     * 规则：
     *   availableCopies == 0 → MAINTENANCE（即使管理员设为AVAILABLE）
     *   availableCopies > 0 → 恢复为AVAILABLE（若之前是MAINTENANCE）
     */
    @Transactional
    public void syncItemStatus(Long itemId, int deltaAvailableCopies) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));

        item.setAvailableCopies(item.getAvailableCopies() + deltaAvailableCopies);
        validateCopies(item);
        syncStatus(item);

        itemRepository.save(item);
    }

    private void validateCopies(Item item) {
        if (item.getAvailableCopies() < 0) {
            item.setAvailableCopies(0);
        }
        if (item.getAvailableCopies() > item.getTotalCopies()) {
            item.setAvailableCopies(item.getTotalCopies());
        }
    }

    private void syncStatus(Item item) {
        if (item.getAvailableCopies() == 0) {
            item.setStatus(ItemStatus.MAINTENANCE);
        } else if (item.getAvailableCopies() > 0 && item.getStatus() == ItemStatus.MAINTENANCE) {
            item.setStatus(ItemStatus.AVAILABLE);
        }
        // LOST 状态需管理员手动设置，不自动恢复
    }
}