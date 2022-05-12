 <img width=0 height=0 src="https://profile-counter.glitch.me/Leon406/count.svg" alt="Leon406:: Visitor's Count" />
<img width=0 height=0 src="https://profile-counter.glitch.me/Leon406_ToolsFx/count.svg" alt="ToolsFx:: Visitor's Count" />

# v1.5.0  
## feature:  
- feat:二维码加入快捷键
- feat:美化非对称加密,签名,二维码样式
- feat:美化对称加密，哈希，MAC样式
- feat:编码及转换样式优化
- feat:编码及编码转换样式美化
- feat:增加二维码提示
- feat:签名与验签模块
- feat:二维码功能完善
- feat: qrcode 识别与生成,截屏识别demo
- feat: 对称加密支持编码集选择及拖动文件优化
## bug fix:  
- fix: toast 异常
- fix:编码转换 hex转base64错误
- fix:编码转换异常显示
# v1.5.1  
## feature:  
- feat:新增base58/base58check功能
- feat:二维码识别 新增剪贴板图片
## bug fix:  
- fix:编码转换urlencode错误及hash大文件按钮点击
- fix:快捷键冲突
# v1.6.0  
## feature:  
- feat: 检测升级功能
- feat: 新增关于模块
- feat: 编码/转换新增 base32/base62/base85/base91 功能
- feat: 更换复制文字为图片
- feat:对称加密文件加密 文件名称优化
- feat:rsa支持pk8 和pem文件加解密
- feat:调整复制按钮位置，新增剪贴板导入
- feat:加入app名字和版本配置
## bug fix:  
- fix: 时间小时格式
- fix: 底部提示默认不显示
- fix:base58/base58check部分字符解码错误 
# v1.7.0  
## feature:  
- feat: 新增字符处理模块
- feat: add new encode octal/decimal/escape
- feat: 增强hex/unicode解码
- feat: 右键置顶/语言选择功能
- feat: 编码base系列支持自定义字典
- feat: 新增base92
- feat: i18n 
- feat: 新增jre/vm信息显示, 置顶APP
- feat: UI美化
- feat: hash/对称加密支持多个文件操作

# v1.8.0  
## feature:  
- feat: 新增模块启用配置
- feat: 新增配置及加载网页功能
- feat: ctf 加入one time pad
- feat: add  classical bacon and polybius cipher
- feat: add module classical
- feat: 支持多次网络加载及优化检测升级
- feat: actions can be line by line
- feat: add PBE
- feat: add punycode & mac key, iv base64/hex format
- feat: encode transfer supports charset
- feat: encode supports charset
- feat: add xxEncode/uuEncode
- feat: add eventbus
- feat: add auto copy & string extract, adjust UI
- feat: add crc32,character statistic ,read http headers
## bug fix:
- fix: urlbase64  urlencode error
# v1.9.2.beta  
## feature:  
- feat: support custom client certification
- feat: add CONNECT request method
- feat: plugin support spi mode
- feat: api post 模块新增全局请求头,代理设置,页面优化
- feat: 新增crc64
- feat: 优化双通道升级检测
- feat: 网络请求模块
- feat: 加入拖动文件大小及后缀限制,避免未响应
- feat: 编码支持执行多次
- feat: add new cipher nihilist
- feat: 新增playfair cipher
- feat: 新增cipher ADFGX/ADFGVX/Autokey
- feat: add W type rail fence
- feat: add brainfuck/ook/troll script decrypt
- feat: add 社会主义核心价值价值观/栅栏加密
- feat: 新增base100编码
- feat: 新增qwe加密
## bug fix:  
- fix: request header parse error
- fix: patch方法错误
- fix: encode QuotePrintable capital letters decode error
- fix: autoKey keyword小写加密不一致
- fix: unicode解码支持大写U
 
# v1.10.0  
## feature:  
- feat(app): 字符处理 乱码还原功能
- feat: new module plugin-location
- feat(app): 非对称加密支持编码选择
- feat(app): 加密可以选择输入编码
- feat(app): add xorbase
- feat(plugin-apipost): release 1.1.2.beta
- feat(plugin-apipost): 新增内置变量
- feat(plugin-apipost): optimize import curl
- feat(plugin-apipost): 优化table输入
- feat(plugin-apipost): 提升易用性,跳转UI
- feat: optimize qrcode module & update input text length at real time
## bug fix:  
- fix(plugin-apipost): 优化json格式化
- fix(plugin-apipost): json格式化网址会添加空格
- fix(plugin-apipost): 导入部分curl解析错误
- fix(app): #18
- fix(plugin-apipost): curl --data-binary 解析错误
- fix(app): webview显示乱码
- fix(plugin-apipost): form-data 加入默认请求头form-data url encode
- fix(plugin-apipost): 文件上传后,文件占用
# v1.11.0  
## feature:  
- feat(app): 优化RSA加解密,非标准格式解析
- feat(app): 优化批量文件加解密,在子目录生成新文件
- feat(app): qrcode新增多文件及拖动图片识别
- feat(plugin-apipost): support response unicode decode
- feat(app): add ctf BubbleBabble and  unicode mix decode
- feat(app): add 01248 &alphabet index
- feat(app): add baudot cipher
- feat(app): 新增盲文
- feat(app): 新增jshex,jsOctal
- feat(app): 优化ook解密
- feat(app): 扩展morse加密解密
- feat(apipost): add datetime2Mills date2Mills function
## bug fix:  
- fix(app): morse H与&符号编解码错误
- fix(app): unicode编码为十进制
- fix(app): 编码互转支持逐行操作
- fix(apipost): post请求日志未显示 body信息
# v1.12.3  
## feature:  
- feat(app): 大数运算新增阶乘,质数阶乘,下一个质数功能
- feat(app): 新增RSA生成公私钥,私钥还原公钥
- feat(app): 新增多个java BigInteger支持的运算
- feat(app):add bigInt calculation module
- feat(app): qrcode支持一图多码识别
- feat(app): 新增adler32,XXTEA算法
- feat(app): 新增新窗口打开
- feat(app): MAC新增ZUC算法,IDEAMAC,DESCMAC,签名新增RSASA-PSS相关算法
- feat(app): hash模块支持常用密码hash,支持多次hash,新增执行时间显示
- feat(app): encode add radix64
- feat(app): add stream cipher ChaCha20,ChaCha20-Poly1305
- feat(app): 新增base45
- feat(app): 新增在线密码新佛曰/兽曰/熊曰,js密码rabbit/aaencode/jjencode
- feat(app): 新增更新内容提示
- feat(app): 优化pem解析
- feat(app): 新增公钥密码密钥文件解析格式cer,der
- feat(plugin-apipost): 加入自动美化,解码开关
- feat(app): hill密码支持2阶
- feat(app): 哈希/签名模块支持输入 hex/base64
- feat(app): MAC模块支持输入 hex/base64
- feat(app): 新增佛曰
- feat(app): 新增gray code
- feat(app): 编码新增 Html Entity
- feat(plugin-apipost): 优化导出curl,文件识别
- feat(app): CTF新增Bifid/Trifid
- feat(app): CTF算法新增 Beaufort, FourSquare, Gronsfeld, Porta,Handycode
- feat(app): 加入截屏ocr识别
- feat(app): 扩展a1z26,支持自定义字典
- feat(app): emoji substitute from emoji-aes
- feat(app): 优化文件识别& ctf体验
- feat(app): 优化morse解密
- feat(app): ctf 字母索引a1z26优化
- feat(app): 关于加入github issue反馈入口
- feat(app): 新增曲路和当铺密码
- feat(app): ctf加入参数提示,编码算法排序
- feat(app): 新增元素周期表加解密
- feat(app): PBE优化
- feat(app): 新增插件下载链接
- feat(app): 新增压缩模块
- feat(app): 新增零宽字符
- feat(plugin-apipost): 支持请求导出为curl请求
## bug fix:  
- fix(app): jsHex/jsOctal编码解码非unicode错误
- fix(apipost): json formatting doesn't work
- fix(app): PBE模块salted切换编码识别错误
- fix(app): hash不支持空字符串
- fix(app): RSA拖入密钥事件重复响应
- fix(app): 乱码还原失败
- fix(plugin-apipost): 浏览器curl解析未删除转义字符

# v1.13.0  
## feature:  
- feat(app): add radix8, radix10, radix32
- feat(app): 内嵌浏览器支持系统浏览器打开,美化UI
- feat(app): 新增hash字典爆破,可配置多个字典
- feat(app): 编码支持一键爆破/hash支持base64编码
- feat(app): TextField限制数字输入
- feat(app): add ECC calculator
- feat(app): signature support output hex signed data
- feat(app): 提示信息新增输入输出长度显示
- feat(app): support input AEAD associated data
- feat(app): 签名支持hex密钥,RSA签名额外支持n e d参数
- feat: RSA密钥支持hex/base64/n e d参数自动识别
- feat: SM2非对称加密结果模式改为国标C1C3C2,支持密钥Q,D加解密
- feat(app): RSA支持padding选择,新增Skein-256-128
- feat(app): 非对称加密算法位数选择
- feat: morse支持自定义符号
- feat: 新增SM2,ElGamal
- feat(app): 对称加密新增VMPC-KSA3/Shacal2/GOST28147/GOST3412-2015/Noekeon
- feat(app): 新增bc 1.71 hash/mac算法
- feat(app): 字符处理新增缩进功能
- feat(app): 对称加密模块新增异或加密,ctf模块新增 ascii sum
- feat(app): 优化rail fence W算法,支持偏移
- feat(app): #39 解析bc已支持x509相关证书
- feat(app): 大数模块,增强参数可读性及新增指定长度随机素数
## bug fix:  
- fix(app): pcMoe x-token过期
- fix(app): radix64 decode issue
- fix(app): radix多余leading zero
- fix(plugin-apipost): query parameter parse error
- fix(app): 大数开根精度丢失
- fix(app): 正式版升级提示
- fix(app): base64Url编码包含=
