package org.deepstudio;

import com.alibaba.fastjson.JSON;
import org.deepstudio.api.SessionManager;
import org.deepstudio.api.WebApi;
import org.deepstudio.ui.FloatingWindow;
import org.deepstudio.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

/**
 * 启动引导类，在工程启动前由引导程序初始化整个程序
 * 完成以下内容。
 * 1. 解析缓存缓存配置
 */
public class Bootstrap {

    //日志实例
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void start() throws Exception{

        //创建资源管理器
        Explorer explorer=Explorer.getInstance();
        logger.info("Explorer started.");

//        System.out.println(explorer.getVisualResultInfo("First","C:\\Users\\happy\\Desktop","mnist",-1));

//        System.out.println(explorer.parseLogCurve("First","C:\\Users\\happy\\Desktop","mnist"));

        //控制台
        Console console=Console.getInstance();
        logger.info("Console started.");

        //会话管理
        SessionManager sessionManager=SessionManager.getInstance();
        logger.info("Session manager started.");

        //创建web接口
        WebApi.run();
        logger.info("Web API started.");

        //懸浮UI啟動
        FloatingWindow floatingWindow=new FloatingWindow();
        floatingWindow.setVisible(true);
        logger.info("Floating UI started.");
    }

}
