package org.deepstudio.bean;

import lombok.Data;

import java.util.List;

/**
 * 曲线数据
 */
@Data
public class CurveData {
    //训练损失，包含训练集与测试集
    private List<LossData> lossData;

    //评估，至做测试集
    private List<EvalData> evalData;

    //时间
    private List<String> time;

    //轮数
    private List<Integer> epoch;

}
