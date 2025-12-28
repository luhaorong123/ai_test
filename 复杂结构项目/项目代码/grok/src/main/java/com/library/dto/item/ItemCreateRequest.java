package com.library.dto.item;

import com.library.enums.ItemStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemCreateRequest {

    private String isbn;

    @NotBlank(message = "书名不能为空")
    private String title;

    private String author;
    private String publisher;
    private Integer publishYear;
    private String category;

    @Min(value = 1, message = "总库存至少为1")
    private Integer totalCopies = 1;

    private String location;
    private ItemStatus status = ItemStatus.AVAILABLE;
}