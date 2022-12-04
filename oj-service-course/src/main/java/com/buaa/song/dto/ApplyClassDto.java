package com.buaa.song.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApplyClassDto {
    protected Integer userId;
    protected Integer classId;
    protected String info;
}
