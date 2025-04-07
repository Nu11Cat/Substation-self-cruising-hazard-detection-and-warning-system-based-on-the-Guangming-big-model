package com.zizisuo.mapper;

import com.zizisuo.pojo.Image;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ImageMapper {
    /**
     *  根据id查询数据
     * @param id
     * @return
     */
    Image getById(Integer id);

    /**
     * 分页查询数据
     * @return
     */

    public List<Image> list(String name, String markings,String createUser, LocalDate begin, LocalDate end);
    /**
     * 删除数据
     * @param id
     */
    @Delete("delete from image where id = #{id}")
    void delete(Integer id);

    /**
     * 新增
     * @param image
     */
    void add(Image image);

    /**
     * 更新
     * @param image
     */
    void update(Image image);

    /**
     * 获取本日本周本月数据
     * @return
     */
    @Select("SELECT COUNT(*) AS data_day\n" +
            "FROM image\n" +
            "WHERE DATE(create_time) = CURDATE();")
    int getDataDay();
    @Select("SELECT COUNT(*) AS data_week\n" +
            "FROM image\n" +
            "WHERE YEARWEEK(create_time, 1) = YEARWEEK(CURDATE(), 1);")
    int getDataWeek();
    @Select("SELECT COUNT(*) AS data_month\n" +
            "FROM image\n" +
            "WHERE YEAR(create_time) = YEAR(CURDATE())\n" +
            "  AND MONTH(create_time) = MONTH(CURDATE());")
    int getDataMouth();

    /**
     * 获取日期范围内的所有image数据
     * @param begin
     * @param end
     * @return
     */
    List<Image> getDataList(LocalDate begin, LocalDate end);
}
