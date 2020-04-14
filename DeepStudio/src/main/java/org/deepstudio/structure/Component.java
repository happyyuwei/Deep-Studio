package org.deepstudio.structure;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 组件bean
 */
public class Component {

    //组件名，需要指明 tensorflow.keras.layers.....
    protected String componentName;

    //参数列表
    protected Map<String, Object> argsMap;

    /**
     * 组件名
     * @param componentName 组件名
     */
    public Component(String componentName){
        this.componentName=componentName;
        this.argsMap=new HashMap<>();
    }

    public String getComponentName() {
        return componentName;
    }

    public Map<String, Object> getArgsMap() {
        return argsMap;
    }

    public void setArg(String arg, Object value){
        this.argsMap.put(arg, value);
    }

    public Object getArg(String arg){
        return this.argsMap.get(arg);
    }

    public Set<String> argSet(){
        return this.argsMap.keySet();
    }

    @Override
    public String toString(){
        return "{component:"+this.componentName+", args:"+this.argsMap+"}";
    }


}
