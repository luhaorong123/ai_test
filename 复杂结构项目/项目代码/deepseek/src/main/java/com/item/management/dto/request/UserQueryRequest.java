// UserQueryRequest.java - 用户查询请求DTO
package com.item.management.dto.request;

import com.item.management.enums.UserStatus;
import com.item.management.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Schema(description = "用户查询请求")
public class UserQueryRequest {

    @Schema(description = "页码，从0开始", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "排序字段", example = "createdAt", defaultValue = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "排序方向", example = "DESC", allowableValues = {"ASC", "DESC"}, defaultValue = "DESC")
    private Sort.Direction direction = Sort.Direction.DESC;

    @Schema(description = "用户名搜索关键词")
    private String username;

    @Schema(description = "邮箱搜索关键词")
    private String email;

    @Schema(description = "手机号搜索关键词")
    private String phone;

    @Schema(description = "用户类型", example = "USER")
    private UserType userType;

    @Schema(description = "账号状态", example = "ACTIVE")
    private UserStatus status;

    public Pageable toPageable() {
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }
}