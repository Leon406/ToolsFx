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
# v1.12.2.beta02  
## feature:
- feat(app): 新增Html Entity/gray code/佛曰
- feat(plugin-apipost): 优化导出curl,文件识别
- feat(app): CTF算法新增 Bifid/Trifid/Beaufort, FourSquare, Gronsfeld, Porta,Handycode
- feat(app): 加入截屏ocr识别
- feat(app): 扩展a1z26,支持自定义字典
- feat(app): emoji substitute from emoji-aes
- feat(app): 优化文件识别& ctf体验
- feat(app): 优化morse解密
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
- fix(app): hash不支持空字符串
- fix(app): RSA拖入密钥事件重复响应
- fix(app): 乱码还原失败
- fix(plugin-apipost): 浏览器curl解析未删除转义字符
# v1.12.2.beta05  
## feature:  
- feat(app): 新增在线密码新佛曰/兽曰/熊曰,js密码rabbit/aaencode/jjencode
- feat(app): 新增更新内容提示
- feat(app): 优化pem解析
- feat(app): 新增公钥密码密钥文件解析格式cer,der
- feat(plugin-apipost): 加入自动美化,解码开关
- feat(app): 新增hill密码,支持2,3阶
- feat(app): 哈希/签名模块支持输入 hex/base64
- feat(app): MAC模块支持输入 hex/base64
## bug fix:  
- fix(app): PBE模块salted切换编码识别错误
