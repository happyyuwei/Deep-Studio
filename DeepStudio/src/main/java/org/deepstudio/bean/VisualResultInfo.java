package org.deepstudio.bean;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 可视化结果信息
 */
@Data
public class VisualResultInfo {

    //图像名称列表,结果命名格式 {epoch}_{title}_{num}
    private List<String> imageList;

    //分类标签
    private Object labels;


}
