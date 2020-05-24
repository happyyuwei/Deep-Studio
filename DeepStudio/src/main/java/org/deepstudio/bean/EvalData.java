package org.deepstudio.bean;

import lombok.Data;

import java.util.List;

/**
 * 评估数据
 */
@Data
public class EvalData {

    //评估名
    private String evalName;

    //数据
    private List<Float> data;
}
