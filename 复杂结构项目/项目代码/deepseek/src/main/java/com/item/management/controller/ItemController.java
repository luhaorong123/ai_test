package com.item.management.controller;

import com.item.management.converter.ItemConverter;
import com.item.management.dto.request.ItemQueryRequest;
import com.item.management.dto.response.ApiResponse;
import com.item.management.dto.response.ItemSummaryDTO;
import com.item.management.entity.Item;
import com.item.management.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "物品查询", description = "物品查询API（普通用户和管理员均可访问）")
@Validated
public class ItemController {

    private final ItemService itemService;
    private final ItemConverter itemConverter;

    @GetMapping("/{itemId}")
    @Operation(summary = "获取物品详情", description = "根据ID获取物品详细信息")
    public ResponseEntity<ApiResponse<Item>> getItem(
            @PathVariable @Parameter(description = "物品ID", required = true) Long itemId) {

        Item item = itemService.getItemById(itemId);
        return ResponseEntity.ok(ApiResponse.success(item, "获取物品详情成功"));
    }

    @GetMapping
    @Operation(summary = "分页查询物品列表", description = "分页查询物品，支持搜索和筛选")
    public ResponseEntity<ApiResponse<Page<ItemSummaryDTO>>> getItems(
            @Valid ItemQueryRequest queryRequest) {

        // 普通用户默认只查询可借物品
        if (queryRequest.getAvailableOnly() == null) {
            queryRequest.setAvailableOnly(true);
        }

        Page<Item> itemPage;

        if (queryRequest.getAvailableOnly()) {
            itemPage = itemService.getAvailableItems(queryRequest.toPageable());
        } else {
            itemPage = itemService.searchItems(queryRequest);
        }

        Page<ItemSummaryDTO> resultPage = itemPage.map(itemConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage, "查询成功"));
    }

    @GetMapping("/available")
    @Operation(summary = "查询可借物品", description = "分页查询所有可借物品")
    public ResponseEntity<ApiResponse<Page<ItemSummaryDTO>>> getAvailableItems(
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<Item> itemPage = itemService.getAvailableItems(
                org.springframework.data.domain.PageRequest.of(page, size));

        Page<ItemSummaryDTO> resultPage = itemPage.map(itemConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage, "查询成功"));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "按类别查询物品", description = "根据类别分页查询可借物品")
    public ResponseEntity<ApiResponse<Page<ItemSummaryDTO>>> getItemsByCategory(
            @PathVariable @Parameter(description = "物品类别", required = true) String category,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<Item> itemPage = itemService.getItemsByCategoryAndStatus(
                category,
                com.item.management.enums.ItemStatus.AVAILABLE,
                org.springframework.data.domain.PageRequest.of(page, size));

        Page<ItemSummaryDTO> resultPage = itemPage.map(itemConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage, "查询成功"));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索物品", description = "根据关键词搜索可借物品")
    public ResponseEntity<ApiResponse<Page<ItemSummaryDTO>>> searchItems(
            @RequestParam @Parameter(description = "搜索关键词", required = true) String keyword,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        // 搜索物品
        Page<Item> itemPage = itemService.searchByKeyword(keyword,
                org.springframework.data.domain.PageRequest.of(page, size));

        // 过滤出可借物品
        List<Item> availableItems = itemPage.getContent().stream()
                .filter(Item::isAvailable)
                .collect(Collectors.toList());

        // 创建新的Page对象
        Page<Item> filteredPage = new org.springframework.data.domain.PageImpl<>(
                availableItems,
                org.springframework.data.domain.PageRequest.of(page, size),
                availableItems.size()
        );

        Page<ItemSummaryDTO> resultPage = filteredPage.map(itemConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage, "查询成功"));
    }

    @GetMapping("/categories")
    @Operation(summary = "获取所有物品类别", description = "获取系统中所有的物品类别")
    public ResponseEntity<ApiResponse<java.util.List<String>>> getAllCategories() {
        // 这里需要从数据库查询所有不重复的类别
        // 为了简化，我们先返回一个空列表，实际实现时可以从itemRepository查询
        return ResponseEntity.ok(ApiResponse.success(
                java.util.Collections.emptyList(),
                "获取类别列表成功"
        ));
    }
}