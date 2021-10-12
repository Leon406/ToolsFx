<p>
    <h1 align="center">ToolsFx 插件指南</h1>
<p align="center">
<a href="README-plugin.md">English</a>|<a href="README-plugin-zh.md">中文</a>
</p>
 <img width=0 height=0 src="https://profile-counter.glitch.me/Leon406/count.svg" alt="Leon406:: Visitor's Count" />
</p>

------

目前同时支持两种机制

## 原理1 classloader

基于classloader 加载对应接口的实现的jar包, 这里同时用到 convention over configuration思想, jar包名称为对应**全类名**

应用启动会扫描**根目录 plugin文件**下的jar包,再通过反射实例化,最后显示

## 原理2 [spi(Service Provider Interface)](https://en.wikipedia.org/wiki/Service_provider_interface)

插件module **resource**目录新建 **META-INF/services** 文件夹, 新建文件名 为**插件接口的全类名**,内容为**接口实现的全类名**

宿主应用通过  ServiceLoader#load 实例化所有插件接口的实现的对象,最后显示

## 对比

| 机制        | 加载时机            | 命名                                      |
| ----------- | ------------------- | ----------------------------------------- |
| classloader | **按需加载**        | 严格已全类名.jar命名,可以放在指定插件目录 |
| spi         | 必须同主app一起加载 | **.jar结尾即可, lib目录或者指定默认加载** |



classloader目前遇到插件resource资源无法加载的问题,临时只能将plugin的资源放到了宿主

## 开发相关

参考 plugin-sample 或者 plugin-apipost