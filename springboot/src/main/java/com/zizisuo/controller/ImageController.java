package com.zizisuo.controller;

import com.zizisuo.pojo.*;
import com.zizisuo.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins = "http://localhost:7070")  // 允许来自7070端口的请求
@RestController
@Slf4j
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       String name,
                       String markings,
                       String createUser,
                       @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate begin,
                       @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate end) {
        log.info("分页查询：{}，{},{},{},{},{},{}",page,pageSize,name,markings,createUser,begin,end);
        PageBean pageBean = imageService.page(page,pageSize,name,markings,createUser,begin,end);
        return Result.success(pageBean);
    }
    /**
     * 删除数据
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        log.info("根据id删除数据:{}",id);
        imageService.delete(id);
        return Result.success();
    }

    /**
     * 新增数据
     * @param image
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Image image){
        log.info("新增数据");
        imageService.add(image);
        return Result.success();
    }

    /**
     * 根据id查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id){
        log.info("根据id:{}查询数据",id);
        Image image= imageService.getById(id);
        return Result.success(image);
    }

    /**
     * 更新数据
     * @param image
     * @return
     */
    @PutMapping
    public Result update(@RequestBody Image image){
        log.info("更新员工信息:{}",image);
        imageService.update(image);
        return Result.success();
    }

    /**
     * 传出处理后url
     * @param perUrl
     * @return
     */
    @GetMapping("/post")
    public Result topost(String perUrl){
        PostMessage postMessage=imageService.topost(perUrl);
        return Result.success(postMessage);
    }

    /**
     * 获取统计数据
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/data")
    public Result getdata(@DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate begin,
                          @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate end){
        DataBean dataBean = imageService.getdata(begin,end);
        return Result.success(dataBean);
    }
}
