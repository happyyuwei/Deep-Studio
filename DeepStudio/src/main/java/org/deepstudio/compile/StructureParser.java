package org.deepstudio.compile;

import org.deepstudio.structure.Component;
import org.deepstudio.structure.DeepStructure;

import java.util.*;

/**
 * 解析器，解析模型的中间结构
 */
public class StructureParser {

    //模型生成函数名称模板
    final public static String MODEL_NAME_TEMPLATE = "make_%s_model";
    //输出模型变量名
    final public static String MODEL_OUTPUT = "_model";
    //模型包装名称，将所有层连接在一起，并使用该函数包装成tensorflow模型。
    final public static String MODEL_FUNCTION = "tensorflow.keras.Model";
    //模型输入变量名称，tensorflow.keras.Model(inputs=...,outputs=...)
    final public static String MODEL_FUNCTION_INPUTS = "inputs";
    //模型输出组件名称
    final public static String MODEL_FUNCTION_OUTPUTS = "outputs";
    //tenosrflow输入层定义
    final public static String INPUT_COMPONENT = "tensorflow.keras.layers.Input";
    //输出变量名前缀，每一层的输出使用 _output_层名
    final public static String OUTPUT_VARIABLE = "_output_";

    //测试函数名称，每个生成的模型文件，都包含一个测试函数，用于输出该模型的结构。
    // 其函数名：def _test():
    //其内容为: _model=make_model()
    //         _model.summary()
    //其中 make_model() 的名字为创建的模型函数名，每次可能不一样。
    final public static String TEST_FUNCTION_NAME = "_test";
    //测试时生成模型的名称
    final public static String TEST_MODEL_NAME = "_model";
    //查看参数方法
    final public static String TEST_FUNCTION_SUMMARY = ".summary";

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


        //注册传入参数名称
        List<String> argList = deepStructure.getArgs();
        for (String arg : argList) {
            function.addArg(arg);
        }

        //获取所有层
        Map<String, Component> layers = deepStructure.getLayers();
        //创建所有组件名称
        for (Map.Entry<String, Component> e : layers.entrySet()) {
            Component component = e.getValue();
            function.invokeProcess(component.getComponentName(), PythonPage.PROCESS_FUNCTION, null, component.getArgsMap(), new String[]{e.getKey()});
        }

        //连接所有层
        //获取所有头结点
        List<String> headList = deepStructure.getHeadList();
        //输出变量名为：output_输入名
        for (String head : headList) {
            String outputVariable = StructureParser.OUTPUT_VARIABLE + head;
            //创建复制过程
            function.invokeProcess(head, PythonPage.PROCESS_VARIABLE, null, null, new String[]{outputVariable});
        }

        //获取拓扑排序顺序
        List<List<String>> order=StructureParser.sortConnections(deepStructure.getConnections());
        //计算逆邻接表
        Map<String, List<String>> rev=StructureParser.revConnections(deepStructure.getConnections());

        //生成python过程
        //从第一个位置开始，输入层无需调用，已在上方处理
        //按优先级生成函数
        for(int i=1;i<order.size();i++){
            //获取同一优先级的函数
            List<String> functionList=order.get(i);
            //对每一个函数，通过逆邻接表查询输入
            for(String f:functionList){
                List<String> combineList=new ArrayList<>();
                //对每一个变量进行修改，添加var:抬头，修改变量名为 _output_+组件名
                for(String from:rev.get(f)){
                    combineList.add(PythonPage.VAR_LABEL+StructureParser.OUTPUT_VARIABLE+from);
                }
                //将其放置与参数列表，
                List<Object> paramList = new ArrayList<>();
                //注意，如果该参数是数组，因此需要将该数组作为Object元素传入参数列表
                if(combineList.size()>1) {
                    paramList.add(combineList);
                }else{
                    //如果只有一个参数，则不要合并成数组
                    paramList.add(combineList.get(0));
                }
                function.invokeProcess(f, PythonPage.PROCESS_FUNCTION, paramList, null, new String[]{StructureParser.OUTPUT_VARIABLE + f});
            }
        }

        //获取尾节点列表
        List<String> endLayers = new ArrayList<>();
        for(String each:order.get(order.size()-1)){
            endLayers.add(StructureParser.OUTPUT_VARIABLE+each);
        }

        //整理返回值中tf.keras.Model()中输入参数
        Map<String, Object> inputParams = StructureParser.generateModelArray(headList, StructureParser.MODEL_FUNCTION_INPUTS);
        //整理返回值中tf.keras.Model()中输出参数
        Map<String, Object> outputParams = StructureParser.generateModelArray(endLayers, StructureParser.MODEL_FUNCTION_OUTPUTS);
        //合并
        Map<String, Object> params = new HashMap<>();
        params.putAll(inputParams);
        params.putAll(outputParams);
        //创建过程
        function.invokeProcess(StructureParser.MODEL_FUNCTION, PythonPage.PROCESS_FUNCTION, null, params, new String[]{StructureParser.MODEL_OUTPUT});

        //返回
        function.addReturn(StructureParser.MODEL_OUTPUT);

        //添加该模型函数
        page.appendFunction(function);

        //添加主函数名称
        page.setMainFunctionName(StructureParser.TEST_FUNCTION_NAME);

        //添加模型内的测试函数，调用生成的模型，并调用summary()
        //创建模型
        PythonFunction test_function = new PythonFunction(StructureParser.TEST_FUNCTION_NAME);
        test_function.invokeProcess(function.getFunctionName(), PythonPage.PROCESS_FUNCTION, null, null, new String[]{StructureParser.TEST_MODEL_NAME});
        test_function.invokeProcess(StructureParser.TEST_MODEL_NAME + StructureParser.TEST_FUNCTION_SUMMARY, PythonPage.PROCESS_FUNCTION, null, null, null);

        //添加测试函数
        page.appendFunction(test_function);

        return page;
    }

    /**
     * 生成参数放入函数中： tf.keras.Model(inputs=inputs,outputs=outputs)
     * 当输入的情况下：tf.keras.Model(inputs=[a,b,c],outputs=[d,e,f])
     *
     * @param list 列表
     * @param key  键
     * @return 返回参数表
     */
    private static Map<String, Object> generateModelArray(List<String> list, String key) {
        Map<String, Object> param = new HashMap<>();
        //如果只有一个，则直接输入，否则合成数组
        if (list.size() == 1) {
            param.put(key, PythonPage.VAR_LABEL + list.get(0));
        } else {
            List<String> paramlist = new ArrayList<>();
            for (String head : list) {
                paramlist.add(PythonPage.VAR_LABEL + head);
            }
            param.put(key, paramlist);
        }
        return param;
    }

    /**
     * 目前来开，该方法会有重复代码，需要在编译的时候去重，使用dfs方式调用
     *
     * @param currentLayer 当前层
     * @param lastOutput   上一层的输出
     * @param function     函数结构
     * @param connections  连接层
     * @param layers       层节点
     * @param endLayers    最后的层
     */
    private static void parseConnections(String currentLayer, String lastOutput, PythonFunction function, Map<String, List<String>> connections, Map<String, Component> layers, List<String> endLayers) {

        // 下一个节点列表
        List<String> nextList = connections.get(currentLayer);

        //如果到最后一层
        if (nextList == null) {
            List<Object> params = new ArrayList<>();
            String outputVariable = StructureParser.OUTPUT_VARIABLE + currentLayer;
            params.add(PythonPage.VAR_LABEL + lastOutput);
            endLayers.add(outputVariable);
            String[] returnValue = {outputVariable};
            function.invokeProcess(currentLayer, PythonPage.PROCESS_FUNCTION, params, null, returnValue);
            return;
        }
        //如果还有下一层继续探索
        for (String nextLayer : nextList) {
            List<Object> params = new ArrayList<>();
            params.add(PythonPage.VAR_LABEL + lastOutput);

            String[] returnValue = {StructureParser.OUTPUT_VARIABLE + currentLayer};
            if (!layers.get(currentLayer).getComponentName().equals(StructureParser.INPUT_COMPONENT)) {
                function.invokeProcess(currentLayer, PythonPage.PROCESS_FUNCTION, params, null, returnValue);
            }
            parseConnections(nextLayer, StructureParser.OUTPUT_VARIABLE + currentLayer, function, connections, layers, endLayers);
        }
    }

    /**
     * 将邻接表解析成逆邻接表
     * 支持邻接表格式：所有节点均在key中出现。若该节点不指向后续节点，则value为空的list,不可以直接传null!
     *
     * @param connections 邻接表
     * @return 逆邻接表
     */
    public static Map<String, List<String>> revConnections(Map<String, List<String>> connections) {

        //逆邻接表，key=终点，value=起点列表
        Map<String, List<String>> rev = new HashMap<>();

        //初始化所有节点
        for (Map.Entry<String, List<String>> e : connections.entrySet()) {
            rev.put(e.getKey(), new ArrayList<>());
        }

        //遍历所有节点
        for (Map.Entry<String, List<String>> e : connections.entrySet()) {
            List<String> toArray = e.getValue();
            String from = e.getKey();
            for (String to : toArray) {
                List<String> fromArray = rev.get(to);
                fromArray.add(from);
            }
        }
        return rev;
    }

    /**
     * 移除图中多个连接
     * @param connections 邻接表
     * @param elementList  需要移除的元素
     * @return 新的邻接表，其地址与原邻接表无关
     */
    public static Map<String, List<String>> removeConnection(Map<String, List<String>> connections, List<String> elementList) {
        //创建结果
        Map<String, List<String>> result = new HashMap<>();
        //去除元素
        for (Map.Entry<String, List<String>> e : connections.entrySet()) {
            //只有不想等的才会保存
            if(!elementList.contains(e.getKey())){
                List<String> array=new ArrayList<>();
                List<String> originArray=e.getValue();
                for(String each:originArray){
                    if(!elementList.contains(each)){
                        array.add(each);
                    }
                }
                result.put(e.getKey(), array);
            }
        }
        return result;
    }

    /**
     * 计算图的拓扑排序
     *
     * @param connections 邻接表
     * @return 拓扑排序，每一个子List包含同级元素，元素按首字母顺序排序
     */
    public static List<List<String>> sortConnections(Map<String, List<String>> connections) {

        //排序顺序
        List<List<String>> orderList=new ArrayList<>();

        //临时表
        Map<String, List<String>> tempConnections=connections;

        //直到空表停止
        while(!tempConnections.isEmpty()) {
            List<String> array=new ArrayList<>();
            //计算逆邻接表
            Map<String, List<String>> rev=StructureParser.revConnections(tempConnections);
            //从头查看所有没有入度的节点，将其取出，取出该节点，并继续
            for (Map.Entry<String, List<String>> e : rev.entrySet()) {
                if (e.getValue().isEmpty()) {
                    array.add(e.getKey());
                }
            }
            //排序,字典顺序
            Collections.sort(array);
            //移除该节点
            tempConnections=StructureParser.removeConnection(tempConnections,array);
            //添加结果
            orderList.add(array);
        }
        return orderList;
    }



}
