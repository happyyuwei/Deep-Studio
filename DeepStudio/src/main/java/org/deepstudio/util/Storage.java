package org.deepstudio.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 存储实例，单例模式
 */
public class Storage {

    //单例模式
    private static class StorageHolder {
        private static Storage storage = new Storage();
    }

    /**
     * 获取实例
     *
     * @return 存储
     */
    public static Storage getInstance() {
        return StorageHolder.storage;
    }

    //日志实例
    private static final Logger logger = LoggerFactory.getLogger(Storage.class);


    /**
     * 私有构造函数
     */
    private Storage(){

    }


    /**
     *
     * @param sessionId
     * @param workspaceName
     * @param workspacePath
     * @param appName
     */
    public void saveSession(String sessionId, String workspaceName, String workspacePath, String appName){
        //读取缓存




    }



}
