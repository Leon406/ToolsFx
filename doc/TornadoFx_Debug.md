插件调试

[下载openjfx sdk](https://gluonhq.com/products/javafx/)

编辑运行配置,VM 参数新增

```
--module-path \javafx路径\lib --add-modules=javafx.controls,javafx.fxml   
```

反射open

set JAVA_OPTS=--add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.https=ALL-UNNAMED
