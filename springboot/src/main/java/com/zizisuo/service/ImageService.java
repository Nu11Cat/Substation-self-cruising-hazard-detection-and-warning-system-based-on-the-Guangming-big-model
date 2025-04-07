package com.zizisuo.service;

import com.zizisuo.pojo.DataBean;
import com.zizisuo.pojo.Image;
import com.zizisuo.pojo.PageBean;
import com.zizisuo.pojo.PostMessage;

import java.time.LocalDate;

public interface ImageService {

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    PageBean page(Integer page, Integer pageSize, String name, String markings, String createUser,LocalDate begin, LocalDate end);
    /**
     * 删除数据
     */
    void delete(Integer id);

    /**
     * 新增数据
     * @param image
     */
    void add(Image image);

    /**
     * 根据id查询数据
     * @param id
     * @return
     */
    Image getById(Integer id);

    /**
     * 更新数据
     * @param image
     */
    void update(Image image);

    /**
     * 获取
     * @param perUrl
     * @return
     */
    PostMessage topost(String perUrl);

    /**
     * 获取统计数据
     * @param begin
     * @param end
     * @return
     */
    DataBean getdata(LocalDate begin, LocalDate end);
}
