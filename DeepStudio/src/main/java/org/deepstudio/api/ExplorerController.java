package org.deepstudio.api;

import org.deepstudio.Console;
import org.deepstudio.Explorer;
import org.deepstudio.bean.*;
import org.deepstudio.exception.WorkspaceNotExistException;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ExplorerController {

    /**
     * 检查版本信息
     * @return 版本信息
     */
    @GetMapping("/version")
    public String checkVersion(){
        return "Deep Studio\n@author yuwei\n@version 0.0.1";
    }

    /**
     * 获取近期所有目录
     * @return 该目录下的所有子目录
     */
    @GetMapping("/workspace/recent")
    public List<Workspace> getRecentWorkspace(){
        return Explorer.getInstance().getRecentWorkspace();
    }

    /**
     * 创建工程
     */
    @PostMapping("/workspace/create")
    public WorkSession createWorkspace(@RequestBody Workspace workspace) throws Exception{
        //创建
        Explorer.getInstance().createWorkspace(workspace.getName(), workspace.getPath());
        //生成会话
        String sessionId=SessionManager.getInstance().createSession(workspace.getName(), workspace.getPath());
        //创建结果
        WorkSession workSession=new WorkSession();
        App app=new App();
        app.setWorkspacePath(workspace.getPath());
        app.setWorkspace(workspace.getName());
        workSession.setSessionId(sessionId);
        workSession.setApp(app);

        return workSession;
    }


    @PostMapping("/workspace/open")
    public WorkSession openWorkspace(@RequestBody Workspace workspace) throws Exception{

        if(Explorer.getInstance().isWorkspaceExist(workspace.getName(), workspace.getPath())) {

            //生成会话
            String sessionId = SessionManager.getInstance().createSession(workspace.getName(), workspace.getPath());
            //创建结果
            WorkSession workSession = new WorkSession();
            App app = new App();
            app.setWorkspacePath(workspace.getPath());
            app.setWorkspace(workspace.getName());
            workSession.setSessionId(sessionId);
            workSession.setApp(app);

            return workSession;
        }else{
            throw new WorkspaceNotExistException();
        }
    }

    @GetMapping("/workspace/info/{sessionId}")
    public WorkSession checkWorkSession(@PathVariable String sessionId) throws Exception{
        App app=SessionManager.getInstance().checkSession(sessionId);
        WorkSession workSession=new WorkSession();
        workSession.setSessionId(sessionId);
        workSession.setApp(app);
        return workSession;
    }


    /**
     *
     * @param sessionId
     * @param app
     * @throws Exception
     */
    @PostMapping("/app/create/{sessionId}")
    public void createApp(@PathVariable String sessionId, @RequestBody App app) throws Exception{
        //查询信息
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        //创建
        Explorer.getInstance().createApp(appInfo.getWorkspace(), appInfo.getWorkspacePath(), app.getAppName());
        //更新会话
        SessionManager.getInstance().updateAppName(sessionId, app.getAppName());
    }

    @PostMapping("/app/open/{sessionId}")
    public void openApp(@PathVariable String sessionId, @RequestBody App app) throws Exception{
        //查询信息
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        //更新会话
        SessionManager.getInstance().updateAppName(sessionId, app.getAppName());
    }


    @GetMapping("/app/list/{sessionId}")
    public List<App> listApp(@PathVariable String sessionId) throws Exception{
        //查询信息
        App appInfo=SessionManager.getInstance().checkSession(sessionId);

        return Explorer.getInstance().getAllApps(appInfo.getWorkspace(), appInfo.getWorkspacePath());
    }

    /**
     * 生成日志曲线

     * @return
     * @throws Exception
     */
    @GetMapping("/monitor/curve/{sessionId}")
    public CurveData getCurveList(@PathVariable String sessionId) throws Exception{
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        return Explorer.getInstance().parseLogCurve(appInfo.getWorkspace(), appInfo.getWorkspacePath(), appInfo.getAppName());
    }

    @GetMapping("/monitor/visual/{sessionId}")
    public Object getCurrentVisualResultInfo(@PathVariable String sessionId) throws Exception{
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        return Explorer.getInstance().getVisualResultInfo(appInfo.getWorkspace(), appInfo.getWorkspacePath(), appInfo.getAppName(),-1);
    }

    @GetMapping("/monitor/visual/{sessionId}/{epoch}")
    public Object getSpecificVisualResultInfo(@PathVariable String sessionId, @PathVariable int epoch) throws Exception{
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        return Explorer.getInstance().getVisualResultInfo(appInfo.getWorkspace(), appInfo.getWorkspacePath(), appInfo.getAppName(),epoch);
    }


    @GetMapping("/console/start/{sessionId}")
    public void startProcess(@PathVariable String sessionId) throws Exception{
        //查询信息
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        Console.getInstance().startTrainProcess(appInfo.getWorkspace(),appInfo.getWorkspacePath(), appInfo.getAppName());
    }


    @GetMapping("/console/stop/{sessionId}")
    public void stopProcess(@PathVariable String sessionId) throws Exception{
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        Console.getInstance().stopTrainingProcess(appInfo.getWorkspace(),appInfo.getWorkspacePath(), appInfo.getAppName());
    }

    @GetMapping("/console/runtime/{sessionId}")
    public List<String> getAppRuntimeLog(@PathVariable String sessionId) throws Exception{
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        return Explorer.getInstance().getAppRuntimeLog(appInfo.getWorkspace(),appInfo.getWorkspacePath(), appInfo.getAppName());
    }

    @GetMapping("/app/running/{sessionId}")
    public Map<String, Boolean> checkAppRunning(@PathVariable String sessionId) throws Exception{
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        boolean isRunning= Console.getInstance().isTrainingProcessRunning(appInfo.getWorkspace(),appInfo.getWorkspacePath(), appInfo.getAppName());
        Map<String,Boolean> result=new HashMap<>();
        result.put("state",isRunning);
        return result;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/app/config/get/{sessionId}")
    public Object getAppConfigJSON(@PathVariable String sessionId) throws Exception{
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        return Explorer.getInstance().getAppConfigJSON(appInfo.getWorkspace(),appInfo.getWorkspacePath(), appInfo.getAppName());
    }

    @PostMapping("/app/config/save/{sessionId}")
    public void saveAppConfigJSON(@PathVariable String sessionId, @RequestBody Object config) throws Exception{
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        Explorer.getInstance().saveAppConfigJSON(config,appInfo.getWorkspace(),appInfo.getWorkspacePath(), appInfo.getAppName());
    }

    @GetMapping("/app/info/classification/{sessionId}")
    public List<String> getAppClassificationLabels(@PathVariable String sessionId) throws Exception{
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        return Explorer.getInstance().getClassifyLabels(appInfo.getWorkspace(),appInfo.getWorkspacePath(), appInfo.getAppName());
    }


    @GetMapping("/monitor/image/{sessionId}/{imageName}")
    @ResponseBody
    public void loadImage(@PathVariable String sessionId, @PathVariable String imageName, HttpServletResponse response) throws Exception{
        App appInfo=SessionManager.getInstance().checkSession(sessionId);
        //load image
        BufferedImage image = Explorer.getInstance().getResultImage(imageName, appInfo.getWorkspace(),appInfo.getWorkspacePath(), appInfo.getAppName());
        ImageIO.write(image, "jpg", response.getOutputStream());
    }



}
