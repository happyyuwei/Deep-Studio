package org.deepstudio;


import com.alibaba.fastjson.JSON;
import org.deepstudio.compile.Compiler;
import org.deepstudio.compile.PythonPage;
import org.deepstudio.compile.StructureParser;
import org.deepstudio.structure.DeepStructure;
import org.deepstudio.ui.FloatingWindow;
import org.deepstudio.ui.Window;
import org.deepstudio.util.FileUtil;

import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception{


//        DeepStructure d=new DeepStructure(FileUtil.readToString("residual.json"));
//        List<Object> l= (List<Object>) d.getLayers().get("inputs").getArg("shape");
//        System.out.println(l.get(0).getClass());
//        System.out.println(Compiler.generateFunctionLine("make_DNN_model", new String[]{"a","b"}));
//        Map<String,Object> m=new HashMap<>();
//        m.put("a","1");
//        m.put("b",2);
//        m.put("c", true);
//        m.put("d",false);
//        m.put("e",0.5);
//        List<Double> list1=new ArrayList<>();
//        list1.add(2.3);
//        list1.add(7.1);
//        List<Integer> list2=new ArrayList<>();
//        list2.add(1);
//        list2.add(2);
//        List<String> list3=new ArrayList<>();
//        list3.add("1");
//        list3.add("2");
//        m.put("e",list1);
//        m.put("f",list2);
//        m.put("g",list3);
//
//        System.out.println(Compiler.generateInvokeLine("tf.a.b.c",m,new String[]{"a1","a2"}));
//        System.out.println(Compiler.generateReturnLine(new String[]{"a","v","c"}));
//        Object a=new int[]{1,2,3,4};
//        System.out.println(a instanceof int[]);
//
//
//        PythonPage p =StructureParser.parseStructure(d);
//        Compiler.compileToFile(p,".");

//        FloatingWindow f=new FloatingWindow();
//
//        f.setVisible(true);
//        Bootstrap b=new Bootstrap();
//        Explorer.getInstance().createWorkspace("first","C:\\Users\\happy\\Desktop");
        Bootstrap.start();

    }


}
