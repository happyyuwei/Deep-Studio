import React from "react"
import { UnControlled as CodeMirror } from 'react-codemirror2';

import 'codemirror/lib/codemirror.css';

import 'codemirror/mode/javascript/javascript';
import "codemirror/theme/eclipse.css"
import "codemirror/theme/blackboard.css"
//括号匹配
import "codemirror/addon/edit/matchbrackets"
//js代码检查
// import "codemirror/addon/lint/lint"
// import "codemirror/addon/lint/lint"
// import "codemirror/addon/lint/json-lint"
//支持代码折叠
import "codemirror/addon/fold/foldgutter.css"
import "codemirror/addon/fold/foldcode"
import "codemirror/addon/fold/foldgutter"
import "codemirror/addon/fold/brace-fold"
import "codemirror/addon/fold/comment-fold"
import "codemirror/addon/selection/active-line"
//格式化
// import "codemirror/addon/format/json-format"

//智能缩进单位
const indentUnit=3;

class JSONEditPanel extends React.Component {


    render = () => {
        return (
            <CodeMirror
            // 格式化json
                value={JSON.stringify(this.props.code, null, indentUnit)}
                options={{
                    mode: { name: "javascript", "json": true },
                    theme: "blackboard",
                    indentUnit: indentUnit,  // 缩进单位
                    smartIndent: true,  // 是否智能缩进
                    // 高亮当前行
                    styleActiveLine:true,
                    //显示行号
                    lineNumbers: true,
                    //匹配括号
                    matchBrackets: true,
                    //代码检查(暂无效)
                    // lint: true,
                    //代码折叠
                    foldGutter: true,
                    gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter", "CodeMirror-lint-markers"]
                }}

                /**
                 * 改变字符串时调用
                 */
                onChange={(editor, data, value) => {
                    this.props.onValueChange(value);
                }}

                // onBeforeChange={(editor, data, value) => {

                // }}
            />
        )
    }

}


export { JSONEditPanel }

