package org.deepstudio.compile;

import java.util.*;

public class PythonFunction {

    //函数名
    private String functionName;

    //参数列表
    private List<String> argsList;

    //返回值列表
    private List<String> returnList;

    //-------------------------------------------------------------
    //以下为过程列表，每个index代表一个过程。
    //调用过程列表， 存储调用过程名称，若是函数过程，则是函数名；若是赋值过程，则是右侧变量名
    //不可以设置null
    private List<String> invokeList;

    //类型包括赋值和函数
    private List<String> processTypeList;

    //非指定参数列表，类似 function(a,b,c)
    private List<List<String>> invokeNonParamList;

    //指定变量名列表，类似 function(x=a,y=b)
    private List<Map<String,Object>> invokeParamList;
    //调用结果列表
    private List<String[]> invokeReturnList;
    //-------------------------------------------------------------------


    /**
     *
     * @param functionName 函数名
     */
    public PythonFunction(String functionName){
        this.functionName=functionName;
        this.argsList=new ArrayList<>();
        this.returnList=new ArrayList<>();
        this.invokeList=new ArrayList<>();
        this.invokeParamList=new ArrayList<>();
        this.invokeReturnList=new ArrayList<>();
        this.invokeNonParamList=new ArrayList<>();
        this.processTypeList=new ArrayList<>();
    }

    /**
     * 添加参数
     * @param arg 参数
     */
    public void addArg(String arg){
        this.argsList.add(arg);
    }

    public List<String> getArgsList() {
        return argsList;
    }

    /**
     * 调用过程
     * @param functionName 调用名
     * @param nonParams 非命名参数
     * @param params 命名参数
     * @param returnName 返回值
     */
    public void invokeProcess(String functionName, String processType, List<String> nonParams, Map<String, Object> params, String[] returnName){
        this.invokeList.add(functionName);
        this.invokeParamList.add(params);
        this.invokeNonParamList.add(nonParams);
        this.invokeReturnList.add(returnName);
        this.processTypeList.add(processType);
    }

    public void addReturn(String returnName){
        this.returnList.add(returnName);
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<String> getReturnList() {
        return returnList;
    }

    public List<String> getInvokeList() {
        return invokeList;
    }


    public List<Map<String, Object>> getInvokeParamList() {
        return invokeParamList;
    }

    public List<String[]> getInvokeReturnList() {
        return invokeReturnList;
    }

    public List<List<String>> getInvokeNonParamList() {
        return invokeNonParamList;
    }

    public List<String> getProcessTypeList() {
        return processTypeList;
    }
}
