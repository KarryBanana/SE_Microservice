package com.buaa.song.dto;

import lombok.Data;

import java.util.List;

/**
 * @FileName: PageAndSort
 * @author: ProgrammerZhao
 * @Date: 2020/11/3
 * @Description:
 */
@Data
public class PageAndSortDto {

    private Integer page;
    private Integer limit;
    private List<MyOrder> myOrders;

    @Data
    public static class MyOrder {
        private String property;
        private String direction;
    }

}