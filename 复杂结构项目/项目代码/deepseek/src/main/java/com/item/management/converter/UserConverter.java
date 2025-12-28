// UserConverter.java - 用户对象转换器
package com.item.management.converter;

import com.item.management.dto.response.UserSummaryDTO;
import com.item.management.entity.User;
import com.item.management.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final BorrowRecordRepository borrowRecordRepository;

    public UserSummaryDTO toSummaryDTO(User user) {
        if (user == null) {
            return null;
        }

        // 统计用户的借阅信息
        long totalBorrows = borrowRecordRepository.findByUserId(user.getId(),
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

        long activeBorrows = borrowRecordRepository.findByUserIdAndStatus(user.getId(),
                com.item.management.enums.BorrowStatus.ACTIVE,
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

        long overdueBorrows = borrowRecordRepository.findUserOverdueRecords(user.getId(),
                java.time.LocalDate.now(), org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

        return UserSummaryDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .borrowCount(totalBorrows)
                .activeBorrowCount(activeBorrows)
                .overdueCount(overdueBorrows)
                .build();
    }

    public List<UserSummaryDTO> toSummaryDTOList(List<User> users) {
        return users.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }
}