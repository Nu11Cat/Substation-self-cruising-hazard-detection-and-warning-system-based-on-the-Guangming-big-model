
CREATE DATABASE zhi

-- 创建表 image
CREATE TABLE `image` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` VARCHAR(255) NOT NULL COMMENT '名字',
    `per_path` VARCHAR(255) NOT NULL COMMENT '处理前图片的路径',
    `post_path` VARCHAR(255) NOT NULL COMMENT '处理后图片的路径',
    `annotated_path` VARCHAR(255) DEFAULT NULL COMMENT '注释后的路径',
    `create_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '修改时间',
    `other` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片信息表';

-- 插入初始化数据
INSERT INTO `image` (`id`,`name`, `per_path`, `post_path`, `annotated_path`, `create_time`, `update_time`, `other`)
VALUES (
    1,
    'testname',
    'https://web-zizisuo.oss-cn-hangzhou.aliyuncs.com/1.jpg',
    'https://web-zizisuo.oss-cn-hangzhou.aliyuncs.com/8ab3eeb3-ef2e-434a-af57-bfaab379cf01.jpg',
    'https://web-zizisuo.oss-cn-hangzhou.aliyuncs.com/8ab3eeb3-ef2e-434a-af57-bfaab379cf01.jpg',
    NOW(),
    NOW(),
    'test备注'
);



CREATE TABLE `marking` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `to_id` INT NOT NULL COMMENT '指向 image 表的主键',
    `string` VARCHAR(255) NOT NULL COMMENT '字符串数据',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`to_id`) REFERENCES `image`(`id`) -- 正确关联到 image 表
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标记表';


INSERT INTO `marking` (`id`,`to_id`, `string`)
VALUES 
(1,1, 'message1'),
(1,1, 'message2');