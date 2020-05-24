import React from "react"
import "antd/dist/antd.css"
import { Card, notification, Switch, Space } from "antd"
import { JSONEditPanel } from "./EditPanel";
import axios from "axios";
import { appSession, workspaceSession, workspacePathSession } from "../constant"
import { getUrl } from "../url";

/**
 * 配置文件工作区
 */
class ConfigWorkspace extends React.Component {

    //提升正在保存
    savingTitle = " - saving";
    notSavingTitle = "";

    constructor(props) {
        super(props);

        this.state = {
            //json文件
            jsonCode: {},
            //保存标题
            saving: this.notSavingTitle,
            //展示json代码
            showJson:true
        };

    }
    /**
     * 当渲染完组件后调用
     */
    componentDidMount = () => {
        //调用配置文件
        this.loadConfig()
    }

    /**
     * 载入配置文件
     */
    loadConfig = () => {
        axios.get(getUrl("/app/config/get"))
            .then((response) => {
                notification["info"]({
                    message: "载入配置文件成功。",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
                //显示json代码, null, 3
                this.setState({ jsonCode: response.data })
            }).catch((error) => {
                notification["error"]({
                    message: "载入配置文件失败。",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
            });
    }


    /**
     * 当改变显示json的按钮时
     */
    onJSONSwitchChange = (checked) => {
       this.setState({
           showJson:checked
       })
    }

    /**
     * 当编辑文件改变时调用
     */
    onEditValueChange = (value) => {
        this.saveConfig(value)
    }

    /**
     * 保存配置文件
     */
    saveConfig = (configJSON) => {

        //捕获异常，在尚未编辑完成时，json解析会报错，此时不保存。
        try {
            //转配置对象
            let config = JSON.parse(configJSON)
            //设置保存标志
            this.setState({
                saving: this.savingTitle
            })
            //创建标题
            axios.post(getUrl("/app/config/save"), config)
                .then((response) => {
                    //设置保存标志结束
                    this.setState({
                        saving: this.notSavingTitle
                    })
                }).catch((error) => {
                    console.log(error)
                    notification["error"]({
                        message: "保存配置文件失败。",
                        description:
                            'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                    });
                });
        } catch (error) {

        }
    }

    /**
     * 渲染
     */
    render = () => {

        let configPanel;

        if(this.state.showJson){
            configPanel=(
                <JSONEditPanel code={this.state.jsonCode} onValueChange={this.onEditValueChange}></JSONEditPanel>
            )
        }else{
            configPanel=(
                <div>开发中...</div>
            )
        }


        return (
            <div>
                <Card size="small"
                    title={"Settings" + this.state.saving}
                    extra={
                        <Space>
                            JSON
                            <Switch defaultChecked onChange={this.onJSONSwitchChange} size="small" />
                        </Space>
                    }
                    bodyStyle={{height:"91vh"}}
                >
                     {/* 配置面板 */}
                   {configPanel}
                   </Card>
            </div>
        )
    }

}

export { ConfigWorkspace }