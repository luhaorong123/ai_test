// ItemCreateRequest.java - 增强的物品创建请求DTO
package com.item.management.dto.request;

import com.item.management.enums.ItemStatus;
import com.item.management.validation.ValidationGroups;
import com.item.management.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

@Data
@Schema(description = "创建物品请求")
public class ItemCreateRequest {

    @NotBlank(message = "物品名称不能为空", groups = {Default.class, ValidationGroups.Create.class})
    @Size(min = 1, max = 100, message = "物品名称长度必须在1-100个字符之间",
            groups = {Default.class, ValidationGroups.Create.class})
    @Schema(description = "物品名称", example = "Java编程思想", required = true)
    private String name;

    @Size(max = 500, message = "物品描述长度不能超过500个字符",
            groups = {Default.class, ValidationGroups.Create.class})
    @Schema(description = "物品描述", example = "经典的Java编程书籍")
    private String description;

    @Size(max = 50, message = "类别长度不能超过50个字符",
            groups = {Default.class, ValidationGroups.Create.class})
    @Schema(description = "物品类别", example = "图书")
    private String category;

    @ValidEnum(enumClass = ItemStatus.class, message = "物品状态必须是AVAILABLE、BORROWED或MAINTENANCE",
            groups = {Default.class, ValidationGroups.Create.class})
    @Schema(description = "物品状态", example = "AVAILABLE", defaultValue = "AVAILABLE")
    private ItemStatus status = ItemStatus.AVAILABLE;
}