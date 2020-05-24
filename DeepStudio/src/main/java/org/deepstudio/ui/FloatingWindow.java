package org.deepstudio.ui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 悬浮窗。当在训练关闭主窗口时，将会启用该悬浮窗口。
 */
public class FloatingWindow extends JFrame {

    //拖拽开始鼠标位置
    private static int mouseX=0;
    private static int mouseY=0;
    //启动拖拽
    private static boolean dragEnable=false;

    //logo路径
    final private String logoPath="./drawable/logo.png";

    //背景图片路径
    final private String backgroundPath="./drawable/floating-background-full.png";


    /**
     * 构造函数
     */
    public FloatingWindow() throws Exception{
        super();
        //计算悬浮窗初始位置
        int initX=(int)(Toolkit.getDefaultToolkit().getScreenSize().width*0.9);
        int initY=(int)(Toolkit.getDefaultToolkit().getScreenSize().height*0.1);

        super.setBounds(initX,initY, 80, 80);
        //设置logo
        super.setIconImage(Toolkit.getDefaultToolkit().getImage(this.logoPath));
        //设置关闭方式
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //去掉标题栏
        super.setUndecorated(true);
        //设置主题
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        //设置背景图层
//        JLabel background = new JLabel();
        JLabel background = new JLabel() {
            protected void paintComponent(Graphics g) {
                ImageIcon icon = new ImageIcon(backgroundPath);
                g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(),
                        icon.getImageObserver());
            }
        };

        //添加背景图层
        super.add(background);
        //将背景透明
        super.setBackground(new Color(0, 0, 0, 0));

        //右键弹出菜单
        JPopupMenu popupMenu=new JPopupMenu();
        JMenuItem topMenu= new JMenuItem("置于顶层");
        JMenuItem openMenu= new JMenuItem("打开工作区");
        JMenuItem exitMenu= new JMenuItem("退出");
        popupMenu.add(topMenu);
        popupMenu.add(openMenu);
        popupMenu.add(exitMenu);

        //退出程序
        exitMenu.addActionListener(actionEvent -> System.exit(0));
        //置于顶层
        topMenu.addActionListener(actionEvent -> {
            if(topMenu.getText().equals("置于顶层")) {
                FloatingWindow.super.setAlwaysOnTop(true);
                topMenu.setText("取消置顶");
            }else{
                FloatingWindow.super.setAlwaysOnTop(false);
                topMenu.setText("置于顶层");
            }
        });

        //拖拽事件
        super.addMouseListener(new MouseAdapter() {
            @Override
            //按下开始拖拽
            public void mousePressed(MouseEvent e) {
                //左键才能拖动
                if(e.getButton()==MouseEvent.BUTTON1) {
                    mouseX = e.getX();
                    mouseY = e.getY();
                    dragEnable=true;
                }
            }
            //停止拖拽
            public void mouseReleased(MouseEvent e){
                //左键才能拖动
                if(e.getButton()==MouseEvent.BUTTON1) {
                    dragEnable=false;
                }
            }
            //右键弹出菜单
            public void mouseClicked(MouseEvent e){
                if(e.getButton()==MouseEvent.BUTTON3) {
                    popupMenu.show(e.getComponent(),e.getX(),e.getY());
                }
            }
        });
        //拖拽
        super.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(dragEnable) {
                    FloatingWindow.super.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
                }
            }
        });



    }

}
