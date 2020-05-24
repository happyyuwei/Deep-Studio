import React from "react"
import "antd/dist/antd.css"
import { Card, Input, Space, List, Avatar, Spin, message, Statistic, Skeleton, Collapse, Button, notification, Slider, InputNumber, Row, Col } from "antd"
import { PlusOutlined, SettingOutlined } from '@ant-design/icons';
import carLogo from '../../drawable/car-logo.png';
import { appSession, workspaceSession, workspacePathSession } from "../constant"
import axios from "axios";
import { Line } from '@antv/g2plot'
import { getUrl } from "../url";

const { Panel } = Collapse;
const { Meta } = Card;

class NormalVisualMonitorWorkspace extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            //信息加载状态，只有状态加载完，才开始渲染
            infoLoad: false,

            //当前显示的轮数
            currentEpoch: 0,
            //最大轮数
            maxEpoch: 0,
            //分类标签，只在分类任务里有效
            classificationLabels: [],
            //可视化数据，只保存当前展示的数据
            visualList: []

        }
    }

    /**
     * 载入基本信息，仅调用一次
     */
    loadBasicInfo = () => {
        //获取分类标签
        axios.get(getUrl("/app/info/classification/"))
            .then((response) => {
                //只有在分类任务中才需要标签
                this.setState({
                    classificationLabels: [...response.data]
                })
                //载入最近的可视化信息
                this.startLoadingVisualInfo();
            }).catch((error) => {
                console.log(error)
                notification["error"]({
                    message: "载入可视化基本信息失败。",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
            });
    }

    /**
     * 载入最近的可视化信息
     */
    loadLatestVisualInfo = () => {
        axios.get(getUrl("/monitor/visual"))
            .then((response) => {
                const visualList = [...response.data];
                //当前最大轮数
                let maxEpoch = 0;
                if (visualList.length > 0) {
                    const imageList = visualList[0].imageList;
                    //若没有图片时，默认是0
                    if (imageList.length > 0) {
                        maxEpoch = imageList[0].split("_")[0];
                    }
                }
                this.setState({
                    visualList: visualList,
                    infoLoad: true,
                    maxEpoch: maxEpoch,
                    //总是显示最新的轮数
                    currentEpoch:maxEpoch
                })
            }).catch((error) => {
                console.log(error)
                notification["error"]({
                    message: "载入可视化数据失败。",
                    description:
                        'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
                });
            });
    }

    /**
     * 渲染完基本骨架后调用
     */
    componentDidMount = () => {
        //载入基本信息
        this.loadBasicInfo();
    }

    /**
     * 组件卸载时调用
     */
    componentWillUnmount = () => {
        this.stopLoadingVisualInfo();
    }

    /**
     * 当滑动条变化
     */
    onSliderChange = (value) => {
        this.setState({
            currentEpoch: value,
        });
    };

    /**
     * 开始载入可视化信息
     */
    startLoadingVisualInfo = () => {
        if (this.timeManager === undefined) {
            this.timeManager = setInterval(this.loadLatestVisualInfo, 5000)
        }
    }

    /**
     * 停止载入可视化信息
     */
    stopLoadingVisualInfo = () => {
        if (this.timeManager !== undefined) {
            clearInterval(this.timeManager)
            this.timeManager = undefined
        }
    }

    /**
     * 渲染
     */
    render = () => {
        let body;

        if (this.state.infoLoad === false) {
            //加载时显示骨架屏
            body = (
                <div>
                    <Skeleton active paragraph={{ rows: 4 }} />
                </div>
            )
        } else {
            //当加载完信息，渲染可视化区域
            //如果是分类任务，则加载分类任务可视化
            // if (this.state.classificationLabels.length > 0) {
            //目前全部使用该模板
            body = renderClassificationVisualArea(this.state.visualList, this.state.classificationLabels);
            
            // } else {
            //     //如果是其他是任务，则加载通用可视化任务
            //     body = renderNormalVisualArea(this.state.visualList);
            // }
        }

        return (
            <Card size="small"
                title={"可视化结果"}
                extra={
                    [<Button icon={<SettingOutlined />}></Button>]
                }
                headStyle={{ fontWeight: "bolder" }}
                style={{ minHeight: "100vh" }}
            >
                <div>
                    {/* 滑条输入 */}
                    <Row>
                        <Col span={12}>
                            <Slider
                                min={1}
                                max={this.state.maxEpoch}
                                onChange={this.onSliderChange}
                                value={typeof this.state.currentEpoch === 'number' ? this.state.currentEpoch : 0}
                            />
                        </Col>
                        <Col>
                            <InputNumber
                                min={1}
                                max={this.state.maxEpoch}
                                style={{ margin: '0 16px' }}
                                value={this.state.currentEpoch}
                                onChange={this.onSliderChange}
                            />
                            <span style={{ fontWeight: "bold" }}>
                                Epoch
                            </span>
                        </Col>
                    </Row>
                    <div>
                        {body}
                    </div>
                </div>
            </Card>
        )
    }

}

//Todo 目前只能分开来排版，还没有很好的设计
/**
 * 渲染分类任务展示区域
 */
const renderClassificationVisualArea = (visualList, classificationLabel) => {

    //渲染列表，一行代表一个输出
    const area = visualList.map((element) => {

        //渲染图像
        const imagePart = element.imageList.map((imageName) => {
            return <Col span={8}>
                <Card
                    hoverable
                    style={{ width: 240 }}
                    cover={<img alt="example" src={getUrl("/monitor/image") + "/" + imageName} />}
                >
                    <Meta title={imageName} />
                </Card>
            </Col>
        });

        //如果还有标签的话一起渲染，目前考虑不渲染
        //对象转es6 Map
        let labelArray = [];
        for (let key in element.labels) {
            // labelMap.set(key, element.labels[key]);
            labelArray.push({
                "key": key,
                "value": element.labels[key]
            })
        }

        /**
         * 计算标签
         */
        const calculateLabelFunction = (probArray, classificationLabel) => {

            let max = 0;
            let index = 0;
            for (let i = 0; i < probArray.length; i++) {
                if (probArray[i] > max) {
                    max = probArray[i];
                    index = i;
                }
            }

            return classificationLabel[index];
        }

        //遍历渲染
        const labelPart = labelArray.map((item) => {
            return <Col span={8}>
                <Card
                    hoverable
                    style={{ width: 240 }}
                >
                    <Statistic title={item.key} value={calculateLabelFunction(item.value, classificationLabel)} />
                </Card>
            </Col>
        })
        //渲染列表
        return (
            <Row gutter={16} style={{ margin: 10 }}>
                {imagePart}
                {labelPart}
            </Row>
        )
    })

    return area;
}

/**
 * 渲染通用可视化面板，只有图像，暂时不用
 * @param {} imageList 
 */
const renderNormalVisualArea = (imageList) => {
    const gridStyle = {
        width: '25%',
        textAlign: 'center',
    };
    return (
        <Card title="Card Title">
            <Card.Grid style={gridStyle}>Content</Card.Grid>
            <Card.Grid style={gridStyle}>Content</Card.Grid>
            <Card.Grid style={gridStyle}>Content</Card.Grid>
            <Card.Grid style={gridStyle}>Content</Card.Grid>
            <Card.Grid style={gridStyle}>Content</Card.Grid>
            <Card.Grid style={gridStyle}>Content</Card.Grid>
        </Card>
    )
}


export { NormalVisualMonitorWorkspace }