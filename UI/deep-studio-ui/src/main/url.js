import {sessionId} from "./constant"
import axios from "axios"

const getUrl=(api)=>{
    return "http://localhost:8080"+api+"/" + sessionStorage.getItem(sessionId)
}


/**
 * 創建標題
 */
const createTitle=()=>{
    //创建标题
    axios.get(getUrl("/workspace/info"))
      .then((response) => {
        //查询会话名称
        const appInfo = response.data.app;
        
        if (appInfo !== undefined) {
          if (appInfo.appName !== null) {
            document.title = appInfo.appName + " - " + appInfo.workspace
          } else {
            document.title = appInfo.workspace
          }
        }

      }).catch((error) => {
        console.log(error)
      });
}

export {getUrl, createTitle}