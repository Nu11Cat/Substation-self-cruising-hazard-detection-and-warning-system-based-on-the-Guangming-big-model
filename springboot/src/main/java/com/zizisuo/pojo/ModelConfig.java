package com.zizisuo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelConfig {
    private String currentModel;  // 当前使用的模型名称
} 