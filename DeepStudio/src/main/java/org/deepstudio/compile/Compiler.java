package org.deepstudio.compile;

import org.deepstudio.util.FileUtil;

import java.io.File;
import java.util.*;

/**
 * 编译器，将python结构编译成python
 * 为了方便目前的编译，去除部分写法
 * 1. 包名不许使用简化
 * 2. 过程必须包括在函数中，不允许直接出现在页中
 * 3. if __name__=="__main__"入口只允许包括一个函数调用，且不可以传入参数。
 * 目前，本编译器不是打造通用编译python代码，而是为了生成tensorflow样板代码，因此部分语法尚不支持。
 */
public class Compiler {

    //python模板
    //import 模板
    final public static String IMPORT_TEMPLATE = "import %s";
    //函数签名模板
    final public static String FUNCTION_TEMPLATE = "def %s(%s):";
    //函数调用模板
    final public static String INVOKE_TEMPLATE = "%s(%s)";
    //函数调用带返回值模板
    final public static String INVOKE_TEMPLATE_RETURN = "%s=%s(%s)";
    //返回参数模板
    final public static String RETURN_TEMPLATE = "return %s";
    //python中使用的逗号
    final public static String PYTHON_COMMA = ",";
    //python中布尔真
    final public static String PYTHON_TRUE = "True";
    //python中布尔假
    final public static String PYTHON_FALSE = "False";
    //python中等号
    final public static String PYTHON_EQUAL = "=";
    //python中双引号
    final public static String PYTHON_QUOT = "\"";
    //python中缩进
    final public static String PYTHON_TAB = "    ";
    //python中主入口模板
    final public static String PYTHON_MAIN="if __name__=='__main__':";


    /**
     * 编译主函数
     * @param pythonPage pythonPage结构
     * @return 行列表，编译好的python代码
     */
    public static List<String> compile(PythonPage pythonPage) {

        List<String> lineList = new ArrayList<>();
        //编译包
        Set<String> packageSet = pythonPage.getPackageSet();
        for (String each : packageSet) {
            lineList.add(Compiler.generateImport(each));
        }
        //编译函数
        List<PythonFunction> pythonFunctionList = pythonPage.getPythonFunctionList();

        //编译过程
        for (PythonFunction pythonFunction : pythonFunctionList) {
            //函数名
            lineList.add(generateFunctionLine(pythonFunction.getFunctionName(), pythonFunction.getArgsList()));
            //函数过程
            List<String> invokeList = pythonFunction.getInvokeList();
            //获取不带标识的参数列表，如 function(1)
            List<List<Object>> invokeNonParamList = pythonFunction.getInvokeNonParamList();
            //获取带表示的参数列表，如function(a=1,b=2)
            List<Map<String, Object>> invokeParamList = pythonFunction.getInvokeParamList();
            //获取返回列表
            List<String[]> invokeReturnList = pythonFunction.getInvokeReturnList();
            //获取过程类型，目前定义两种：函数调用与变量赋值。
            //其中函数调用为 a=b(), 变量赋值为a=b，初始化属于变量赋值
            List<String> typeList=pythonFunction.getProcessTypeList();
            for (int i = 0; i < invokeList.size(); i++) {
                if(typeList.get(i).equals(PythonPage.PROCESS_FUNCTION)) {
                    String line = Compiler.generateInvokeLine(invokeList.get(i), invokeNonParamList.get(i), invokeParamList.get(i), invokeReturnList.get(i));
                    //缩进
                    line = Compiler.PYTHON_TAB + line;
                    lineList.add(line);
                }else{
                    String line=Compiler.generateVariableLine(invokeList.get(i), invokeReturnList.get(i));
                    //缩进
                    line = Compiler.PYTHON_TAB + line;
                    lineList.add(line);
                }
            }
            //编译返回值
            lineList.add(Compiler.PYTHON_TAB + Compiler.generateReturnLine(pythonFunction.getReturnList()));
//
        }
        //添加主函数
        lineList.add(Compiler.PYTHON_MAIN);
        lineList.add(Compiler.PYTHON_TAB+String.format(INVOKE_TEMPLATE,pythonPage.getMainFunctionName(),""));

        //去重，由于在生成tensorflow代码时会有重复，这部分需要去重。
        //目前就这么设计，之后想到更好的图搜索代码再改。
        //@since 2020.4.15
        lineList=Compiler.removeSame(lineList);

        return lineList;
    }

    /**
     * 列表去重，
     * @param list 列表
     * @return 去重的列表
     */
    public static List<String> removeSame(List<String> list){
        List<String> result=new ArrayList<>();
        //使用一个set集合找重复值
        Set<String> set=new HashSet<>();
        for(String each:list){
            if(!set.contains(each)){
                result.add(each);
                set.add(each);
            }
        }
        return result;
    }

    /**
     * 编译至文件
     * @param pythonPage pythonPage结构
     * @param path 路径
     * @throws Exception 抛出异常，包括：文件读取异常
     */
    public static void compileToFile(PythonPage pythonPage, String path) throws Exception {
        List<String> list = compile(pythonPage);
        path = path + File.separator + pythonPage.getPageName();
        FileUtil.writeLines(list, path);
    }

    /**
     * 生成导入包的python代码
     * @param packageName 包名 e.g. tensorflow
     * @return e.g. import tensorflow
     */
    public static String generateImport(String packageName) {
        return String.format(Compiler.IMPORT_TEMPLATE, packageName);
    }

    /**
     * 在编译参数部分时，要考虑参数类型，如字符串，数字，布尔，数组等
     * 如：function("hello") #字符串
     *     function(1)      #数字
     *     function(1.2)    #小数
     *     function(True)   #布尔
     *     function(["hello","world"]) #字符串数组
     *     function([1,2])  #数字数组
     *     function([1.1,2,2]) #数字数组
     *
     * @param value 值
     * @return 字符串
     */
    public static String generateParam(Object value) {

        //数组,[1,2,3]
        if (value instanceof List) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            List<Object> list = (List) value;
            //递归生成
            stringBuilder.append(generateParam(list.get(0)));
            for (int i = 1; i < list.size(); i++) {
                stringBuilder.append(Compiler.PYTHON_COMMA);
                stringBuilder.append(generateParam(list.get(i)));
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            //字符串加双引号
            if (value instanceof String) {
                //判断是不是变量还是真字符串
                if (((String) value).contains(PythonPage.VAR_LABEL)) {
                    //如果包含“var:”部分则为变量
                    String variable=((String) value).split(PythonPage.VAR_LABEL)[1];
                    stringBuilder.append(variable);
                } else {
                    stringBuilder.append(Compiler.PYTHON_QUOT);
                    stringBuilder.append(value);
                    stringBuilder.append(Compiler.PYTHON_QUOT);
                }
            } else if (value instanceof Boolean) {
                //布尔转成python的布尔类型
                if ((Boolean) value) {
                    stringBuilder.append(Compiler.PYTHON_TRUE);
                } else {
                    stringBuilder.append(Compiler.PYTHON_FALSE);
                }
            } else {
                stringBuilder.append(value);
            }
            return stringBuilder.toString();
        }
    }

    /**
     * 创建赋值过程 a,b=c
     * @param rightVariable 右边
     * @param leftVariableList 左边
     * @return
     */
    public static String generateVariableLine(String rightVariable, String[] leftVariableList){

        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(leftVariableList[0]);
        for(int i=1;i<leftVariableList.length;i++){
            stringBuilder.append(",");
            stringBuilder.append(leftVariableList[i]);
        }
        stringBuilder.append(Compiler.PYTHON_EQUAL);
        stringBuilder.append(rightVariable);
        return stringBuilder.toString();
    }
    /**
     * e.g. a1,a2=tf.a.b.c(a="1",b=2,c=True,d=False,e=0.5,f=[1,2])
     *
     * @param functionName 函数名
     * @param nonParams    不带名称的参数
     * @param params       参数
     * @param returnName   返回值
     * @return 字符串
     */
    public static String generateInvokeLine(String functionName, List<Object> nonParams, Map<String, Object> params, String[] returnName) {

        String paramsString = null;

        //不带名称的参数
        if (nonParams != null) {
            if (nonParams.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Compiler.generateParam(nonParams.get(0)));
                for (int i = 1; i < nonParams.size(); i++) {
                    stringBuilder.append(",");
                    stringBuilder.append(Compiler.generateParam(nonParams.get(i)));
                }
                paramsString = stringBuilder.toString();
            }
        }


        //带名称的参数
        if (params != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, Object> e : params.entrySet()) {
                stringBuilder.append(e.getKey());
                stringBuilder.append(Compiler.PYTHON_EQUAL);
                stringBuilder.append(Compiler.generateParam(e.getValue()));
                stringBuilder.append(Compiler.PYTHON_COMMA);
            }


            String temp = stringBuilder.toString();

            //两个参数都有的化中间加逗号
            if (paramsString != null && !temp.equals("")) {
                paramsString = paramsString + Compiler.PYTHON_COMMA + temp;
            } else {
                paramsString = temp;
            }

            //去掉最后一个逗号
            if (paramsString.endsWith(",")) {
                paramsString = paramsString.substring(0, paramsString.length() - 1);
            }
        }
        if(paramsString==null){
            paramsString="";
        }
        String line = String.format(Compiler.INVOKE_TEMPLATE, functionName, paramsString);
        if (returnName != null) {
            if (returnName.length > 0) {
                //添加返回值
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(returnName[0]);
                for (int i = 1; i < returnName.length; i++) {
                    stringBuilder.append(Compiler.PYTHON_COMMA);
                    stringBuilder.append(returnName[i]);
                }
                return String.format(Compiler.INVOKE_TEMPLATE_RETURN, stringBuilder.toString(), functionName, paramsString);
            }
        }
        return line;
    }

    /**
     * e.g. def function(a,b,c)
     *
     * @param function function name
     * @param args     arg array
     * @return string
     */
    public static String generateFunctionLine(String function, List<String> args) {

        String params = "";
        if (args != null) {
            if (args.size() > 0) {
                StringBuilder argsBuilder = new StringBuilder();
                argsBuilder.append(args.get(0));
                for (int i = 1; i < args.size(); i++) {
                    argsBuilder.append(Compiler.PYTHON_COMMA);
                    argsBuilder.append(args.get(i));
                }
                params = argsBuilder.toString();
            }
        }
        return String.format(Compiler.FUNCTION_TEMPLATE, function, params);
    }

    /**
     * e.g. return a,b,c
     *
     * @param returnList 返回值
     * @return 字符串
     */
    public static String generateReturnLine(List<String> returnList) {

        StringBuilder stringBuilder = new StringBuilder();

        if (returnList != null) {
            if (returnList.size() > 0) {
                stringBuilder.append(returnList.get(0));
                for (int i = 1; i < returnList.size(); i++) {
                    stringBuilder.append(Compiler.PYTHON_COMMA);
                    stringBuilder.append(returnList.get(i));
                }
                return String.format(Compiler.RETURN_TEMPLATE, stringBuilder.toString());
            }
        }
        return "";

    }

}
