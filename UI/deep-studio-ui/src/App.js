import React from 'react';
//使用路由跳转页面
import { HashRouter as Router, Route, Link } from 'react-router-dom'
import 'antd/dist/antd.css';
import './index.css';
import "./App.css"
import axios from "axios";

import { Layout, Menu } from 'antd';
import {
  DesktopOutlined,
  PieChartOutlined,
  TeamOutlined,
  UserOutlined,
  CopyOutlined
} from '@ant-design/icons';
import Guide from './main/Guide';
import { OpenFile, CreateFile } from "./main/File"
import { empty, workspaceSession, appSession, sessionId } from './main/constant';
import { getUrl, createTitle } from './main/url';
import { AppWorkspace, ConsoleWorkspace } from './main/workspace/appWorkspace';
import { ConfigWorkspace } from './main/workspace/configWorkspace';
import { CurveMonitorWorkspace } from './main/workspace/MonitorWorkspace';
import { NormalVisualMonitorWorkspace } from './main/workspace/VisualMonitorWorkspace';

const { Content, Sider } = Layout;
const { SubMenu } = Menu;

//当侧栏打开时的状态
const uncollapsedProps = {
  collapsed: false,
  logo: "Deep Studio",
  //当打开测栏的时候，logo会慢慢展现出来，否则在视觉上由瞬间的字体错乱。
  logoClassName: "logo-appear",
  //当侧栏被压缩时，主栏的宽度需要变大，左侧距离从200变到80
  mainMarginLeft: 200,
};
//当侧栏压缩时的状态
const collapsedProps = {
  collapsed: true,
  logo: "",
  //当打开测栏的时候，logo会慢慢展现出来，否则在视觉上由瞬间的字体错乱。
  logoClassName: "logo",
  //当侧栏被压缩时，主栏的宽度需要变大，左侧距离从200变到80
  mainMarginLeft: 80,
};

//在工作区中使用url传参的办法传递当前工作区信息

/**
 * 主布局，使用侧栏可拉伸的导航栏布局
 */
class MainLayout extends React.Component {


  /**
   * 初始化构造函数
   * @param {输入参数} props 
   */
  constructor(props) {
    super(props);
    //当前状态是否拉伸
    this.state = {
      //侧栏样式状态
      collapsed: uncollapsedProps.collapsed,
      logo: uncollapsedProps.logo,
      //当打开测栏的时候，logo会慢慢展现出来，否则在视觉上由瞬间的字体错乱。
      logoClassName: uncollapsedProps.logoClassName,
      //当侧栏被压缩时，主栏的宽度需要变大，左侧距离从200变到80
      mainMarginLeft: uncollapsedProps.mainMarginLeft,
    };
    //如果工程已打开，则创建标题
    //创建标题
    createTitle();

  }
  //侧栏收缩
  onCollapse = (collapsedState) => {

    //侧栏被压缩，相应参数变化
    if (collapsedState === true) {
      //如果侧栏被压缩，则不需要显示LOGO
      //更新状态
      this.setState({
        collapsed: collapsedProps.collapsed,
        logo: collapsedProps.logo,
        //当打开测栏的时候，logo会慢慢展现出来，否则在视觉上由瞬间的字体错乱。
        logoClassName: collapsedProps.logoClassName,
        //当侧栏被压缩时，主栏的宽度需要变大，左侧距离从200变到80
        mainMarginLeft: collapsedProps.mainMarginLeft
      })

    } else {
      //打开侧栏时更新状态
      //更新状态
      this.setState({
        collapsed: uncollapsedProps.collapsed,
        logo: uncollapsedProps.logo,
        //当打开测栏的时候，logo会慢慢展现出来，否则在视觉上由瞬间的字体错乱。
        logoClassName: uncollapsedProps.logoClassName,
        //当侧栏被压缩时，主栏的宽度需要变大，左侧距离从200变到80
        mainMarginLeft: uncollapsedProps.mainMarginLeft
      });
    }
  };


  //jsx渲染
  render() {
    return (
      <Router>
        <Layout style={{ minHeight: '100vh' }}>
          {/* 侧工具栏 */}
          <Sider className="fixed-sider" collapsible collapsed={this.state.collapsed} onCollapse={this.onCollapse}>
            <div className={this.state.logoClassName} >
              {this.state.logo}
            </div>
            <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline">
              <SubMenu key="file" icon={<CopyOutlined />} title="文件">
                <Menu.Item key="open-file">
                  <Link to="/open">打开工作环境</Link>
                </Menu.Item>
                <Menu.Item key="create-file">
                  <Link to="/create">新建工作环境</Link>
                </Menu.Item>
              </SubMenu>
              <SubMenu key="app" icon={<PieChartOutlined />} title="应用">
                <Menu.Item key="app-list">
                  <Link to="/app/list/">新建与查找</Link>
                </Menu.Item>
                <Menu.Item key="app-console">
                  <Link to="/app/console/">控制台</Link>
                </Menu.Item>
                <Menu.Item key="app-config">
                  <Link to="/app/config/">配置文件</Link>
                </Menu.Item>
              </SubMenu>
              <SubMenu key="monitor" icon={<DesktopOutlined />} title="监控训练">
                <Menu.Item key="monitor-loss"><Link to="/monitor/curve/">损失函数</Link></Menu.Item>
                <Menu.Item key="monitor-visual"><Link to="/monitor/visual">可视化</Link></Menu.Item>
                <Menu.Item key="monitor-other">其他</Menu.Item>
              </SubMenu>
              <SubMenu key="sub2" icon={<TeamOutlined />} title="代码">
                <Menu.Item key="7">Team 1</Menu.Item>
                <Menu.Item key="8">Team 2</Menu.Item>
              </SubMenu>
              <SubMenu key="help" icon={<TeamOutlined />} title="帮助">
                <Menu.Item key="guide"><Link to="/">欢迎</Link></Menu.Item>
                <Menu.Item key="tips">指南手册</Menu.Item>
              </SubMenu>
            </Menu>
          </Sider>
          {/* 主视图 */}
          <Layout style={{ marginLeft: this.state.mainMarginLeft }}>
            <Content style={{ margin: "0 0" }}>
              {/* 由路由跳转 */}
              <Route path="/" exact component={Guide} />
              <Route path="/open" exact component={OpenFile} />
              <Route path="/create" exact component={CreateFile} />
              <Route path="/app/list" exact component={AppWorkspace} />
              <Route path="/app/console" exact component={ConsoleWorkspace} />
              <Route path="/app/config" exact component={ConfigWorkspace} />
              <Route path="/monitor/curve" exact component={CurveMonitorWorkspace} />
              <Route path="/monitor/visual" exact component={NormalVisualMonitorWorkspace} />
            </Content>
          </Layout>
        </Layout>
      </Router>
    );
  }
}

/**
 * App入口
 */
function App() {
  return (
    <div>
      <MainLayout></MainLayout>
    </div>
  );
}


//导出当前应用组件
export default App;
