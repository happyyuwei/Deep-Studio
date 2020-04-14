package org.deepstudio.compile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PythonPage {

    //区分字符串与变量，在变量前加该标签
    final public static String VAR_LABEL="var:";
    //过程分为函数和变量
    //变量赋值类似于：a=b
    final public static String PROCESS_VARIABLE="process_variable";
    //函数调用类似于：a=b()
    final public static String PROCESS_FUNCTION="process_function";

    //页名，需要包含.py后缀
    private String pageName;

    //package 列表，不允许使用 as 重新取名
    private Set<String> packageSet;

    //函数列表
    private List<PythonFunction> pythonFunctionList;

    //主函数
    //主要数只允许一句话，不可以加入参数
    //在 __name__=="__main__"中调用
    private String mainFunctionName;


    /**
     * 构造函数
     * @param pageName 页名，需要包含“.py"后缀
     */
    public PythonPage(String pageName){
        this.pageName=pageName;
        this.packageSet=new HashSet<>();
        this.pythonFunctionList=new ArrayList<>();
    }

    /**
     * 主函数名为为__name__=="__main__"中调用的函数。
     * @param mainFunctionName 主函数名
     */
    public void setMainFunctionName(String mainFunctionName) {
        this.mainFunctionName = mainFunctionName;
    }

    /**
     * 导入包，若存在多个包，则多次调用
     * @param packageName 包名
     */
    public void importPackage(String packageName){
        this.packageSet.add(packageName);
    }

    /**
     * 追加函数
     * @param pythonFunction python函数
     */
    public void appendFunction(PythonFunction pythonFunction){
        this.pythonFunctionList.add(pythonFunction);
    }

    //getter and setter......................................

    public String getPageName() {
        return pageName;
    }

    public Set<String> getPackageSet() {
        return packageSet;
    }

    public List<PythonFunction> getPythonFunctionList() {
        return pythonFunctionList;
    }

    public String getMainFunctionName() {
        return mainFunctionName;
    }
}
