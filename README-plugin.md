<p>
    <h1 align="center">ToolsFx Plugin Guide</h1>
<p align="center">
<a href="README-plugin.md">English</a>|<a href="README-plugin-zh.md">中文</a>
</p>
 <img width=0 height=0 src="https://profile-counter.glitch.me/Leon406/count.svg" alt="Leon406:: Visitor's Count" />
</p>


------

Support two principles meanwhile

## Principle 1 classloader

Bases on java classloader, load implement of plugin jar, and also uses *convention over configuration* conception.

Jar File name muse be full quality  class name. When application starts, it will scan jar files under plugin directory,and instantces by reflection.

## Principle 2 [spi(Service Provider Interface)](https://en.wikipedia.org/wiki/Service_provider_interface)

Host application will instantces all interface implementations by  ServiceLoader#load, and pluign module resources directory must hava META-INF/services/{plugin interface full quality class name} file whose content is plugin interface implementation class full quality name

## Compare

| Principle   | Load Time     | Naming                                                       |
| ----------- | ------------- | ------------------------------------------------------------ |
| classloader | if needed     | jar file  strictly naming with full quality class name of impl |
| spi         | with host app | jar file is ok                                               |



**problem**:   when plugin app is a plugin, it can't access its own resource 

## Develop Related

see **plugin-sample**  or  *plugin-apipost*

