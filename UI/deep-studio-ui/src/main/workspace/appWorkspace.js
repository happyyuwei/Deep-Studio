import React from "react"
import "antd/dist/antd.css"
import { Card, Input, Space, List, Avatar, Spin, Button, notification, Modal, Switch } from "antd"
import { PlusOutlined, PoweroffOutlined, CaretRightOutlined, SettingOutlined } from '@ant-design/icons';
import carLogo from '../../drawable/car-logo.png';
import { appSession, workspaceSession, workspacePathSession, sessionId } from "../constant"
import axios from "axios";
import { getUrl, createTitle } from "../url";

import { FixedSizeList } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";


const { Search } = Input



/**
 * App 新建与查找 工作区
 */
class AppWorkspace extends React.Component {


    /**
     * 构造函数
     * @param {} props 
     */
    constructor(props) {
        super(props);

        this.state = {
            loading: false,
        }
        //查询Applist
        this.listApp();
    }


    /**
     * 查询所有App
     */
    listApp = () => {
        //初始状态，显示加载中
        this.setState({ loading: false })
        axios.get("http://localhost:8080/app/list/" + sessionStorage.getItem(sessionId))
            .then((response) => {
                this.appList = [...response.data]
                this.setState({ loading: true });
            }).catch((error) => {
                this.setState({ loading: false });
                notification["error"]({
                    message: "无法查询应用",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
            });
    }

    /**
     * 创建App
     */
    requestAppCreate = (appName) => {
        axios.post("http://localhost:8080/app/create/" + sessionStorage.getItem(sessionId), {
            appName: appName
        })
            .then((response) => {
                //创建标题
                createTitle();
                notification["info"]({
                    message: "创建应用完成",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
                //跳转
                this.props.history.push("/app/console/");
                
            }).catch((error) => {
                console.log(error);
                notification["error"]({ 
                    message: "无法创建应用。",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
            });
    }


    /**
     * 进入监控页面
     * @param {} app 
     */
    onAppChange(appName) {
        //保存
        axios.post("http://localhost:8080/app/open/" + sessionStorage.getItem(sessionId), {
            appName: appName
        }).then((response) => {
            //創建標題
            createTitle();
            notification["info"]({
                message: "已打开应用。",
                description:
                    'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
            });
            //跳转
            this.props.history.push("/app/console/");

        }).catch((error) => {
            notification["error"]({
                message: "无法打开应用。",
                description:
                    'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
            });
        });
    }


    render=()=> {

        let appListDiv;

        //切记，异步加载完数据以后才能渲染列表，否则渲染空表
        if (this.state.loaded === false) {
            appListDiv = (
                <Space>
                    <Spin />
                        加载中...
                </Space>
            );
        } else {
            appListDiv = (
                <div>
                    <List
                        itemLayout="horizontal"
                        dataSource={this.appList}
                        renderItem={(app) => (
                            <List.Item
                                actions={[<div onClick={(e) => { this.onAppChange(app.appName) }}>监控</div>]}
                            >
                                <List.Item.Meta
                                    avatar={<Avatar src={carLogo} />}
                                    title={app.appName}
                                    description="No description found."
                                />
                            </List.Item>
                        )}
                    />
                </div>
            );
        }
        return (
            <div>
                <div className="app-card-container">
                    <Card className="app-create-card">
                        <Search placeholder="新建应用" onSearch={(value) => this.requestAppCreate(value)} enterButton={<PlusOutlined />} />
                    </Card>
                </div>
                <div className="app-card-container">
                    <Card title="所有应用" className="app-recent-card" headStyle={{ fontWeight: "bolder" }}>
                        {appListDiv}
                    </Card>

                </div>
            </div >
        )
    }

}


/**
 * App 控制台工作区
 */
class ConsoleWorkspace extends React.Component {


    trainingDescAvailable = "空闲";
    trainingDescBusy = "训练中";
    logDesriptionAvailable = "暂停加载日志";
    logDesriptionBusy = "正在载入日志";


    constructor(props) {
        super(props);

        this.state = {
            "startButtonDisabled": false,
            "stopButtonDisabled": false,
            //每隔一段时间查询一次是否有新日志，只有启动训练才开始查询，当退出当前页面，停止查询，关闭训练，停止查询。
            //运行状态
            "trainingState": false,
            //运行时日志
            "runtimeLog": [],
            "trainingDescription": this.trainingDescAvailable,
            "logDesription": this.logDesriptionAvailable,

            //设置框
            "settingModalVisible": false
        }

    }

    /**
     * 渲染完成
     */
    componentDidMount = () => {
        // 一旦渲染完毕，则开始检查训练状态
        //检查训练状态
        this.checkTrainingState();
    }

    /**
     * 开始载入日志
     */
    startLoadRuntime = () => {
        if (this.timeManager === undefined) {
            this.setState({
                logDesription: this.logDesriptionBusy
            })
            this.timeManager = setInterval(this.loadRuntime, 5000)
        }
    }

    /**
     * 停止载入日志
     */
    stopLoadRuntime = () => {
        if (this.timeManager !== undefined) {
            this.setState({
                logDesription: this.logDesriptionAvailable
            })
            clearInterval(this.timeManager)
            this.timeManager = undefined
        }
    }

    /**
     * 当页面切换，则停止载入日志
     */
    componentWillUnmount = () => {
        this.stopLoadRuntime()
    }

    /**
     * 加载运行日志
     */
    loadRuntime = () => {
        axios.get(getUrl("/console/runtime"))
            .then((response) => {
                //更新日志
                this.setState({ "runtimeLog": [...response.data] })
            }).catch((error) => {
                notification["error"]({
                    message: "加载日志失败",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
            });
    }


    /**
     * 查看训练状态
     */
    checkTrainingState = () => {
        axios.get(getUrl("/app/running"))
            .then((response) => {
                this.setState({
                    "trainingState": response.data.state
                });
                if (response.data.state === true) {
                    this.setState({
                        "trainingDescription": this.trainingDescBusy
                    })
                    notification["success"]({
                        message: "训练进程正在运行",
                        description:
                            'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                    });
                    //加载日志
                    this.startLoadRuntime();
                }
            }).catch((error) => {

            });
    }

    /**
     * 启动训练
     */
    startTraining = () => {
        //禁用按键
        this.setState({
            "startButtonDisabled": true
        })
        axios.get(getUrl("/console/start"))
            .then((response) => {
                notification["success"]({
                    message: "训练进程启动",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
                this.setState({
                    "startButtonDisabled": false,
                    "trainingDescription": this.trainingDescBusy
                })
                //开始载入运行时
                this.startLoadRuntime()
            }).catch((error) => {
                console.log(error)
                notification["error"]({
                    message: "训练进程启动失败",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
                this.setState({
                    "startButtonDisabled": false
                })
            });
    }


    /**
     * 停止训练
     */
    stopTraining = () => {
        //禁用按键
        this.setState({
            "stopButtonDisabled": true
        })
        axios.get(getUrl("/console/stop"))
            .then((response) => {
                //停止監聽日志
                this.stopLoadRuntime();
                notification["success"]({
                    message: "训练进程已停止",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
                this.setState({
                    "stopButtonDisabled": false,
                    "trainingDescription": this.trainingDescAvailable
                })
            }).catch((error) => {
                notification["error"]({
                    message: "训练进程停止失败",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
                this.setState({
                    "stopButtonDisabled": false
                })
            });
    }
    /**
     * 打开设置模态框
     */
    openSettingModel = () => {
        this.setState({
            settingModalVisible: true,
        });
    }

    /**
     * 设置框确认按钮
     */
    settingModelHandleOk = (e) => {
        this.setState({
            settingModalVisible: false,
        });
    };

    /**
     * 设置框取消按钮
     */
    settingModelHandleCancel = (e) => {
        this.setState({
            settingModalVisible: false,
        });
    };

    /**
     * 自动刷新日志开启
     */
    onUpdateLogAutoChange = (checked) => {
        if (checked) {
            this.startLoadRuntime();
        } else {
            this.stopLoadRuntime();
        }
    }

    /**
     * 渲染控制台每一行，使用react-window的虚拟dom，增加长列表渲染性能
     */
    renderRow = ({ index, style }) => {

        return (
            <div style={style}>
                {this.state.runtimeLog[index]}
                </div>
        );
    }

    /**
     * 总渲染
     */
    render = () => {
        return (
            // 这里一定要设置宽高，否则后面的react-window无法定位大小
            <div style={{ width: "100%", height: "100%" }}>
                <Card size="small" title={"控制台 - " + this.state.trainingDescription}
                    bordered={false}
                    extra={
                        [
                            <Space>
                                <Button ghost size="small" icon={<CaretRightOutlined />} onClick={this.startTraining} disabled={this.state.startButtonDisabled}></Button>
                                <Button ghost size="small" icon={<PoweroffOutlined />} onClick={this.stopTraining} disabled={this.state.stopButtonDisabled}></Button>
                                <Button ghost size="small" icon={<SettingOutlined />} onClick={this.openSettingModel}></Button>
                            </Space>
                        ]
                    }
                    headStyle={{ backgroundColor: "#001529", color: "#fff", borderRadius: 0 }}
                    bodyStyle={{ height: "93vh", color: "#fff", backgroundColor: "#012456" }}
                >
                    {/* 使用react-window加载长列表 */}
                    <AutoSizer>
                        {({ height, width }) => (
                            <FixedSizeList height={height} itemCount={this.state.runtimeLog.length} itemSize={35} width={width}>
                                {this.renderRow}
                            </FixedSizeList>
                        )}
                    </AutoSizer>

                </Card>


                {/* 设置模态框 */}
                <Modal
                    title="设置（开发中）"
                    visible={this.state.settingModalVisible}
                    onOk={this.settingModelHandleOk}
                    onCancel={this.settingModelHandleCancel}
                >
                    <div>
                        <Space>
                            <div>自动刷新日志  </div>
                            <Switch size="small" defaultChecked onChange={this.onUpdateLogAutoChange} />
                        </Space>
                    </div>
                </Modal>
            </div>
        )
    }
}





export { AppWorkspace, ConsoleWorkspace }