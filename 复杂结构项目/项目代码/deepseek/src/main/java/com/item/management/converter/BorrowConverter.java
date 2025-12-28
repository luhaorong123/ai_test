// BorrowConverter.java - 借阅记录转换器
package com.item.management.converter;

import com.item.management.dto.response.BorrowDetailDTO;
import com.item.management.dto.response.BorrowSummaryDTO;
import com.item.management.dto.response.ItemSummaryDTO;
import com.item.management.dto.response.UserSummaryDTO;
import com.item.management.entity.BorrowRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BorrowConverter {

    private final UserConverter userConverter;
    private final ItemConverter itemConverter;

    public BorrowSummaryDTO toSummaryDTO(BorrowRecord record) {
        if (record == null) {
            return null;
        }

        return BorrowSummaryDTO.builder()
                .id(record.getId())
                .userId(record.getUser() != null ? record.getUser().getId() : null)
                .username(record.getUser() != null ? record.getUser().getUsername() : null)
                .itemId(record.getItem() != null ? record.getItem().getId() : null)
                .itemName(record.getItem() != null ? record.getItem().getName() : null)
                .itemCategory(record.getItem() != null ? record.getItem().getCategory() : null)
                .borrowDate(record.getBorrowDate())
                .expectedReturnDate(record.getExpectedReturnDate())
                .actualReturnDate(record.getActualReturnDate())
                .status(record.getStatus())
                .overdueDays(record.getOverdueDays())
                .isUpcoming(record.isExpired() ? false :
                        java.time.temporal.ChronoUnit.DAYS.between(
                                java.time.LocalDate.now(), record.getExpectedReturnDate()) <= 3)
                .isOverdue(record.isOverdue() || record.isExpired())
                .build();
    }

    public BorrowDetailDTO toDetailDTO(BorrowRecord record) {
        if (record == null) {
            return null;
        }

        UserSummaryDTO userSummary = record.getUser() != null ?
                userConverter.toSummaryDTO(record.getUser()) : null;

        ItemSummaryDTO itemSummary = record.getItem() != null ?
                itemConverter.toSummaryDTO(record.getItem()) : null;

        return BorrowDetailDTO.builder()
                .id(record.getId())
                .user(userSummary)
                .item(itemSummary)
                .borrowDate(record.getBorrowDate())
                .expectedReturnDate(record.getExpectedReturnDate())
                .actualReturnDate(record.getActualReturnDate())
                .status(record.getStatus())
                .overdueDays(record.getOverdueDays())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .remark(record.getRemark())
                .damaged(record.getDamaged())
                .damageDescription(record.getDamageDescription())
                .build();
    }

    public List<BorrowSummaryDTO> toSummaryDTOList(List<BorrowRecord> records) {
        return records.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowDetailDTO> toDetailDTOList(List<BorrowRecord> records) {
        return records.stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());
    }
}