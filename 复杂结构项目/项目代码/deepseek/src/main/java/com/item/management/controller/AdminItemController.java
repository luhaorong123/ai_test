// AdminItemController.java - 管理员物品管理控制器
package com.item.management.controller;

import com.item.management.converter.ItemConverter;
import com.item.management.dto.request.*;
import com.item.management.dto.response.ApiResponse;
import com.item.management.dto.response.ItemStatsDTO;
import com.item.management.dto.response.ItemSummaryDTO;
import com.item.management.entity.Item;
import com.item.management.enums.ItemStatus;
import com.item.management.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/items")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "管理员-物品管理", description = "管理员操作物品的API")
@Validated
public class AdminItemController {

    private final ItemService itemService;
    private final ItemConverter itemConverter;

    @PostMapping
    @Operation(summary = "创建物品", description = "创建新物品")
    public ResponseEntity<ApiResponse<Item>> createItem(
            @Valid @RequestBody ItemCreateRequest request) {

        Item item = Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(request.getStatus())
                .build();

        Item createdItem = itemService.createItem(item);
        return ResponseEntity.ok(ApiResponse.success(createdItem, "物品创建成功"));
    }

    @PostMapping("/batch")
    @Operation(summary = "批量创建物品", description = "批量创建多个物品")
    public ResponseEntity<ApiResponse<List<Item>>> batchCreateItems(
            @Valid @RequestBody List<ItemCreateRequest> requests) {

        List<Item> items = requests.stream()
                .map(req -> {
                    Item item = Item.builder()
                            .name(req.getName())
                            .description(req.getDescription())
                            .category(req.getCategory())
                            .build();
                    if (req.getStatus() != null) {
                        item.setStatus(req.getStatus());
                    }
                    return item;
                })
                .collect(Collectors.toList());

        List<Item> createdItems = itemService.createItems(items);
        return ResponseEntity.ok(ApiResponse.success(createdItems, "批量创建物品成功"));
    }



    @GetMapping("/{itemId}")
    @Operation(summary = "获取物品详情", description = "根据ID获取物品详细信息")
    public ResponseEntity<ApiResponse<Item>> getItem(
            @PathVariable @Parameter(description = "物品ID", required = true) Long itemId) {

        Item item = itemService.getItemById(itemId);
        return ResponseEntity.ok(ApiResponse.success(item, "获取物品详情成功"));
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "更新物品信息", description = "更新物品基本信息（不包含状态）")
    public ResponseEntity<ApiResponse<Item>> updateItem(
            @PathVariable @Parameter(description = "物品ID", required = true) Long itemId,
            @Valid @RequestBody ItemUpdateRequest request) {

        Item itemUpdate = Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .build();

        Item updatedItem = itemService.updateItem(itemId, itemUpdate);
        return ResponseEntity.ok(ApiResponse.success(updatedItem, "物品信息更新成功"));
    }

    @PatchMapping("/{itemId}/status")
    @Operation(summary = "更新物品状态", description = "更新物品状态")
    public ResponseEntity<ApiResponse<Item>> updateItemStatus(
            @PathVariable @Parameter(description = "物品ID", required = true) Long itemId,
            @RequestParam @Parameter(description = "物品状态", required = true) ItemStatus status) {

        Item updatedItem = itemService.updateItemStatus(itemId, status);
        return ResponseEntity.ok(ApiResponse.success(updatedItem, "物品状态更新成功"));
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "删除物品", description = "删除物品（软删除）")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @PathVariable @Parameter(description = "物品ID", required = true) Long itemId) {

        itemService.deleteItem(itemId);
        return ResponseEntity.ok(ApiResponse.success(null, "物品删除成功"));
    }

    @PostMapping("/batch-operation")
    @Operation(summary = "批量操作", description = "批量更新状态或删除物品")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchOperation(
            @Valid @RequestBody BatchItemRequest request) {

        Map<String, Object> result = new HashMap<>();

        if ("UPDATE_STATUS".equals(request.getOperationType())) {
            if (request.getNewStatus() == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("更新状态操作需要指定newStatus参数"));
            }

            itemService.batchUpdateStatus(request.getItemIds(), request.getNewStatus());
            result.put("message", "批量更新状态成功");
            result.put("count", request.getItemIds().size());

        } else if ("DELETE".equals(request.getOperationType())) {
            itemService.batchDeleteItems(request.getItemIds());
            result.put("message", "批量删除成功");
            result.put("count", request.getItemIds().size());

        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("不支持的操作类型: " + request.getOperationType()));
        }

        return ResponseEntity.ok(ApiResponse.success(result, "批量操作成功"));
    }

    @GetMapping
    @Operation(summary = "分页查询物品列表", description = "分页查询所有物品，支持搜索和筛选")
    public ResponseEntity<ApiResponse<Page<ItemSummaryDTO>>> getItems(
            @Valid ItemQueryRequest queryRequest) {

        Page<Item> itemPage;

        if (queryRequest.getAvailableOnly() != null && queryRequest.getAvailableOnly()) {
            itemPage = itemService.getAvailableItems(queryRequest.toPageable());
        } else {
            itemPage = itemService.searchItems(queryRequest);
        }

        Page<ItemSummaryDTO> resultPage = itemPage.map(itemConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "按类别查询物品", description = "根据类别分页查询物品")
    public ResponseEntity<ApiResponse<Page<ItemSummaryDTO>>> getItemsByCategory(
            @PathVariable @Parameter(description = "物品类别", required = true) String category,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<Item> itemPage = itemService.getItemsByCategory(category,
                org.springframework.data.domain.PageRequest.of(page, size));

        Page<ItemSummaryDTO> resultPage = itemPage.map(itemConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询物品", description = "根据状态分页查询物品")
    public ResponseEntity<ApiResponse<Page<ItemSummaryDTO>>> getItemsByStatus(
            @PathVariable @Parameter(description = "物品状态", required = true) ItemStatus status,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<Item> itemPage = itemService.getItemsByStatus(status,
                org.springframework.data.domain.PageRequest.of(page, size));

        Page<ItemSummaryDTO> resultPage = itemPage.map(itemConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索物品", description = "根据关键词搜索物品")
    public ResponseEntity<ApiResponse<Page<ItemSummaryDTO>>> searchItems(
            @RequestParam @Parameter(description = "搜索关键词", required = true) String keyword,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<Item> itemPage = itemService.searchByKeyword(keyword,
                org.springframework.data.domain.PageRequest.of(page, size));

        Page<ItemSummaryDTO> resultPage = itemPage.map(itemConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }

    @GetMapping("/stats")
    @Operation(summary = "物品统计信息", description = "获取物品统计信息")
    public ResponseEntity<ApiResponse<ItemStatsDTO>> getItemStats() {
        ItemStatsDTO stats = new ItemStatsDTO();

        long totalItems = itemService.countAllItems();
        long availableItems = itemService.countByStatus(ItemStatus.AVAILABLE);
        long borrowedItems = itemService.countByStatus(ItemStatus.BORROWED);
        long maintenanceItems = itemService.countByStatus(ItemStatus.MAINTENANCE);

        stats.setTotalItems(totalItems);
        stats.setAvailableItems(availableItems);
        stats.setBorrowedItems(borrowedItems);
        stats.setMaintenanceItems(maintenanceItems);

        // 这里可以添加更复杂的统计逻辑，如按类别统计等

        return ResponseEntity.ok(ApiResponse.success(stats, "获取物品统计信息成功"));
    }

    @GetMapping("/exists/name/{name}")
    @Operation(summary = "检查物品名称是否存在", description = "检查物品名称是否已存在")
    public ResponseEntity<ApiResponse<Boolean>> checkItemNameExists(
            @PathVariable @Parameter(description = "物品名称", required = true) String name) {

        boolean exists = itemService.existsByName(name);
        return ResponseEntity.ok(ApiResponse.success(exists, "检查物品名称是否存在成功"));
    }

    @GetMapping("/available/{itemId}")
    @Operation(summary = "检查物品是否可借", description = "检查指定物品是否可借")
    public ResponseEntity<ApiResponse<Boolean>> checkItemAvailable(
            @PathVariable @Parameter(description = "物品ID", required = true) Long itemId) {

        boolean isAvailable = itemService.isItemAvailable(itemId);
        return ResponseEntity.ok(ApiResponse.success(isAvailable, "检查物品是否可借成功"));
    }


}