# IDEA显示Run Dashboard窗口

## 设置步骤

1.找到.idea下面的workspace.xml文件   2.在下面的代码中加入一段配置代码   源代码位置

```xml
<component name="RunDashboard">
  <option name="ruleStates">
      <list>
        <RuleState>
          <option name="name" value="ConfigurationTypeDashboardGroupingRule" />
        </RuleState>
        <RuleState>
          <option name="name" value="StatusDashboardGroupingRule" />
        </RuleState>
      </list>
   </option>
    <option name="contentProportion" value="0.22874807" />
  </component>
```

  配置代码

```xml
<option name="configurationTypes">
      <set>
        <option value="SpringBootApplicationConfigurationType" />
      </set>
</option>
```



  3.大功告成，idea启动后Run Dashboard 在项目中自动启动。
最终代码如下：

```xml
<component name="RunDashboard">
 <option name="ruleStates">
     <list>
       <RuleState>
           <option name="name" value="ConfigurationTypeDashboardGroupingRule" />
       </RuleState>
       <RuleState>
          <option name="name" value="StatusDashboardGroupingRule" />
       </RuleState>
     </list>
  </option>
  <option name="contentProportion" value="0.22874807" />
  <option name="configurationTypes">
     <set>
          <option value="SpringBootApplicationConfigurationType" />
     </set>
  </option>
 </component>
```