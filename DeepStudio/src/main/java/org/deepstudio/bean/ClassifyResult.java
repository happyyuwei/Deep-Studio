package org.deepstudio.bean;

import lombok.Data;

import java.util.List;

@Data
public class ClassifyResult {

    //预测标签分布
    private List<Float> predictedLabel;

    //实际标签分布
    private List<Float> targetLabel;

}
