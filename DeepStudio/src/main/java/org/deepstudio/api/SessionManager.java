package org.deepstudio.api;

import org.deepstudio.Explorer;
import org.deepstudio.bean.App;
import org.deepstudio.exception.SessionNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 会话管理。每个窗口会新建一个会话。会话以app为基本单位。
 * 单例模式
 */
public class SessionManager {

    private static class SessionManagerHolder{
        private static SessionManager sessionManager=new SessionManager();
    }

    public static SessionManager getInstance(){
        return SessionManagerHolder.sessionManager;
    }

    //会话列表
    final private Map<String, App> workSessionMap;
    //日志实例
    private static final Logger logger = LoggerFactory.getLogger(Explorer.class);

    /**
     * 私有构造函数
     */
    private SessionManager(){
        this.workSessionMap=new HashMap<>();
    }


    /**
     * 创建会话
     * @param workspaceName
     * @param workspacePath
     * @return
     */
    public String createSession(String workspaceName, String workspacePath){
        //session为随机long数
        Random random = new Random();
        String sessionId=String.valueOf(random.nextLong());
        //保存
        //创建App实例
        App app=new App();
        //初次创建还没选定app，将其设为null
        app.setAppName(null);
        app.setWorkspace(workspaceName);
        app.setWorkspacePath(workspacePath);
        this.workSessionMap.put(sessionId, app);

        logger.info("Session created. session="+this.workSessionMap);

        return sessionId;
    }

    /**
     * 查询会话
     * @param sessionId
     * @return
     */
    public App checkSession(String sessionId) throws Exception{
        App app= this.workSessionMap.get(sessionId);
        if(app!=null){
            return app;
        }else{
            throw new SessionNotExistException();
        }
    }

    /**
     * 更新
     * @param sessionId
     * @param appName
     */
    public void updateAppName(String sessionId, String appName){
        App app=this.workSessionMap.get(sessionId);
        if(app!=null){
            app.setAppName(appName);
            logger.info("Session updated. session="+this.workSessionMap);
        }
    }

}
