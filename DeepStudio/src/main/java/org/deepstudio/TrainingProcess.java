package org.deepstudio;

import org.deepstudio.util.ThreadLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 训练进程
 */
public class TrainingProcess implements Runnable {

    //工作区
    private String workspace;
    //应用
    private String app;
    //路径
    private String path;
    //app 路径
    private String appPath;
    //进程实例
    private Process process;

    //日志实例
    private static final Logger logger = LoggerFactory.getLogger(TrainingProcess.class);

    /**
     * @param workspace 工作空间
     * @param app       应用
     * @param path      目录
     */
    public TrainingProcess(String workspace, String app, String path) {
        this.workspace = workspace;
        this.app = app;
        this.path = path;
        //创建工作目录，工作目录为app工程
        this.appPath = Explorer.getAppPath(workspace, path, app);
    }

    /**
     * 关闭进程
     */
    public void destroy(){
        this.process.destroy();
    }

    @Override
    public void run() {

        //创建进程日志，该日志需要与运行日志分开,最后这个反斜杠一定要加，不然不行
        org.apache.log4j.Logger trainingLogger= ThreadLogger.getLogger("runtime", this.appPath+"\\");

        //设置参数
        List<String> command=new ArrayList<>();
        command.add("python");
        command.add("./main.py");
        command.add("--env=\""+this.appPath+"\"");
        command.add("--config=config.json");
        command.add("--script=eidolon.train.py");

        logger.info("Start training process. command="+String.valueOf(command));

        //创建进程
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        //重定向错误
        processBuilder.redirectErrorStream(true);
        try {
            //启动进程
            this.process = processBuilder.start();
            //获取输出流
            BufferedReader br = new BufferedReader(new InputStreamReader(this.process.getInputStream(), "GBK"));
            String line;
            //监控输出流
            while ((line = br.readLine()) != null) {
                trainingLogger.info(line);
            }
        }catch (Exception exc){
            exc.printStackTrace();
        }
    }

}
