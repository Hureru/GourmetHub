package com.hureru.order.dto;

import com.hureru.common.annotation.AtLeastOneField;
import com.hureru.order.dto.group.Create;
import com.hureru.order.dto.group.Update;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * @author zheng
 */
@Data
@AtLeastOneField(message = "至少需要一个更新字段", groups = {Update.class})
public class AddressDTO {
    @NotBlank(groups = {Create.class})
    @Size(max = 50, groups = {Create.class, Update.class})
    private String recipientName;

    @NotBlank(groups = {Create.class})
    @Pattern(regexp = "^1[3-9]\\d{9}$", groups = {Create.class, Update.class})
    private String phoneNumber;

    @NotBlank(groups = {Create.class})
    @Size(max = 200, groups = {Create.class, Update.class})
    private String streetAddress;

    @NotBlank(groups = {Create.class})
    @Size(max = 50, groups = {Create.class, Update.class})
    private String city;

    @NotBlank(groups = {Create.class})
    @Size(max = 50, groups = {Create.class, Update.class})
    private String stateProvince;

}

