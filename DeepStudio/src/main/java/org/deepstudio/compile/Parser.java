package org.deepstudio.compile;

import org.deepstudio.structure.Component;
import org.deepstudio.structure.DeepStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析器，解析模型的中间结构
 */
public class Parser {

    //模型生成函数名称模板
    final public static String MODEL_NAME_TEMPLATE="make_%s_model";
    //输出模型变量名
    final public static String MODEL_OUTPUT="_model";
    //模型包装名称，将所有层连接在一起，并使用该函数包装成tensorflow模型。
    final public static String MODEL_FUNCTION="tensorflow.keras.Model";
    //模型输入变量名称，tensorflow.keras.Model(inputs=...,outputs=...)
    final public static String MODEL_FUNCTION_INPUTS="inputs";
    //模型输出组件名称
    final public static String MODEL_FUNCTION_OUTPUTS="outputs";
    //tenosrflow输入层定义
    final public static String INPUT_COMPONENT="tensorflow.keras.layers.Input";
    //输出变量名前缀，每一层的输出使用 _output_层名
    final public static String OUTPUT_VARIABLE="_output_";

    //测试函数名称，每个生成的模型文件，都包含一个测试函数，用于输出该模型的结构。
    // 其函数名：def _test():
    //其内容为: _model=make_model()
    //         _model.summary()
    //其中 make_model() 的名字为创建的模型函数名，每次可能不一样。
    final public static String TEST_FUNCTION_NAME="_test";
    //测试时生成模型的名称
    final public static String TEST_MODEL_NAME="_model";
    //查看参数方法
    final public static String TEST_FUNCTION_SUMMARY=".summary";

    /**
     * 将模型结构编译成tensorflow代码
     *
     * @param deepStructure 网络结构语言
     * @return python结构
     */
    public static PythonPage parseStructure(DeepStructure deepStructure) {

        //文件名是网络名称+.py
        PythonPage page = new PythonPage(deepStructure.getName() + ".py");

        //导入包
        List<String> packageList = deepStructure.getPackageList();
        for (String each : packageList) {
            page.importPackage(each);
        }

        //创建函数
        //函数名为 make_网络名_model()
        PythonFunction function = new PythonFunction(String.format(MODEL_NAME_TEMPLATE, deepStructure.getName()));

        //获取所有层
        Map<String, Component> layers = deepStructure.getLayers();
        for (Map.Entry<String, Component> e : layers.entrySet()) {
            Component component = e.getValue();
            function.invokeProcess(component.getComponentName(), PythonPage.PROCESS_FUNCTION, null, component.getArgsMap(), new String[]{e.getKey()});
        }
        //获取所有连接
        Map<String, List<String>> connections = deepStructure.getConnections();

        //连接所有层
        //获取所有层
        List<String> headList = deepStructure.getHeadList();
        //输出变量名为：output_输入名
        for (String head : headList) {
            String outputVariable = Parser.OUTPUT_VARIABLE + head;
            //创建复制过程
            function.invokeProcess(head, PythonPage.PROCESS_VARIABLE, null, null, new String[]{outputVariable});
        }

        //创建尾节点列表
        List<String> endLayers=new ArrayList<>();

        for(String head:headList){
            //解析
            Parser.parseConnections(head, Parser.OUTPUT_VARIABLE+head,function, connections, layers, endLayers);
        }

//        Map<String, Object> param=new HashMap<>();
//        param.put(Parser.MODEL_FUNCTION_INPUTS);
        //整理输入
        Map<String, Object> inputParams= Parser.generateModelArray(headList, Parser.MODEL_FUNCTION_INPUTS);
        //整理输出
        Map<String, Object> outputParams=Parser.generateModelArray(endLayers, Parser.MODEL_FUNCTION_OUTPUTS);
        //合并
        Map<String, Object> params=new HashMap<>();
        params.putAll(inputParams);
        params.putAll(outputParams);

       function.invokeProcess(Parser.MODEL_FUNCTION, PythonPage.PROCESS_FUNCTION, null, params, new String[]{Parser.MODEL_OUTPUT});

        //返回
        function.addReturn(Parser.MODEL_OUTPUT);

        //添加函数
        page.appendFunction(function);

        //添加主函数
        page.setMainFunctionName(Parser.TEST_FUNCTION_NAME);

        //添加测试函数
        //创建模型
        PythonFunction test_function = new PythonFunction(Parser.TEST_FUNCTION_NAME);
        test_function.invokeProcess(function.getFunctionName(),PythonPage.PROCESS_FUNCTION, null,null, new String[]{Parser.TEST_MODEL_NAME});
        test_function.invokeProcess(Parser.TEST_MODEL_NAME+Parser.TEST_FUNCTION_SUMMARY,PythonPage.PROCESS_FUNCTION, null,null, null);

        //添加测试函数
        page.appendFunction(test_function);

        return page;
    }

    /**
     * 生成参数放入函数中： tf.keras.Model(inputs=inputs,outputs=outputs)
     * @param list 列表
     * @param key 键
     * @return 返回参数表
     */
    private static Map<String, Object> generateModelArray(List<String> list, String key){
        Map<String, Object> param=new HashMap<>();
        //如果只有一个，则直接输入，否则合成数组
        if(list.size()==1){
            param.put(key, PythonPage.VAR_LABEL+list.get(0));
        }else{
            List<String> paramlist=new ArrayList<>();
            for(String head:list){
                paramlist.add(PythonPage.VAR_LABEL+head);
            }
            param.put(key, paramlist);
        }
        return param;
    }

    /**
     * 目前来开，该方法会有重复代码，需要在编译的时候去重
     * @param currentLayer 当前层
     * @param lastOutput 上一层的输出
     * @param function 函数结构
     * @param connections 连接层
     * @param layers 层节点
     * @param endLayers 最后的层
     */
    private static void parseConnections(String currentLayer, String lastOutput,PythonFunction function, Map<String, List<String>> connections, Map<String, Component> layers, List<String> endLayers) {

        // 下一个节点列表
        List<String> nextList=connections.get(currentLayer);

        //如果到最后一层
        if(nextList==null){
            List<String> params = new ArrayList<>();
            String outputVariable=Parser.OUTPUT_VARIABLE+currentLayer;
            params.add(PythonPage.VAR_LABEL+lastOutput);
            endLayers.add(outputVariable);
            String[] returnValue = {outputVariable};
            function.invokeProcess(currentLayer, PythonPage.PROCESS_FUNCTION, params, null, returnValue);
            return;
        }
        //如果还有下一层继续探索
        for (String nextLayer : nextList) {
            List<String> params = new ArrayList<>();
            params.add(PythonPage.VAR_LABEL+lastOutput);

            String[] returnValue = {Parser.OUTPUT_VARIABLE+currentLayer};
            if(!layers.get(currentLayer).getComponentName().equals(Parser.INPUT_COMPONENT)) {
                function.invokeProcess(currentLayer, PythonPage.PROCESS_FUNCTION, params, null, returnValue);
            }
            parseConnections(nextLayer,Parser.OUTPUT_VARIABLE+currentLayer,function, connections,layers, endLayers);
        }
    }
}
