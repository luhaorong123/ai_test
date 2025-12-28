// ItemQueryRequest.java - 物品查询请求DTO
package com.item.management.dto.request;

import com.item.management.enums.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Schema(description = "物品查询请求")
public class ItemQueryRequest {

    @Schema(description = "页码，从0开始", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "排序字段", example = "createdAt", allowableValues = {"id", "name", "category", "status", "createdAt"}, defaultValue = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "排序方向", example = "DESC", allowableValues = {"ASC", "DESC"}, defaultValue = "DESC")
    private Sort.Direction direction = Sort.Direction.DESC;

    @Schema(description = "物品名称搜索关键词")
    private String name;

    @Schema(description = "物品类别", example = "图书")
    private String category;

    @Schema(description = "物品状态", example = "AVAILABLE")
    private ItemStatus status;

    @Schema(description = "是否只查询可借物品", example = "false")
    private Boolean availableOnly = false;

    @Schema(description = "创建时间开始范围")
    private String createdStart;

    @Schema(description = "创建时间结束范围")
    private String createdEnd;

    public Pageable toPageable() {
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }
}