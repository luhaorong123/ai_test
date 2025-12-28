package com.library.dto.item;

import com.library.entity.Item;
import com.library.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {

    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer publishYear;
    private String category;
    private Integer totalCopies;
    private Integer availableCopies;
    private String location;
    private ItemStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getIsbn(),
                item.getTitle(),
                item.getAuthor(),
                item.getPublisher(),
                item.getPublishYear(),
                item.getCategory(),
                item.getTotalCopies(),
                item.getAvailableCopies(),
                item.getLocation(),
                item.getStatus(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}