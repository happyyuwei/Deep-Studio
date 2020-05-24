import React from "react"
import logo from '../drawable/deep-studio.svg';
import antdLogo from "../drawable/antd-logo.png"
import reactLogo from "../drawable/react-logo.png"
import { Typography, Space } from "antd"
import { FundTwoTone } from '@ant-design/icons'
const { Paragraph, Text, Title } = Typography

class Guide extends React.Component {
    render() {
        return (
            <div className="logo-container">
                <img src={logo} className="App-logo" alt="logo" />
                <Title level={4} type={"secondary"}>
                    企业级人工智能产品设计环境，创造高效愉快的工作体验。
                </Title>
                <Title level={4} style={{ color: "#4F8CC3" }}>
                    <Space>
                        <FundTwoTone />
                        0.0.1-beta 正式发布
                    </Space>
                </Title>
                {/* <Title level={4} Text strong type="secondary" style={{fontStyle:"italic"}}>
                    Powered by
                </Title> */}
                <Text type="secondary" style={{fontSize: 15, fontWeight:"bold"}}>
                    <Space>
                        <span style={{fontStyle:"italic"}}>
                    Powered by:
                    </span>
                        <img src={antdLogo} className="power-logo" alt="logo" />
                        React
                        <img src={reactLogo} className="power-logo" alt="logo" />
                        Ant Design
                    </Space>
                </Text>


            </div>
        )
    }
}

export default Guide