 <img width=0 height=0 src="https://profile-counter.glitch.me/Leon406/count.svg" alt="Leon406:: Visitor's Count" />
<img width=0 height=0 src="https://profile-counter.glitch.me/Leon406_ToolsFx/count.svg" alt="ToolsFx:: Visitor's Count" />

# v1.18.0
## feature:
- feat(app): misc github 新增地址转raw直链，直链转浏览地址，更新镜像
- feat(app): misc shorturl remove unavailable type
- feat(app): misc add variable naming
- feat(app): Misc 更新翻译镜像服务器
- feat(app): key base64自动解析移除 pem header 和 footer #248
- feat(app): 非对称加密/签名支持key,der,pk8后缀 文本解析
- feat(app): 非对称加密新增公私钥指数模模解析
- feat(app): 非对称加密中秘钥可带到签名与验签模块
- feat(app): File Magic Number add heic, and fix bug
- feat(app): Misc uuid可选删除连接符
- feat(app): Misc 新增世界语言编码匹配
- feat(location): 加入amap签名校验, 1.3.1
- feat(app): misc 新增世界货币
- feat(app): misc 新增中英文标点转换
- feat(app): misc link check add type option
- feat(plugin-location): 新增天地图及逆地理编码,支持环境变量配置key
- feat(app): .class magics support jdk 20-27
- feat(app): misc add China car number
- feat(app): remove mit info
- feat(app): Misc githubMirror support raw.github.com
## bug fix:
- fix(plugin-compress): Lzstring base64 decoding padding处理 #252
- feat(app): File Magic Number add heic, and fix bug
- fix(app): 哈希算法，base64,hex编码迭代计算错误 #220
- fix(app): radix8 dict错误

# v1.17.0  

## feature:
- feat(app): KeyIvInputView add tooltip
- feat(app): Misc Tcping add output options
- feat(app): ChaCha20-Poly1305 support AAD
- feat(app): KeyIvInputView support auto sizing
- feat(app): output textarea support resizing
- feat(app): Misc add Endia
- feat(app): Misc 加入MIME解析
- feat(app): adjust misc prefColumns to 7
- feat(app): Misc add code mapping
- feat(app): 哈希加入各种CRC算法 #139
- feat(app): ocr support auto detect language
- feat(app): Misc Unit Convert add speed
- feat(app): Misc add shorten url
- feat(app): optimize normalCharacter
- feat(app): StringProcess optimize tts
- feat(app): Misc combine port scan & ping/tcping support ipv6
- feat(app): StringProcess tokenize support variant character
- feat(plugin-apipost): 移除默认请求头,限制最大显示内容为1M
- feat(app): StringProcessView Vocabulary add syllable
- feat(app): Misc新增繁简体转换
- feat(app): Misc add origin Google Translate, add translation strategy
- feat(app): Misc DNS hosts add fail urls cloudfare attempt
- feat(app): Misc ping add output options
- feat(app): StringProcessView add locating word position
- feat(app): Misc add Unit Conversion
- feat(app): adjust ocr
- feat(app): TTS设置支持方向盘拖动微调及显示
- feat(app): TTS设置保存后不自动关闭
- feat(app): 默认不置顶显示
- feat(app): 加入句子在线翻译功能
- feat(app): tts分段请求并发
- feat(app): 加入tts功能,支持配置
- feat(app): update dictionary.conf
- feat(app): StringProcessView 词典配置文件支持目录
- feat(app): StringProcessView 加入默认文件排除
- feat(app): OnlineWebView support loading html
- feat(app): optimize StringProcessView
- feat(app): StringProcessView 优化tokenize条件
- feat(app): StringProcess 加入自定义字典翻译
- feat(app): StringProcess 写入文件及Ctrl+D 光标行复制
- feat(app): StringProcess 优化Webview加载过度显示
- feat(app): StringProcess token优化,增加列表显示,查看在线翻译,加入快捷键
- feat(app): StringProcess 新增忽略大小写
- feat(app): StringProcess addition小写自动转换
- feat(app): StringProcess addition操作支持多文件
- feat(app): 字符处理 差集/并集/合集更新底部提示
- feat(app): 字符处理 加入两个数据源 差集/并集/合集
- feat(plugin-apipost): jsonpath raw string不处理
- feat(plugin-apipost): 支持简化jsonpath提取
- feat(app): 优化webview中文字体显示
- feat(app): Misc githubMirror support release url
- feat(app): Misc加入中日韩罗马音标注
- feat(app): Misc支持更多参数和选项
- feat(app): CTF与佛论禅新增加密版本, 优化零宽显示结果
- feat(app): Misc新增罗马数字转换,修复换行字符转义符号替换
- feat(app): Misc add encoding recovery, full/half width char
- feat(app): Misc 新增链接校验,github镜像链接
- feat(app): CTF 新增base64大小写 crack, hacker words
- feat(app): CTF zwcUnicode 新增binary算法
- feat(plugin-image): fixPng support correct magic number
- feat(app): Misc add cron explain
- feat(app): Misc加入 DNS host解析
- feat(app): add minimize to system tray
- feat(plugin-image): add load log

## bug fix:
- fix: Hash Crack Dict read error
- fix(app): Misc ICP备案失效
- fix(app): QuotePrintable decode error
- fix(app): Block Cipher padding alg error #146
- fix(app): Misc ping ipv6 error
- fix(app): RSA pubDecrypt OAEP error
- fix(app): StringProcessView default vocabulary files duplicate
- fix(app): google translation support long sentences
- fix(app): Translator initialization jam issue
- fix(app): CTF autokey 包含小写无法正常加解密
- fix(app): tts部分符号无法正常播放
- fix(app): TTS 异常字符处理
- fix(app): Encode HtmlEntity 编码错误,多余"#"
- fix(app): StringProcessView tokenize 重复
- fix(app):  导包错误, 兼容jdk8
- fix(app): base64 字典包含padding字符解码错误
- fix(app): MISC ip地址修复
- feat(plugin-image): fixPng support correct magic number
- fix(app): Signature JWT未输入验证内容错误



# v1.16.0
## feature:
- feat(app): CTF RSA-crack nec模式优化
- feat(app): 非对称加密/签名支持同时显示公私钥
- feat(app): jwt add ECDSA algorithm
- feat(app): Signature add jwt
- feat(app): Misc add ip2Int,ipLocation,cidr,int2Ip
- feat(app): misc add whois & icp
- feat(app): CTF Image新增wakanda,编码 Unicode支持 js utf-16解码
- feat(app): CTF 新增 Citrix CTX1
- feat(app): Hash 新增 mysql(oldpassword)  mysql5(password), md5-middle
- feat(app): CTF add Type7
- feat(plugin-image): 支持hex转图片,另存为图片
- feat(app): misc模块新增ping tcping,提示
- feat(app): 新增misc模块
- feat(app): CTF RSA-crack支持 多因子费马分解, 修复fullFermat错误后停止分解
- feat(app): 编码加入输入选项限制,只能选一个
- feat(app): 字符处理,支持转义
- feat(app): QrcodeView多文件识别修复及简化交互
- feat(plugin-image): 加入自动识别文件路径和base64, 加入图片缩放,支持图片上移
- feat(plugin-image): 新增形态学,图片转01,图片处理功能
- feat(plugin-apipost): 新增base64File方法
- feat(plugin-apipost): Post form-data 参数自动urlencode
- feat(plugin-image): fix png支持宽高同时修改
- feat(plugin-image): 新增微信data图片解密
- feat(app): SymmetricCrypto 底部提示新增key/iv长度
- feat(app): 文件模式 显示非文本文件扩展
- feat(app): SymmetricCrypto key iv编码支持自动转换
- feat(app): SymmetricCrypto新增 Tnepres
- feat(app): 编码支持文件加解密
- feat(plugin-image): 集成StegSolve
- feat(app): BigInt新增排列和组合
- feat(plugin-image): add gif split
- feat(app): CTF RSA-crack 支持d c e nbits参数
- feat(plugin-image): 新增图片模块
- feat(plugin-sample): add sample2
- feat(app): Encode add 十进制转其他进制,需自定义字典
- feat(app): support custom scale
- feat(app): CTF add Symbol Cipher and rename
- feat(app): Digest crack 支持 mask
- feat(app): 字符处理 分割支持正则
- feat(app): CTF zwcUnicode不设置字典可以自动解析
- feat(app): CTF优化 rsa-crack参数解析,过滤多余字符
- feat(app): 合并CTF类似功能算法
- feat(app): CTF base32 steg加解密
- feat(app): CTF base64 steg 支持加密
- feat(app): CTF 新增百家姓, base64隐写
- feat: crc add padding
- feat(plugin-apipost): 使用默认Dispatchers.IO
- feat(app): CTF RSA fermat分解支持完全分解
- feat(app): CTF新增zwc unicode
## bug fix:
- fix(plugin-image): 选项切换显示错误
- fix(app): 编码文件解密同名覆盖问题
- fix(app): js hex编码缺少补零
- fix(tools): 添加minjre缺少模块
- fix(app): 内嵌浏览器中文显示乱码
- fix(tools): minjre缺少模块
- fix(app): 功能名太长自动换行
- fix(plugin-apipost): 忽略证书验证功能错误
- feat(plugin-image): fix png支持宽高同时修改
- fix(app): 动态参数未生效
- fix(app): CTF base64 steg 解密错误
- fix(app): CTF图片编码 jar资源文件夹遍历错误


#  v1.15.0

## feature:  

- feat(app): CTF新增零宽morse
- feat(app): CTF 更多图片编码优化及新增图片
- feat(app): CTF RSA-Crack pqec模式 支持amm算法
- feat(app): CTF RSA-crack支持AMM
- feat: 编码新增输入翻转,crack上移内容优化
- feat(app): CTF brainfuck,ook,troll支持加密
- feat(plugin-apipost): 新增https忽略证书校验
- feat(plugin-apipost): 支持自动添加http://,优化json格式化,get请求参数拼接
- feat(app): CTF RSA-CRACK pqrec支持 e phi互素
- feat(app): Encode add base2048, base32768, ecoji
- feat(app): CTF add twin-hex cipher
- feat(app): 新增配置参数offlineMode, 隐藏联网功能
- feat(app): 字符处理模块,统计结果按照数量从高到低排序
- feat(location): 地心与经纬度坐标转换
- feat(app): CTF 新增 FracMorse & Fenham Cipher
- feat(app): 新增RadixN
- feat(app): Encoding add radix9
- feat(app): CTF新增图片加密算法查看
- feat(app): hash crack hash结果转小写
- feat(app): HASH新增 windows LM, NTLM
- feat(app): CTF新增 base60 (天干地支编码),简化radixN方法
- feat(app): CTF新增元音vowel 和 八卦加解密
- feat(app): ctf 新增manchester及差分相关功能
- feat: CTF rsa-crack  wiener支持n超过2个因子
- feat: hidpi支持配置
- feat: 禁用hidpi自动缩放
- feat(apipost): follow redirect默认false，可在设置配置
- feat(app): CTF 佛曰 支持魔曰翻转解密
- feat(app): CTF rsa-crack 解密结果不正常，不进行转字符串
- feat: 大数因式分解 最大迭代次数改为超时限制
- feat: 编码hex binary解码支持0x,0b头
- feat(app): 编码新增base69, base65536
- feat(app): 编码crack支持解密最小目标长度及不显示crack失败原始文本
- feat(app): 编码新增 hex_octal_binary混合编解码
- feat(app): 编码新增UTF-7编码

## bug fix:  

- fix(app): radixN解密错误, 修改hidip默认值为开启
- fix(app): 负数 circleIndex 错误
- fix(app): windows LM crack错误
- fix(app): RsaSolver#solveN2EC2
- fix(app): RsaSolver#solveN2EC2 解密错误
- fix(app): 修复curve cipher编码错误
- fix(app): CTF manchester 解码 00为 -1,改为 0
- fix(app): ascii解码分割正则错误
- fix(app): unicodeMix2String识别错误
- fix(app): rail fence crack范围错误，只取第一个结果的问题
- fix(app): ctf rsa-crack e phi不互素无法解密
- fix(app): 编码crack最小目标长度判断条件修改
- fix(app): unicode编码 #65
- fix(app): base91 base92 crack异常
- fix(app): base91 无法crack
- fix(app): UTF-7解码表错误
- fix(app): base91解码异常
- fix(app): playfair解码算法存在问题 #62

# v1.14.0  

## feature:

- feat(app): CTF caesar支持大小写不同偏移
- feat(app): 非对称加密模块,RSA新增,与python OAEP互通算法
- feat(app): CTF新增 阴阳怪气算法
- feat(app): 大数运算优化, 新增简单的RSA d及解密计算
- feat(app): 编码crack优化
- feat(app): 字符处理 提取支持文件
- feat(app): RSA-crack 支持8进制解析,广播支持大于3组数据
- feat(app): CTF新增 Rot8000, Caesar Box, Cetacean cipher
- feat(app): CTF add crack for rain fence,caeser,2x2 hill,curve cipher, affine cipher
- feat(app): PBE支持 Md5andRC4 Md5andTripleDes
- feat(app): update ToolsFx.properties, add sagecell
- feat(app): 内置浏览器支持websocket, ws wss
- feat(app): 调整样式及部分布局
- feat(app): 编码crack支持 文件模式(建议文件大于1M时使用)
- feat(app): CTF新增 tapCode敲击码
- feat(app): CTF,关于加入wiki入口
- feat(app): CTF bifid默认字典
- feat(app): RSA-crack新增 N2EC参数,优化参数解析
- feat(app): CTF playfair/ADGFX兼容包含空白符号的table
- feat(app): CTF playFair支持大小写还原
- feat(app): RSA-crack 新增n多因子解密 p q r r1 r2 rn
- feat(app): CTF新增 DNA加密解密
- feat(app): RSA-crack 加入本地质因分解算法 rho,p-1
- feat(app): RSA-crack支持低指数e=3 广播攻击
- feat(app): RSA-crack新增NEC e phi不互素算法
- feat(app): RSA-crack新增 wiener attack
- feat(app): 优化factorDb请求,RSA-crack支持更多模式
- feat(app): RSA-crack新增 ncd, nc1c2e1e2模式
- feat(app): 增强RSA-crack功能
- feat: #55
- feat(app): 新增字符处理 文件名替换
- feat(app): string process support remove space
- feat(app): 新增base85扩展Z85,Base64-IPv6
- feat(app): 一键解码操作优化
- feat(app): ctf模块新增RSA-NEC,RSA-PQEC 破解
- feat(app): atBash,affine,virgenene,qwe支持保留大小写
- feat(app): 新增SpringSecurity密码hash,支持crack
- feat: #50 rot13/rot18/caeser support case recovery
- feat(app): 优化编码crack编码结果,不显示无效编码结果

## bug fix:

- fix(app): base91特定长度最后一位解码错误
- fix(app): 去除NON_PRINTABLE换行符
- fix(app): CTF autoKey 提示语错误,修正兽音
- fix(app): QrCode截屏识别错误, 编码逐行功能无效
- fix(app): 编码一键crack不正确, 排除octal
- fix(app): RSA-crack 小指数开方不包含 c^(n/1)
- fix(app): RSA参数解析错误
- fix(app): 异常优化
- fix(app): virgenene 非字母偏移

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
- feat(app): 新增在线密码新佛曰/兽音/熊曰,js密码rabbit/aaencode/jjencode
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


# v1.5.1  

## feature:  

- feat:新增base58/base58check功能
- feat:二维码识别 新增剪贴板图片

## bug fix:  

- fix:编码转换urlencode错误及hash大文件按钮点击
- fix:快捷键冲突

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
