package org.deepstudio.structure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.*;

/**
 * 神经网络结构
 */
public class DeepStructure {

    //模型函数名称
    final public static String JSON_NAME="name";
    //模型使用的组件包
    final public static String JSON_PACKAGES="packages";
    //输入的参数
    final public static String JSON_ARGS="args";
    //包含的层，为一个hashmap, key为层名称，value为该层具体信息
    final public static String JSON_LAYERS="layers";
    //各层连接方式，为邻接表
    final public static String JSON_CONNECTIONS="connections";
    //第一层名称
    final public static String JSON_HEADS="heads";
    //组件名
    final public static String JSON_COMPONENT="component";



    //定义层数结构
    final private Map<String, Component> layers;
    //定义层的连接结构
    final private Map<String, List<String>> connections;
    //头节点入口，可能存在多个路口
    final private List<String> headList;
    //外部包
    final private List<String> packageList;
    //名称
    final private String name;
    //输入参数
    final private List<String> args;

    /**
     *
     * @param json json字符串
     */
    public DeepStructure(String json){
        this.layers=new HashMap<>();
        this.connections=new HashMap<>();

        //解析json
        JSONObject jsonObject=JSONObject.parseObject(json);
        //解析名称
        this.name=jsonObject.getString(DeepStructure.JSON_NAME);
        //解析传入参数
        this.args=jsonObject.getJSONArray(DeepStructure.JSON_ARGS).toJavaList(String.class);

        //解析头节点
        this.headList=jsonObject.getJSONArray(DeepStructure.JSON_HEADS).toJavaList(String.class);

        //解析包
        this.packageList=jsonObject.getJSONArray(DeepStructure.JSON_PACKAGES).toJavaList(String.class);

        //解析 layers
        JSONObject layersObject=jsonObject.getJSONObject(DeepStructure.JSON_LAYERS);
        //遍历所有层
        for(String layerName:layersObject.keySet()){
            JSONObject componentObject=layersObject.getJSONObject(layerName);
            Component component=new Component(componentObject.getString(DeepStructure.JSON_COMPONENT));
            //遍历所有参数
            for(String key:componentObject.keySet()){
                if(!key.equals(DeepStructure.JSON_COMPONENT)){
                    component.setArg(key, parseType(componentObject.get(key)));
                }
            }
            this.layers.put(layerName, component);
        }

        //解析connections
        JSONObject connectionsObject=jsonObject.getJSONObject(DeepStructure.JSON_CONNECTIONS);
        //遍历
        for(String key: connectionsObject.keySet()){
            this.connections.put(key, connectionsObject.getJSONArray(key).toJavaList(String.class));
        }
//        System.out.println();
    }

    /**
     * 将 bigdicimal转换成 double, 将JSONarray转成 arraylist
     * @param obj 对象
     * @return 对象
     */
    public Object parseType(Object obj){

        if(obj instanceof BigDecimal){
            return ((BigDecimal) obj).doubleValue();
        }else if(obj instanceof JSONArray){
            List<Object> list=new ArrayList<>();
            for(int i=0;i<((JSONArray) obj).size();i++){
                Object each=((JSONArray) obj).get(i);
                if(each instanceof BigDecimal){
                    list.add(((BigDecimal) each).doubleValue());
                }else{
                    list.add(each);
                }
            }
            return list;
        }else{
            return obj;
        }
    }

    public Map<String, Component> getLayers() {
        return layers;
    }

    public List<String> getPackageList() {
        return packageList;
    }

    public Map<String, List<String>> getConnections() {
        return connections;
    }

    public List<String> getHeadList() {
        return headList;
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        return args;
    }
}
