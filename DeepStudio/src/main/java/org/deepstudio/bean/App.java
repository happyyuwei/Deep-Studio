package org.deepstudio.bean;

public class App {

    private String workspace;

    private String workspacePath;

    private String appName;

    private boolean isRunning;

    private int currentEpoch;

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public void setWorkspacePath(String workspacePath) {
        this.workspacePath = workspacePath;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public int getCurrentEpoch() {
        return currentEpoch;
    }

    public void setCurrentEpoch(int currentEpoch) {
        this.currentEpoch = currentEpoch;
    }

    @Override
    public String toString() {
        return "App{" +
                "workspace='" + workspace + '\'' +
                ", workspacePath='" + workspacePath + '\'' +
                ", appName='" + appName + '\'' +
                ", isRunning=" + isRunning +
                ", currentEpoch=" + currentEpoch +
                '}';
    }
}
