package com.buaa.song.utils;

import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.dto.PageAndSortDto.MyOrder;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * @FileName: PageAndSortUtil
 * @author: ProgrammerZhao
 * @Date: 2020/11/3
 * @Description:
 */

public class PageAndSortUtil {

    public static Sort transformToSort(PageAndSortDto pageAndSort){
        List<Order> orders = new ArrayList<>();
        List<MyOrder> myOrders = pageAndSort.getMyOrders();
        for(MyOrder myorder : myOrders){
            String property = myorder.getProperty();
            String direction = myorder.getDirection();
            Order order = null;
            if("asc".equalsIgnoreCase(direction)){
                order = Order.asc(property);
            }else {
                order = Order.desc(property);
            }
            orders.add(order);
        }
        return Sort.by(orders);
    }

}