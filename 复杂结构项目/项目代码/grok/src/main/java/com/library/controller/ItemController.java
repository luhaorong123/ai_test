package com.library.controller;

import com.library.common.ApiResponse;
import com.library.dto.item.ItemCreateRequest;
import com.library.dto.item.ItemResponse;
import com.library.dto.item.ItemUpdateRequest;
import com.library.entity.Item;
import com.library.enums.ItemStatus;
import com.library.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // ========== 管理员专用 ==========

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<ItemResponse> createItem(@Valid @RequestBody ItemCreateRequest request) {
        Item item = new Item();
        item.setIsbn(request.getIsbn());
        item.setTitle(request.getTitle());
        item.setAuthor(request.getAuthor());
        item.setPublisher(request.getPublisher());
        item.setPublishYear(request.getPublishYear());
        item.setCategory(request.getCategory());
        item.setTotalCopies(request.getTotalCopies());
        item.setAvailableCopies(request.getTotalCopies());
        item.setLocation(request.getLocation());
        item.setStatus(request.getStatus());

        Item saved = itemService.createItem(item);
        return ApiResponse.success(ItemResponse.from(saved));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<ItemResponse> updateItem(@PathVariable Long id,
                                                @Valid @RequestBody ItemUpdateRequest request) {
        Item item = new Item();
        item.setIsbn(request.getIsbn());
        item.setTitle(request.getTitle());
        item.setAuthor(request.getAuthor());
        item.setPublisher(request.getPublisher());
        item.setPublishYear(request.getPublishYear());
        item.setCategory(request.getCategory());
        item.setTotalCopies(request.getTotalCopies());
        item.setLocation(request.getLocation());
        item.setStatus(request.getStatus());

        Item updated = itemService.updateItem(id, item);
        return ApiResponse.success(ItemResponse.from(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ApiResponse.success("图书删除成功");
    }

    // ========== 公共查询接口 ==========

    @GetMapping("/{id}")
    public ApiResponse<ItemResponse> getItem(@PathVariable Long id) {
        Item item = itemService.getItemById(id);
        return ApiResponse.success(ItemResponse.from(item));
    }

    /**
     * 管理员完整查询 + 普通用户查询（自动过滤仅可借）
     */
    @GetMapping
    public ApiResponse<Page<ItemResponse>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean availableOnly) {  // 用户端传 true

        Page<Item> itemPage;
        if (availableOnly) {
            // 普通用户只能看到可借图书
            itemPage = itemService.searchAvailableItems(keyword, category, page, size);
        } else {
            // 管理员可看到全部
            itemPage = itemService.searchItems(keyword, category, status, page, size);
        }

        Page<ItemResponse> responsePage = itemPage.map(ItemResponse::from);
        return ApiResponse.success(responsePage);
    }
}