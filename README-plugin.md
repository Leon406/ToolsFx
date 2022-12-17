<p>
    <h1 align="center">ToolsFx Plugin Guide</h1></p>
<p align="center">
<a href="README-plugin.md">English</a>|<a href="README-plugin-zh.md">中文</a>
</p>
 <img width=0 height=0 src="https://profile-counter.glitch.me/Leon406/count.svg" alt="Leon406:: Visitor's Count" />
 <img width=0 height=0 src="https://profile-counter.glitch.me/Leon406_ToolsFx/count.svg" alt="ToolsFx:: Visitor's Count" />



------

Support two principles meanwhile

## Principle 1 classloader

Bases on java classloader, load implement of plugin jar, and also uses *convention over configuration* conception.

Jar File name muse be full quality  class name. When application starts, it will scan jar files under plugin directory,and instanced by reflection.

## Principle 2 [spi(Service Provider Interface)](https://en.wikipedia.org/wiki/Service_provider_interface)

Host application will instance all interface implementations by  ServiceLoader#load, and plugin module resources directory must hava META-INF/services/{plugin interface full quality class name} file whose content is plugin interface implementation class full quality name

## Compare

| Principle   | Load Time     | Naming                                                       |
| ----------- | ------------- | ------------------------------------------------------------ |
| classloader | if needed     | jar file  strictly naming with full quality class name of impl |
| spi         | with host app | jar file is ok                                               |



**problem**:   when plugin app is a plugin, it can't access its own resource 

## Develop Related

see **plugin-sample**  or  *plugin-apipost*



## Plugins

- ### ApiPost

  **support methods**

![sym](/art/plugin/apipost_methods.gif)

  **data and headers**

![sym](/art/plugin/apipost_post.gif)

  **file upload**

![sym](/art/plugin/apipost-upload.gif)

### Location (经纬度转换)

![location](/art/plugin/location.gif)

### Compression (压缩)

### Image Process

- png fix
- binary 01 to image
- binary 01 to qrcode
- rgb to image
- base64 to image
- image to base64
- gif split

plugin download https://leon.lanzoub.com/b0d9w4cof 提取码：ax63

  

