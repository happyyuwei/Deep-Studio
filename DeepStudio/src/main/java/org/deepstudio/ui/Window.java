package org.deepstudio.ui;
import org.deepstudio.ui.engine.GraphPanel;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口，继承于javax.swing.JFrame。
 * 该软件主视图样式。
 * 相较于JFrame，该窗口去除了标题栏，改为自定义标题栏，并将菜单栏至于标题栏中。
 * @since 2020.4.5
 * @version yuwei
 */
public class Window extends JFrame{


    public Window(){
        super();
        //获取屏幕尺寸
        Rectangle screenRect=GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        //设置大小
        super.setSize(screenRect.width,screenRect.height);

        //设置关闭模式
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //自定义顶部栏
        super.setLayout(new BorderLayout());


        GraphPanel graphPanel=new GraphPanel();

        JScrollPane scrollPane=new JScrollPane(graphPanel);
        super.add(scrollPane);
        //启动
        super.setVisible(true);
    }

    public static void main(String[] args){
        Window window=new Window();

    }





}
