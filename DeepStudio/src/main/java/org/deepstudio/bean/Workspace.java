package org.deepstudio.bean;


import lombok.Data;

/**
 * 工作区bean
 */
@Data
public class Workspace {
    //工程名称
    private String name;
    //路径，路径不带最后该工程的名字
    private String path;


}
