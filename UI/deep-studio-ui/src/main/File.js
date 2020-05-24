import React from "react"
import { Input, Button, List, Avatar, Card, Spin, Space, notification } from 'antd';
//使用axios请求ajax
import axios from "axios";
import carLogo from '../drawable/car-logo.png';
import 'antd/dist/antd.css';
import { UserOutlined, FormOutlined, PlusOutlined } from '@ant-design/icons';
import { workspaceSession, workspacePathSession, sessionId } from "./constant";



/**
 * 文件类操作，包括打开文件与新建文件两个操作。
 */


/**
 * 打开工程
 */
class OpenFile extends React.Component {

    /**
     * 构造函数
     * @param {*} props 
     */
    constructor(props) {
        super(props)

        //数据是否加载完毕
        this.state = {
            loaded: false
        }
        //请求近期项目，异步加载！！
        axios.get("http://localhost:8080/workspace/recent")
            .then((response) => {
                this.recentList = [...response.data]
                this.setState({ loaded: true })
            }).catch((error) => {
                console.log(error)
            });

    }

    /**
     * 打开工程
     */
    openWorkspace = (workspace, path) => {
        axios.post("http://localhost:8080/workspace/open", {
            name: workspace,
            path: path
        }).then((response) => {
            //保存当前会话
            sessionStorage.setItem(sessionId, response.data.sessionId)

            notification["info"]({
                message: "已打开工作区。",
                description:
                    'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
            });
            //页面标题修改
            document.title = workspace;
            //页面跳转
            this.props.history.push("/app/list/");
        }).catch((error) => {
            notification["error"]({
                message: "无法打开工程。",
                description:
                    'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
            });
        });
    }


    /**
     * 渲染
     */
    render() {
        let recentListDiv;

        //切记，异步加载完数据以后才能渲染列表，否则渲染空表
        if (this.state.loaded === false) {
            recentListDiv = (
                <Space>
                    <Spin />
                        加载中...
                </Space>
            );
        } else {
            recentListDiv = (
                <div>
                    <List
                        itemLayout="horizontal"
                        dataSource={this.recentList}
                        renderItem={(item) => (
                            <List.Item
                                //这边的Link组件无效，因此使用超链接自己写url的方式，前面切记要加 /#/
                                actions={[<div onClick={(e) => { this.openWorkspace(item.name, item.path) }}>打开</div>]}
                            >
                                <List.Item.Meta
                                    avatar={<Avatar src={carLogo} />}
                                    title={item.name}
                                    description={item.path}
                                />
                            </List.Item>
                        )}
                    />
                </div>
            );
        }
        return (
            <div>
                <div className="card-container">
                    <Card title="打开工作环境" className="open-card" headStyle={{ fontWeight: "bolder" }}>
                        <Input placeholder="工作环境名称" prefix={<UserOutlined />} onChange={this.onNameChange} />
                        <br />
                        <br />
                        <Input placeholder="保存位置" prefix={<UserOutlined />} onChange={this.onPathChange} />
                        <br />
                        <br />
                        <Button type="primary" icon={<FormOutlined />} onClick={this.openWorkspace}>
                            打开
                        </Button>
                    </Card>
                </div>
                <div className="card-container">
                    <Card title="近期环境" className="recent-card" headStyle={{ fontWeight: "bolder" }}>
                        {recentListDiv}
                    </Card>
                </div>
            </div >
        )
    }
}

/**
 * 创建工程
 */
class CreateFile extends React.Component {

    /**
     * 输入状态
     */
    state = {
        name: "",
        path: "",
        //控制进度条变化
        loading: false,
        buttonDisabled: false
    };


    /**
     * 监控名称变化
     */
    onNameChange = (e) => {
        this.setState({ name: e.target.value })
    }

    /**
     * 监控路径变化
     */
    onPathChange = (e) => {
        this.setState({ path: e.target.value })
    }

    /**
     * 
     * 发送
     */
    onClick = () => {

        //进入加载状态
        this.setState({
            loading: true,
            buttonDisabled: true
        });
        //发送请求
        axios.post("http://localhost:8080/workspace/create", {
            name: this.state.name,
            path: this.state.path
        }).then((response) => {
            //保存当前会话
            sessionStorage.setItem(sessionId, response.data.sessionId)
            this.setState({ loading: false, buttonDisabled: false });
            notification["info"]({
                message: "创建工作区完成。",
                description:
                    'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
            });
            //创建标题
            document.title = this.state.name;
            // 跳转
            this.props.history.push("/app/list/")
        }).catch((error) => {
            this.setState({ loading: false, buttonDisabled: false });
            notification["error"]({
                message: "无法创建工作区。",
                description:
                    'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
            });
        });
    }

    render() {
        return (
            <div className="card-container">
                <Card title="新建工作环境" className="create-card" headStyle={{ fontWeight: "bolder" }}>
                    <Input placeholder="工作环境名称" prefix={<UserOutlined />} onChange={this.onNameChange} />
                    <br />
                    <br />
                    <Input placeholder="保存位置" prefix={<UserOutlined />} onChange={this.onPathChange} />
                    <br />
                    <br />
                    <Button type="primary" icon={<PlusOutlined />} loading={this.state.loading} disabled={this.state.buttonDisabled} onClick={this.onClick} style={{ color: "#fff" }}>
                        创建
                    </Button>
                </Card>
            </div>
        )
    }
}



export { OpenFile, CreateFile };