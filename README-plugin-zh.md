<p> <h1 align="center">ToolsFx 插件指南</h1></p>
<p align="center">
<a href="README-plugin.md">English</a>|<a href="README-plugin-zh.md">中文</a>
</p>
 <img width=0 height=0 src="https://profile-counter.glitch.me/Leon406/count.svg" alt="Leon406:: Visitor's Count" />
 <img width=0 height=0 src="https://profile-counter.glitch.me/Leon406_ToolsFx/count.svg" alt="ToolsFx:: Visitor's Count" />

------

目前同时支持两种机制

## 原理1 classloader

基于classloader 加载对应接口的实现的jar包, 这里同时用到 convention over configuration思想, jar包名称为对应**全类名**

应用启动会扫描**根目录 plugin文件**下的jar包,再通过反射实例化,最后显示

## 原理2 [spi(Service Provider Interface)](https://en.wikipedia.org/wiki/Service_provider_interface)

插件module **resource**目录新建 **META-INF/services** 文件夹, 新建文件名 为**插件接口的全类名**,内容为**接口实现的全类名**

宿主应用通过  ServiceLoader#load 实例化所有插件接口的实现的对象,最后显示

## 对比

| 机制        | 加载时机            | 命名                          |
| ----------- | ------------------- |-----------------------------|
| classloader | **按需加载**        | 严格以全类名.jar命名,可以放在指定插件目录     |
| spi         | 必须同主app一起加载 | **.jar结尾即可, lib目录或者指定默认加载** |



classloader目前遇到插件resource资源无法加载的问题,临时只能将plugin的资源放到了宿主

## 开发相关

参考 plugin-sample 或者 plugin-apipost

## 插件

### ApiPost

#### **支持方法**

![sym](/art/plugin/apipost_methods.gif)

#### **数据与请求头**

![sym](/art/plugin/apipost_post.gif)

#### **文件上传**

![sym](/art/plugin/apipost-upload.gif)



#### 内置变量及函数

使用{{}} 包裹, 同其他api测试工具

##### 变量

- uuid  40位uuid

  ```
  {{uuid}}
  ```

- uuid2  32位uuid,删除-

  ```
  {{uuid2}}
  ```

- timestamp  时间戳(毫秒)

  ```
  {{timestamp}}
  ```

- timestamp2  时间戳 (秒)

  ```
  {{timestamp2}}
  ```

##### 函数

目前只支持一个单级别,可以嵌套

- md5

  ```
  {{md5(ad123124)}}
  ```

- base64

  ```
  {{base64(ad123124)}}
  ```

- digest

  ```
  {{digest(SHA1,412312)}}
  ```

- binary

  ```
  {{binary(ad123124)}}
  ```

- uppercase

  ```
  {{uppercase({{digest(SHA1,123)}})}}
  ```

- lowercase

  ```
  {{lowercase(asdfAfsdf)}}
  ```

- datetime2Mills

  ```
  {{datetime2Mills(2022-01-01 10:00:00)}}
  ```

- date2Mills

  ```
  {{date2Mills(2022-01-01)}}
  ```



### Location (经纬度转换)

![location](/art/plugin/location.gif)

### Compression (压缩)

### 图片模块

- png 宽高修复
- 二进制01转图片
- 二进制01转二维码
- rgb色值 转图片
- base64转图片
- 图片转base64
- gif 拆分

插件下载 https://leon.lanzoub.com/b0d9w4cof 提取码：ax63