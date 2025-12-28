package com.library.dto.item;

import com.library.enums.ItemStatus;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ItemUpdateRequest {

    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer publishYear;
    private String category;

    @Min(value = 1)
    private Integer totalCopies;

    private String location;
    private ItemStatus status; // 管理员可手动设为 LOST 等
}