package com.hureru.iam.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author zheng
 */
@Data
public class UserProfileDTO {
    @Size(min = 2, max = 20, message = "昵称需要2-20个字符")
    private String nickname;
    private String avatarUrl;
    @Size(max = 100, message = "个人简介最长100个字符")
    private String bio;

    @Deprecated
    @AssertTrue(message = "至少需要一个更新字段")
    private boolean isAtLeastOneFieldPresent() {
        return nickname != null || avatarUrl != null || bio != null;
    }


}
