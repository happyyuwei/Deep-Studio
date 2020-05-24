package org.deepstudio.bean;

import lombok.Data;

import java.util.List;

/**
 * 训练损失
 */
@Data
public class LossData {

    //损失名称
    String lossName;

    //训练集损失
    List<Float> trainLoss;

    //测试集损失
    List<Float> testLoss;


}
