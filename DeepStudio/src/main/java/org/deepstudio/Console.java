package org.deepstudio;

import org.deepstudio.exception.TrainingProcessAlreadyStartException;
import org.deepstudio.exception.TrainingProcessNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 负责与后端tensorflow环境交互交互
 */
public class Console {

    //日志实例
    private static final Logger logger = LoggerFactory.getLogger(Explorer.class);

    //单例模式
    private static class ConsoleHolder {
        private static Console console = new Console();
    }

    /**
     * 获取实例
     * @return Console实例
     */
    public static Console getInstance() {
        return ConsoleHolder.console;
    }

    //线程池
    private ExecutorService executorService;
    //运行实例
    private Map<String, TrainingProcess> trainingProcessList;

    /**
     * 构造函数
     */
    private Console(){
        //获取最多管理进程数量
        int processNum=Explorer.getInstance().getCache().getProcessNum();
        //创建固定数量的线程池
        this.executorService= Executors.newFixedThreadPool(processNum);
        //线程列表，支持同步
        this.trainingProcessList= Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * 启动训练进程
     * @param workspace 工作空间
     * @param path 工作路径
     * @param app 训练应用
     */
    public void startTrainProcess(String workspace, String path, String app) throws Exception{

        String appPath=Explorer.getAppPath(workspace,path,app);
        TrainingProcess trainingProcess=this.trainingProcessList.get(appPath);

        //只有当该进程没有启动时才允许启动
        if(trainingProcess==null) {

            trainingProcess = new TrainingProcess(workspace, app, path);
            //执行训练进程
            this.executorService.submit(trainingProcess);
            //注册进程
            this.trainingProcessList.put(appPath, trainingProcess);
            logger.info("Training process start. appPath="+appPath+", currentProcess="+this.trainingProcessList);
        }else{
            logger.info("Training process already started. appPath="+appPath+", currentProcess="+this.trainingProcessList);
            throw new TrainingProcessAlreadyStartException();
        }
    }

    /**
     * 销毁训练进程
     * @param workspace
     * @param path
     * @param app
     */
    public void stopTrainingProcess(String workspace, String path, String app) throws Exception{
        String appPath=Explorer.getAppPath(workspace,path,app);
        //获取进程
        TrainingProcess trainingProcess=this.trainingProcessList.get(appPath);
        if(trainingProcess!=null){
            //销毁进程
            trainingProcess.destroy();
            //移除注册
            this.trainingProcessList.remove(appPath);
            logger.info("Training process destroy. appPath="+appPath+", currentProcess="+this.trainingProcessList);
        }else{
            logger.info("No training process found. appPath="+appPath+", currentProcess="+this.trainingProcessList);
            throw new TrainingProcessNotFoundException();
        }
    }


    /**
     * 查看训练过程是否启动
     * @param workspace
     * @param path
     * @param app
     * @return
     */
    public boolean isTrainingProcessRunning(String workspace, String path, String app){
        String appPath=Explorer.getAppPath(workspace,path,app);
        return this.trainingProcessList.containsKey(appPath);
    }




}
