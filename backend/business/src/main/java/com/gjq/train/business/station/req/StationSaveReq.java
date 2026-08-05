package com.gjq.train.business.station.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StationSaveReq {

    @NotBlank(message = "站名不能为空")
    @Size(max = 20, message = "站名不能超过20个字符")
    private String name;

    @NotBlank(message = "站名拼音不能为空")
    @Size(max = 50, message = "站名拼音不能超过50个字符")
    private String namePinyin;

    @NotBlank(message = "站名拼音首字母不能为空")
    @Size(max = 50, message = "站名拼音首字母不能超过50个字符")
    private String namePy;
}
