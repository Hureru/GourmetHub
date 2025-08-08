package com.hureru.product_artisan.dto;

import com.hureru.iam.dto.group.ChildCreate;
import com.hureru.iam.dto.group.Create;
import com.hureru.iam.dto.group.Update;
import com.hureru.product_artisan.bean.Artisan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @author zheng
 */
@Data
public class ArtisanDTO {
    @NotBlank(groups = {Update.class, ChildCreate.class})
    private String id;
    @Email(groups = Create.class, message = "邮箱格式不正确")
    private String email;
    @Size(min = 6, max = 20, message = "密码长度需在6-20之间", groups = Create.class)
    private String password;
    @Size(min = 6, max = 20, message = "昵称长度需在6-40之间", groups = {Update.class, ChildCreate.class})
    private String name;
    @Size(min = 10, max = 200, message = "品牌故事长度需在10-200之间", groups = {Update.class, ChildCreate.class})
    private String brandStory;
    @NotNull(groups = {Update.class, ChildCreate.class})
    private Artisan.Location location;
    @NotBlank(groups = {Update.class, ChildCreate.class})
    private String logoUrl;

    private List<@NotBlank(groups = {Update.class, ChildCreate.class}) @Size(max = 20, groups = {Update.class, ChildCreate.class}) String> certifications;
}
