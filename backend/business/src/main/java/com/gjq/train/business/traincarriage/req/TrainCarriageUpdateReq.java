package com.gjq.train.business.traincarriage.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TrainCarriageUpdateReq {

    @NotNull(message = "火车车厢ID不能为空")
    private Long id;

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20, message = "车次编号不能超过20个字符")
    private String trainCode;

    @NotNull(message = "厢号不能为空")
    @Min(value = 1, message = "厢号必须大于0")
    private Integer index;

    @NotBlank(message = "座位类型不能为空")
    private String seatType;

    @NotNull(message = "排数不能为空")
    @Min(value = 1, message = "排数必须大于0")
    private Integer rowCount;
}
