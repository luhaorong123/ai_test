// ItemConverter.java - 物品对象转换器
package com.item.management.converter;

import com.item.management.dto.response.ItemSummaryDTO;
import com.item.management.entity.Item;
import com.item.management.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemConverter {

    private final BorrowRecordRepository borrowRecordRepository;

    public ItemSummaryDTO toSummaryDTO(Item item) {
        if (item == null) {
            return null;
        }

        // 统计物品的借阅次数
        long borrowCount = borrowRecordRepository.findByItemId(item.getId(),
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

        // 截取描述，避免过长
        String shortDescription = item.getDescription();
        if (shortDescription != null && shortDescription.length() > 100) {
            shortDescription = shortDescription.substring(0, 100) + "...";
        }

        return ItemSummaryDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(shortDescription)
                .category(item.getCategory())
                .status(item.getStatus())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .borrowCount(borrowCount)
                .build();
    }

    public List<ItemSummaryDTO> toSummaryDTOList(List<Item> items) {
        return items.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }
}