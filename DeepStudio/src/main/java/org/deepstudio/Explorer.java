package org.deepstudio;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.deepstudio.bean.*;
import org.deepstudio.exception.DirectionaryExistException;
import org.deepstudio.exception.WorkspaceNotExistException;
import org.deepstudio.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.util.*;

/**
 * 资源管理器，
 * 核心类，上层接口均使用该类中的核心方法
 */

public class Explorer {

    //单例模式
    private static class ExplorerHolder {
        private static Explorer explorer = new Explorer();
    }

    /**
     * 获取实例
     *
     * @return 资源管理器
     */
    public static Explorer getInstance() {
        return ExplorerHolder.explorer;
    }

    //日志实例
    private static final Logger logger = LoggerFactory.getLogger(Explorer.class);
    //缓存
    private Cache cache;

    //缓存位置
    public static String cachePath = "cache.json";
    //后端eidolon目录，直接在根目录内
    public static String eidolonPath = "./eidolon";
    //app目录
    public static String appDir = "app";
    //数据集目录
    public static String dataDir = "data";
    //代码目录
    public static String srcDir = "src";

    //日志目录
    public static String logDir = "log";

    //训练曲线文件
    public static String trainLog = "train_log.txt";

    public static String resultImageDir = "result_image";
    //配置文件
    public static String appConfig = "config.json";

    //可视化标签文件
    public static String visualLabelFile = "label.txt";

    //运行时日志
    public static String runtimeLog = "runtime.log";

    //日志曲线字段分隔符
    public static String logSeparator = ",";

    //日志每个字段的连接符
    public static String logConnector = "=";

    //训练损失标签
    public static String logTrainLossTag = "train: ";
    //测试损失标签
    public static String logTestLossTag = "test: ";

    //创建app训练配置文件指令模板，需要指定创建路径与运行时
    public static String createConfigCmdTemple="python ./eidolon/config.py -d %s -r %s";


    /**
     * 构造函数
     */
    private Explorer() {
        try {
            //解析缓存
            try {
                //当存在缓存文件，则读取缓存
                String cacheJson = FileUtil.readToString(cachePath);
                this.cache = JSON.parseObject(cacheJson, Cache.class);
            }catch (Exception exc){
                //若不存在缓存，则启动新缓存。此语句用于第一次启动程序时使用。
                this.cache=new Cache();
            }
            //如果第一次打开，则不需要
            if (this.cache.getLastWorkspace() != null) {
                //检查工作区路径是否存在
                File workspace = new File(this.cache.getLastWorkspace());
                //如果工作区不存在，则设置成null
                if (!workspace.exists()) {
                    this.cache.setLastWorkspace(null);
                    logger.info("Workspace not found. path=" + this.cache.getLastWorkspace());
                }
            } else {
                logger.info("No workspace found, start new panel.");
            }
            //更新缓存
            this.updateRecentWorkspace();
            //保存缓存
            String jsonString = JSON.toJSONString(this.cache);
            FileUtil.writeString(jsonString, cachePath);
            logger.info("Cache freshed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建工作区
     *
     * @param name 工作区名字
     * @param path 工作区路径
     */
    public void createWorkspace(String name, String path) throws Exception {
        //保存原始路径
        String pathOrig = path;
        //合并路径与文件名
        path = path + File.separator + name;
        //检查文件夹是否存在
        File workspace = new File(path);
        //如果文件夹以存在，则创建失败
        if (workspace.exists()) {
            throw new DirectionaryExistException();
        }
        //创建文件夹
        workspace.mkdir();
        //创建工作区子文件夹
        //创建app文件夹
        File appDir = new File(path + File.separator + Explorer.appDir);
        appDir.mkdir();
        //创建数据集文件夹
        File dataDir = new File(path + File.separator + Explorer.dataDir);
        dataDir.mkdir();
        File srcDir = new File(path + File.separator + Explorer.srcDir);
        srcDir.mkdir();

        //创建workspace bean
        Workspace w = new Workspace();
        w.setPath(pathOrig);
        w.setName(name);
        //更新缓存
        this.cache.getRecentWorkspace().add(w);
        String jsonString = JSON.toJSONString(this.cache);
        FileUtil.writeString(jsonString, cachePath);

        System.out.println(this.cache);

        logger.info("Workspace created. path=" + path);
    }

    /**
     * 检查近期工作区，如果某个工作区以删除，则对其删除。
     * 每次启动程序是运行
     */
    public void updateRecentWorkspace() {
        List<Workspace> result = new ArrayList<>();
        //从缓存中获取近期工程
        List<Workspace> workspaceList = this.cache.getRecentWorkspace();
        //挨个检查，是否该目录真实存在
        for (Workspace workspace : workspaceList) {
            String pathName = workspace.getPath() + File.separator + workspace.getName();
            File file = new File(pathName);
            if (file.exists()) {
                result.add(workspace);
            } else {
                logger.info("Workspace not fount. path=" + pathName);
            }
        }
        //重置缓存
        this.cache.setRecentWorkspace(result);
    }

    /**
     * 获取近期工程
     *
     * @return 工程列表
     */
    public List<Workspace> getRecentWorkspace() {
        //直接获取缓存
        return this.cache.getRecentWorkspace();
    }

    /**
     * 获取缓存
     *
     * @return 缓存
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * 获取所有App
     *
     * @param workspace 工作空间
     * @return App列表
     */
    public List<App> getAllApps(String workspace, String path) {

        //app 目录
        File appDir = new File(path + File.separator + workspace + File.separator + this.appDir);

        String[] appArray = appDir.list();

        List<App> result = new ArrayList<>();
        if (appArray != null) {
            for (String each : appArray) {
                File file = new File(Explorer.getAppPath(workspace, path, each));
                if (file.isDirectory()) {
                    App app = new App();
                    app.setAppName(each);
                    app.setWorkspace(workspace);
                    app.setWorkspacePath(path);
                    result.add(app);
                }
            }
        }
        return result;
    }

    public boolean isWorkspaceExist(String workspace, String path) {
        File file = new File(path + File.separator + workspace);
        return file.exists();
    }

    /**
     * 解析训练日志曲线
     *
     * @param workspace
     * @param path
     * @param appName
     * @return
     * @throws Exception
     */
    public CurveData parseLogCurve(String workspace, String path, String appName) throws Exception {

        //生成文件路径
        String logFile = path + File.separator + workspace + File.separator + Explorer.appDir + File.separator + appName + File.separator + this.logDir + File.separator + this.trainLog;

        //读取文件
        List<String> lines = FileUtil.readLines(logFile);

        //数据
        Map<String, List<Float>> map = new HashMap<>();
        //轮数
        List<Integer> epoch = new ArrayList<>();
        //日期
        List<String> date = new ArrayList<>();

        for (String line : lines) {
            //解析字段
            String[] fields = line.split(logSeparator);
            for (int i = 0; i < fields.length; i++) {
                //Todo 尚未想好日期字段如何处理,目前是字符串
                //解析每个字段的key和value
                String[] values = fields[i].split(logConnector);
                if (i > 1) {
                    List<Float> curveList = map.get(values[0]);
                    if (curveList == null) {
                        curveList = new ArrayList<>();
                        curveList.add(Float.parseFloat(values[1]));
                        map.put(values[0], curveList);
                    } else {
                        curveList.add(Float.parseFloat(values[1]));
                    }
                } else if (i == 1) {
                    //日期
                    date.add(values[1]);
                } else {
                    //第一个是轮数
                    epoch.add(Integer.parseInt(values[1]));
                }
            }
        }
        //解析，将损失与评估指标分开
        //先遍历一遍找到所有评估损失
        List<EvalData> evalDataList = new ArrayList<>();
        for (Map.Entry<String, List<Float>> e : map.entrySet()) {
            String curveName = e.getKey();
            List<Float> data = e.getValue();
            //如果不包含上述标志，则说明是评估
            if (!curveName.contains(Explorer.logTrainLossTag) && !curveName.contains(Explorer.logTestLossTag)) {
                EvalData evalData = new EvalData();
                evalData.setEvalName(curveName);
                evalData.setData(data);
                evalDataList.add(evalData);
            }
        }
        //再遍历一遍找到所有训练损失
        List<LossData> lossDataList = new ArrayList<>();
        for (Map.Entry<String, List<Float>> e : map.entrySet()) {
            String curveName = e.getKey();
            List<Float> data = e.getValue();
            //如果包含训练标志，则说明是训练
            if (curveName.contains(Explorer.logTrainLossTag)) {
                LossData lossData = new LossData();
                //分离标志
                String lossName = curveName.replace(Explorer.logTrainLossTag, "");
                lossData.setLossName(lossName);
                lossData.setTrainLoss(data);
                //寻找对应的测试曲线
                String testLossName = Explorer.logTestLossTag + lossName;
                lossData.setTestLoss(map.get(testLossName));
                lossDataList.add(lossData);
            }
        }

        //综合
        CurveData curveData = new CurveData();
        curveData.setEpoch(epoch);
        curveData.setTime(date);
        curveData.setEvalData(evalDataList);
        curveData.setLossData(lossDataList);

        return curveData;
    }


    /**
     * 获取可视化结果数据
     *
     * @param workspace
     * @param path
     * @param appName
     * @return
     */
    public List<VisualResultInfo> getVisualResultInfo(String workspace, String path, String appName, int epochNum) throws Exception {
        //生成可视化日志路径
        String visualDir = path + File.separator + workspace + File.separator + Explorer.appDir + File.separator + appName + File.separator + this.logDir + File.separator + Explorer.resultImageDir;
        File visualDirFile = new File(visualDir);

        //读取所有图片文件名
        String[] list = visualDirFile.list();
        List<String> imageList = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            if (list[i].endsWith(".png")) {
                imageList.add(list[i]);
            }
        }
        //每轮最大的数量
        int maxNum = 0;
        //若epoch<0。则读取最大轮数
        if (epochNum < 0) {
            //遍历寻找最大的数字
            for (int i = 0; i < imageList.size(); i++) {
                //去除后缀，分割
                String[] values = imageList.get(i).replace(".png", "").split("_");
                //获取轮数
                int epoch = Integer.parseInt(values[0]);
                if (epochNum < epoch) {
                    epochNum = epoch;
                }
                //获取每轮数量
                int num = Integer.parseInt(values[2]);
                if (maxNum < num) {
                    maxNum = num;
                }
            }
        }

        //保存当前轮数图像
        List<List<String>> images = new ArrayList<>();
        //创建空列表
        for (int i = 0; i < maxNum; i++) {
            images.add(new ArrayList<>());
        }
        for (int i = 0; i < imageList.size(); i++) {
            //去除后缀，分割
            String[] values = imageList.get(i).replace(".png", "").split("_");
            //获取轮数
            int epoch = Integer.parseInt(values[0]);
            //只读取当前轮数的
            if (epochNum == epoch) {
                int num = Integer.parseInt(values[2]);
                images.get(num - 1).add(imageList.get(i));
            }
        }

        //读取标签文件
        String labelPath = visualDir + File.separator + Explorer.visualLabelFile;
        File visualLabelFile = new File(labelPath);
        //标签集
        JSONObject labelObj = null;
        //标签
        if (visualLabelFile.exists()) {
            String json = FileUtil.readToString(labelPath);
            labelObj = JSON.parseObject(json);
        }

        //整理结果
        List<VisualResultInfo> visualResultInfos = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            VisualResultInfo visualResultInfo = new VisualResultInfo();
            visualResultInfo.setImageList(images.get(i));
            //如果标签存在
            if (labelObj != null) {
                //标签，命名 {epoch}_{num}
                JSONObject obj = labelObj.getJSONObject(epochNum + "_" + i);
                visualResultInfo.setLabels(obj);
            }
            visualResultInfos.add(visualResultInfo);
        }

        return visualResultInfos;
    }

    /**
     * 读取结果图片
     * @param imageName
     * @param workspace
     * @param path
     * @param appName
     * @return
     * @throws Exception
     */
    public BufferedImage getResultImage(String imageName, String workspace, String path, String appName) throws Exception{
        String visualDir = path + File.separator + workspace + File.separator + Explorer.appDir + File.separator + appName + File.separator + Explorer.logDir + File.separator + Explorer.resultImageDir;
        String imagePath=visualDir+File.separator+imageName;
        logger.info("Start to load image. path="+imagePath);

        return ImageIO.read(new File(imagePath));
    }

    /**
     * 获取分类标签
     * @param workspace
     * @param path
     * @param appName
     * @return
     * @throws Exception
     */
    public List<String> getClassifyLabels(String workspace, String path, String appName) throws Exception{
        JSONObject configJSON=this.getAppConfigJSON(workspace,path,appName);
        //读取
        JSONArray labels=configJSON.getJSONObject("classification").getJSONObject("labels").getJSONArray("value");

        //转化成list
        return labels.toJavaList(String.class);
    }


    /**
     * 创建App
     *
     * @param workspace 指定工作区
     * @param path      路径
     * @param appName   应用名
     */
    public void createApp(String workspace, String path, String appName) throws Exception {

        File workDir = new File(path + File.separator + workspace);
        //如果工程不存在，则不创建新应用
        if (!workDir.exists()) {
            throw new WorkspaceNotExistException();
        }
        String appPath=path + File.separator + workspace + File.separator + Explorer.appDir + File.separator + appName;
        File appDir = new File(appPath);
        //如果应用已存在，则不创建新应用
        if (appDir.exists()) {
            throw new DirectionaryExistException();
        }

        //创建目录
        appDir.mkdir();

        //生成运行时路径
        String runtimePath=path + File.separator + workspace + File.separator+ Explorer.srcDir;

        //新建配置文件
        Runtime.getRuntime().exec(String.format(Explorer.createConfigCmdTemple, appPath, runtimePath));

        logger.info("App created. app=" + appName);
    }

    /**
     * 获取APP 运行时日志，目前全部获取
     *
     * @param workspace
     * @param path
     * @param appName
     * @return
     */
    public List<String> getAppRuntimeLog(String workspace, String path, String appName) throws Exception {
        String appPath = Explorer.getAppPath(workspace, path, appName);
        String runtimeLog = appPath + File.separator + Explorer.runtimeLog;
        logger.info("load runtime log. app=" + appName + ", workspace=" + workspace);
        return FileUtil.readLines(runtimeLog);
    }

    /**
     * 获取某个文件夹的所有子文件夹
     *
     * @param path 文件夹路径
     * @return 子文件夹列表
     */
    public List<String> getSubDirectionary(String path) {
        //结果列表
        List<String> list = new ArrayList<>();
        //根目录
        File dir = new File(path);
        //获取子文件
        String[] files = dir.list();
        if (files != null) {
            for (String file : files) {
                String subPath = path + File.separator + file;
                File sub = new File(subPath);
                //检查是否是目录
                if (sub.isDirectory()) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    /**
     * 获取配置文件
     *
     * @param workspace
     * @param path
     * @param appName
     * @return
     * @throws Exception
     */
    public JSONObject getAppConfigJSON(String workspace, String path, String appName) throws Exception {
        String appPath = Explorer.getAppPath(workspace, path, appName);
        String configPath = appPath + File.separator + Explorer.appConfig;
        //读取json
        String json = FileUtil.readToString(configPath);
        logger.info("load config. app=" + appName + ", workspace=" + workspace);
//        //解析json
        JSONObject jsonObject = JSON.parseObject(json);

        return jsonObject;
    }

    /**
     * 保存配置文件
     *
     * @param config
     * @param workspace
     * @param path
     * @param appName
     * @throws Exception
     */
    public void saveAppConfigJSON(Object config, String workspace, String path, String appName) throws Exception {
        String appPath = Explorer.getAppPath(workspace, path, appName);
        String configPath = appPath + File.separator + Explorer.appConfig;
        //转字符串
        String json = JSON.toJSONString(config);
        //读取json
        FileUtil.writeString(json, configPath);
        logger.info("save config. app=" + appName + ", workspace=" + workspace);
    }

    /**
     * @param workspace 工作空间
     * @param path      工程路径
     * @param app       应用名
     * @return 应用路径
     */
    public static String getAppPath(String workspace, String path, String app) {
        //去除path最后的斜杠
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }
        return path + File.separator + workspace + File.separator + Explorer.appDir + File.separator + app;
    }


}
