package com.zizisuo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    private Integer id; // ID
    private String name; //名字
    private String perUrl; //处理前图片的路径
    private String postUrl; //处理后图片的路径
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//修改时间
    private String createUser;//创建人
    private String updateUser;//修改人
    private String markings;//标注
    private String other; // 备注
}
/*
CREATE TABLE image (
    id INT PRIMARY KEY AUTO_INCREMENT, -- ID
    name VARCHAR(255) NOT NULL, -- 名字
    per_url VARCHAR(255) NOT NULL, -- 处理前图片的路径
    post_url VARCHAR(255) NOT NULL, -- 处理后图片的路径
    create_time DATETIME NOT NULL, -- 创建时间
    update_time DATETIME NOT NULL, -- 修改时间
    create_user VARCHAR(255) NOT NULL, -- 创建人
    update_user VARCHAR(255), -- 修改人（可以为空）
    markings TEXT, -- 标注（可以为空）
    other TEXT -- 备注（可以为空）
);
 */
/*
INSERT INTO image (id, name, per_url, post_url, create_time, update_time, create_user, update_user, markings, other)
VALUES (
    1,
    '仓库1',
    'https://web-zizisuo.oss-cn-hangzhou.aliyuncs.com/f7ddbb5f-e887-4a25-aba0-e76aff241f5f.jpg',
    'https://web-zizisuo.oss-cn-hangzhou.aliyuncs.com/f7ddbb5f-e887-4a25-aba0-e76aff241f5f.jpg',
    '2025-03-05 20:39:15',
    '2025-03-05 20:39:15',
    'admin',
    'admin',
    '漏油,生锈',
    '已解决'
);
 */