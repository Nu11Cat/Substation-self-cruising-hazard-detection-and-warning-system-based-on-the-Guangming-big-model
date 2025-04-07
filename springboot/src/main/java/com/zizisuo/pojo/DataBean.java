package com.zizisuo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataBean {
    private Integer today;
    private Integer week;
    private Integer month;
    private List images;
}
