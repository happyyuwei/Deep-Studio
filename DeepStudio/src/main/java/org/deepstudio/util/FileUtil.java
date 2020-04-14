package org.deepstudio.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static void writeLines(List<String> lines, String path) throws Exception{

        PrintStream out=new PrintStream(new FileOutputStream(path));
        for(String line:lines){
            out.println(line);
        }
        out.close();
    }

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

    public static String readToString(String file) throws Exception{
        List<String> list=FileUtil.readLines(file);

        StringBuilder stringBuilder=new StringBuilder();
        for(String line: list){
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

}
