import React from "react"
import "antd/dist/antd.css"
import { Card, Input, Space, List, Avatar, Spin, message, Collapse, Button, notification, Row, Col } from "antd"
import { PlusOutlined, SettingOutlined } from '@ant-design/icons';
import carLogo from '../../drawable/car-logo.png';
import { appSession, workspaceSession, workspacePathSession } from "../constant"
import axios from "axios";
import { Line } from '@antv/g2plot'
import { getUrl } from "../url";

const { Panel } = Collapse;






class CurveMonitorWorkspace extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            //加载图标信息完成
            infoLoaded: false,
            //损失折叠面板打开的序号
            // lossFigureKey: [],
            //图表信息
            lossFigureInfo: [],
            //评估折叠面板打开的序号
            // evalFigureKey: [],
            //评估图表信息
            evalFigureInfo: [],
            //面板键
            activeKey: [],
            //图表框架是否需要渲染，只有把折叠框都渲染完才能开始渲染
            figureShouldRender: false
        }
        //载入图表信息
        this.loadFigureInfo();

    }

    /**
     * 载入图表信息，只载入一次。
     */
    loadFigureInfo = () => {
        axios.get(getUrl("/monitor/curve"))
            .then((response) => {
                let curveData = { ...response.data }

                //创建打开面板的键
                let activeKey = []
                for (let i = 0; i < curveData.lossData.length; i++) {
                    activeKey.push("loss-" + curveData.lossData[i].lossName)
                }

                //
                for (let i = 0; i < curveData.evalData.length; i++) {
                    activeKey.push("eval-" + curveData.evalData[i].evalName)
                }


                let lossFigureInfo = []
                //遍历损失数据
                for (let i = 0; i < curveData.lossData.length; i++) {
                    lossFigureInfo.push({
                        panelName: curveData.lossData[i].lossName,
                        key: "loss-" + curveData.lossData[i].lossName
                    })
                }
                let evalFigureInfo = []
                //遍历评估数字
                for (let i = 0; i < curveData.evalData.length; i++) {
                    evalFigureInfo.push({
                        panelName: curveData.evalData[i].evalName,
                        key: "eval-" + curveData.evalData[i].evalName
                    })
                }

                //更新状态
                this.setState({
                    //加载图标信息完成
                    infoLoaded: true,
                    //折叠面板打开的序号
                    activeKey: activeKey,
                    //图表信息
                    lossFigureInfo: lossFigureInfo,
                    //评估图表信息
                    evalFigureInfo: evalFigureInfo,
                    //可以渲染图表框
                    figureShouldRender: true
                })
                // this.loadLossData();

            }).catch((error) => {
                console.log(error)
                notification["error"]({
                    message: "加载图表信息失败。",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
            });
    }

    /**
     * 开始加载损失曲线
     */
    startLoadCurveData = () => {
        if (this.timeManager === undefined) {
            this.timeManager = setInterval(this.loadLossData, 5000)
        }
    }

    /**
     * 停止载入损失曲线
     */
    stopLoadCurveData = () => {
        if (this.timeManager !== undefined) {
            clearInterval(this.timeManager)
            this.timeManager = undefined
        }
    }

    /**
     * 将列表重新整理成G2的格式
     */
    arrangeList = (key, valueList) => {

        let list = []

        for (let i = 0; i < key.length; i++) {
            let element = {
                "epoch": key[i],
                "value": valueList[i]
            }
            list.push(element);
        }
        return list;
    }

    /**
     * key=[1,2,3]
     * valueList=[[1,2,3],[2,3,4]]
     * typeList=["a","b"]
     * =>
     * [{"epoch":1, "type":"a","value"=[1]}]
     */
    arrangeMoreList = (key, valueList, typeList) => {

        let list = []

        for (let i = 0; i < key.length; i++) {
            for (let j = 0; j < typeList.length; j++) {
                let element = {
                    "epoch": key[i],
                    "type": typeList[j],
                    "value": valueList[j][i]
                }
                list.push(element);
            }
        }
        return list;
    }


    /**
     * 载入训练损失数据
     */
    loadLossData = () => {
        //创建标题
        axios.get(getUrl("/monitor/curve"))
            .then((response) => {
                const curveData = { ...response.data }
                //渲染数据
                //渲染损失曲线
                for (let i = 0; i < curveData.lossData.length; i++) {
                    //获取id
                    let id = "loss-" + curveData.lossData[i].lossName + "-train";
                    let data = this.arrangeList(curveData.epoch, curveData.lossData[i].trainLoss);
                    this.renderFigure(id, "Train: " + curveData.lossData[i].lossName, data, false);

                    //获取id
                    id = "loss-" + curveData.lossData[i].lossName + "-test";
                    data = this.arrangeList(curveData.epoch, curveData.lossData[i].testLoss)
                    this.renderFigure(id, "Test: " + curveData.lossData[i].lossName, data, false);

                    // 获取id
                    id = "loss-" + curveData.lossData[i].lossName + "-compare";
                    data = this.arrangeMoreList(curveData.epoch, [curveData.lossData[i].trainLoss, curveData.lossData[i].testLoss], ["train: " + curveData.lossData[i].lossName, "test: " + curveData.lossData[i].lossName])
                    this.renderFigure(id, "Compare: " + curveData.lossData[i].lossName, data, true);
                }
                // 渲染评估曲线
                for (let i = 0; i < curveData.evalData.length; i++) {
                    //获取id
                    let id = "eval-" + curveData.evalData[i].evalName;
                    let data = this.arrangeList(curveData.epoch, curveData.evalData[i].data);
                    this.renderFigure(id, curveData.evalData[i].evalName, data, false);
                }
            }).catch((error) => {
                console.log(error)
                notification["error"]({
                    message: "载入数据失败。",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
            });
    }

    //图表，用于更新数据
    figureMap = {

    }

    /**
     * 渲染图表框，只调用一次
     */
    renderFigure = (id, figureName, data, multiLine) => {

        //空表
        // const data = [{ epoch: '0', value: 0 }];

        if (multiLine === true) {
            const linePlot = new Line(id, {
                title: {
                    visible: true,
                    text: figureName,
                },
                //渲染像素比，很重要，默认为2，在1920*1080分辨率上有点糊。
                pixelRatio: 4,
                //数据，只能叫data，有点坑
                data,
                xField: "epoch",
                yField: "value",
                forceFit: true,
                meta: {
                    //提示栏上加上单位
                    epoch: {
                        formatter: (v) => { return `epoch ${v}` }
                    }
                },
                legend: {
                    position: 'top-right',
                },
                seriesField: 'type',
                responsive: true,
                // padding: 'auto'
            });
            this.figureMap[id] = linePlot;
            linePlot.render();
        } else {
            const linePlot = new Line(id, {
                title: {
                    visible: true,
                    text: figureName,
                },
                //渲染像素比，很重要，默认为2，在1920*1080分辨率上有点糊。
                pixelRatio: 4,
                //数据，只能叫data，有点坑
                data,
                xField: "epoch",
                yField: "value",
                forceFit: true,
                meta: {
                    //提示栏上加上单位
                    epoch: {
                        formatter: (v) => { return `epoch ${v}` }
                    },
                },
                responsive: true,
            });
            this.figureMap[id] = linePlot;
            linePlot.render();
        }
    }

    /**
     * 更新完组件调用
     */
    componentDidUpdate = () => {
        if (this.state.figureShouldRender) {
            this.startLoadCurveData();
        }
    }

    /**
     * 组件退出时停止
     */
    componentWillUnmount = () => {
        this.stopLoadCurveData();
    }


    render() {
        // return (<div id="canvas" style={{ width: 300, height: 300 }}></div>)
        let body;
        if (!this.state.infoLoaded) {
            body = (
                <div>
                    加载中...
                </div>
            )
        } else {
            const lossItems = this.state.lossFigureInfo.map((info) =>
                <Panel header={info.panelName} key={info.key}>
                    <Row>
                        <Col span={8}>
                            <div id={info.key + "-train"} style={{ height: "25vw" }} ></div>
                        </Col>
                        <Col span={8}>
                            <div id={info.key + "-test"} style={{ height: "25vw" }}></div>
                        </Col>
                        <Col span={8}>
                            <div id={info.key + "-compare"} style={{ height: "25vw" }}></div>
                        </Col>
                    </Row>
                </Panel>
            );
            const evalItems = this.state.evalFigureInfo.map((info) =>
                <Panel header={info.panelName} key={info.key}>
                    <Row>
                        <Col span={8}>
                            <div id={info.key} style={{ height: "25vw" }} ></div>
                        </Col>
                        <Col span={8}>
                            {/* do nothing */}
                        </Col>
                        <Col span={8}>
                            {/* do nothing */}
                        </Col>
                    </Row>
                </Panel>
            );

            body = (
                <Collapse defaultActiveKey={this.state.activeKey}>
                    {lossItems}
                    {evalItems}
                </Collapse>
            )
        }
        return (
            <Card size="small"
                title={"损失与评估"}
                extra={
                    [<Button icon={<SettingOutlined />}></Button>]
                }
                headStyle={{ fontWeight: "bolder" }}
                style={{ minHeight: "100vh" }}
            >
                {body}
            </Card>

        )
    }


}

export { CurveMonitorWorkspace }