### 配置hosts

```
216.239.32.40 translate.google.com
216.239.32.40 translate.googleapis.com
```



如果ip失效,从以下ip筛选出可用ip,配合Misc模块 ping功能,再进行hosts配置

- 精简 IPv4：
  - 官方：https://unpkg.com/@hcfy/google-translate-ip/ips.txt
  - 备用1（ghproxy 镜像）：https://ghproxy.com/https://raw.githubusercontent.com/hcfyapp/google-translate-cn-ip/master/packages/google-translate-ip/ips.txt
  - 备用2（jsDelivr CDN）：https://cdn.jsdelivr.net/npm/@hcfy/google-translate-ip/ips.txt
- 扩展 IPv4：
  - 官方1：https://raw.githubusercontent.com/Ponderfly/GoogleTranslateIpCheck/master/src/GoogleTranslateIpCheck/GoogleTranslateIpCheck/ip.txt
  - 官方2：https://ghproxy.com/https://raw.githubusercontent.com/Ponderfly/GoogleTranslateIpCheck/master/src/GoogleTranslateIpCheck/GoogleTranslateIpCheck/ip.txt
  - 备用（GitCode 镜像）：https://gitcode.net/mirrors/Ponderfly/GoogleTranslateIpCheck/-/raw/master/src/GoogleTranslateIpCheck/GoogleTranslateIpCheck/ip.txt
- 标准 IPv6：
  - 官方1：https://raw.githubusercontent.com/Ponderfly/GoogleTranslateIpCheck/master/src/GoogleTranslateIpCheck/GoogleTranslateIpCheck/IPv6.txt
  - 官方2：https://ghproxy.com/https://raw.githubusercontent.com/Ponderfly/GoogleTranslateIpCheck/master/src/GoogleTranslateIpCheck/GoogleTranslateIpCheck/IPv6.txt
  - 备用（GitCode 镜像）：https://gitcode.net/mirrors/Ponderfly/GoogleTranslateIpCheck/-/raw/master/src/GoogleTranslateIpCheck/GoogleTranslateIpCheck/IPv6.txt



### 可以下载工具筛选配置

[GoogleTranslate_IPFinder](https://github.com/GoodCoder666/GoogleTranslate_IPFinder)