package org.deepstudio;


import lombok.Data;
import org.deepstudio.bean.Workspace;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存类，保存所有该程序的缓存信息，供下次打开时使用
 */
@Data
public class Cache {

    //上一次打开的工作区，下一次将重新打开该工作区
    //若没有可用工作区，则设为null
    private String lastWorkspace;

    //保存近期工作空间
    private List<Workspace> recentWorkspace;

    //单个进程最多管理十个tensorflow训练进程
    private int processNum;

    /**
     * 构造函数，若此处不创建空列表，则在新建缓存时会出现null指针。
     */
    public Cache(){
        //創建空列表
        recentWorkspace=new ArrayList<>();
        //默認管理格數是10個
        this.processNum=10;

    }

}
