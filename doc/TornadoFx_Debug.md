插件调试

[下载openjfx sdk](https://gluonhq.com/products/javafx/)

[网盘下载 提取码：3u3s](https://leon.lanzouw.com/b0d9j93cb)
编辑运行配置,VM 参数新增

```
--module-path \javafx路径\lib --add-modules=javafx.controls,javafx.fxml   
```
sdk路径  D:\AndroidSdk\javafx-sdk-17.0.2,完整配置参如下

--module-path D:\AndroidSdk\javafx-sdk-17.0.2\lib --add-modules=javafx.controls,javafx.fxml  --add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.https=ALL-UNNAMED --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED 

启动脚本添加反射open

set JAVA_OPTS=--add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.https=ALL-UNNAMED --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED
