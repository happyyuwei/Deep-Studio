package org.deepstudio;


import org.deepstudio.bean.Workspace;

import java.util.List;

/**
 * 缓存类，保存所有该程序的缓存信息，供下次打开时使用
 */
public class Cache {

    //上一次打开的工作区，下一次将重新打开该工作区
    //若没有可用工作区，则设为null
    private String lastWorkspace;

    private List<Workspace> recentWorkspace;

    private int processNum;

    public String getLastWorkspace() {
        return lastWorkspace;
    }

    //
    public void setLastWorkspace(String lastWorkspace) {
        this.lastWorkspace = lastWorkspace;
    }

    public List<Workspace> getRecentWorkspace() {
        return recentWorkspace;
    }

    public void setRecentWorkspace(List<Workspace> recentWorkspace) {
        this.recentWorkspace = recentWorkspace;
    }

    public int getProcessNum() {
        return processNum;
    }

    public void setProcessNum(int processNum) {
        this.processNum = processNum;
    }
}
