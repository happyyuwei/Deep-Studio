package org.deepstudio.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    /**
     * 写入行
     * @param lines 行列表
     * @param path 路径
     * @throws Exception 文件异常
     */
    public static void writeLines(List<String> lines, String path) throws Exception{

        PrintStream out=new PrintStream(new FileOutputStream(path));
        for(String line:lines){
            out.println(line);
        }
        out.close();
    }

    /**
     * 写入字符串
     * @param content 内容
     * @param path 路径
     * @throws Exception 异常
     */
    public static void writeString(String content, String path) throws Exception{
        PrintStream out=new PrintStream(new FileOutputStream(path));
        out.println(content);
        out.close();
    }

    /**
     * 按行读取文件
     * @param file 文件名
     * @return 行列表
     * @throws Exception 异常
     */
    public static List<String> readLines(String file) throws Exception {
        BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        String line="";

        List<String> list=new ArrayList<>();

        while((line=in.readLine())!=null){
            list.add(line);
        }
        in.close();
        return list;
    }

    /**
     * 将文件读成字符串
     * @param file 文件
     * @return 字符串
     * @throws Exception 异常
     */
    public static String readToString(String file) throws Exception{
        List<String> list=FileUtil.readLines(file);

        StringBuilder stringBuilder=new StringBuilder();
        for(String line: list){
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

}
