package com.connecttoweChat.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

public abstract class MybatisPlusServiceEnhancer<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    protected IPage<T> convert(Pageable pageable) {
        Page<T> page = new Page<T>();
        // setting page number
        page.setCurrent(pageable.getPageNumber() + 1);//page start 0
        // setting page size
        page.setSize(pageable.getPageSize());
        // setting sorts
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            List<OrderItem> orderItems = sort.stream().map(order -> {
                if (order.isAscending()) {
                    return OrderItem.asc(order.getProperty());
                } else {
                    return OrderItem.desc(order.getProperty());
                }
            }).collect(Collectors.toList());

            page.addOrder(orderItems);
        }

        return page;
    }

}
