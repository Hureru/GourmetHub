package com.hureru.iam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author zheng
 */
@Data
public class UserDTO {
    private Long userId;
    @Email(message = "邮箱格式不正确")
    private String email;
    @Size(min = 6, max = 20, message = "密码长度需在6-20之间")
    private String password;
    @Size(min = 2, max = 10, message = "昵称长度需在2-10之间")
    private String nickname;
}

