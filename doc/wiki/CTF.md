CTF

## 01248

又称云影密码,使用 0，1，2，4，8 四个数字，其中 0 用来表示间隔，其他数字以加法

**特征**:   只有 0，1，2，4，8

**测试数据:** 

```
880810421
```

## a1z26

每个字母被替换成其在字母表中的编号

**特征**:  数字1-26  + 分隔符

**测试数据:** 

```
19-5-22-5-18-1-12 23-15-18-4-19 1-19 5-24-1-13-16-12-5
```

## aaencode

成颜文字网络表情的编码

**特征**: 表情符号

**测试数据**:

```
ﾟωﾟﾉ= /｀ｍ´）ﾉ ~┻━┻   //*´∇｀*/ ['_']; o=(ﾟｰﾟ)  =_=3; c=(ﾟΘﾟ) =(ﾟｰﾟ)-(ﾟｰﾟ); (ﾟДﾟ) =(ﾟΘﾟ)= (o^_^o)/ (o^_^o);(ﾟДﾟ)={ﾟΘﾟ: '_' ,ﾟωﾟﾉ : ((ﾟωﾟﾉ==3) +'_') [ﾟΘﾟ] ,ﾟｰﾟﾉ :(ﾟωﾟﾉ+ '_')[o^_^o -(ﾟΘﾟ)] ,ﾟДﾟﾉ:((ﾟｰﾟ==3) +'_')[ﾟｰﾟ] }; (ﾟДﾟ) [ﾟΘﾟ] =((ﾟωﾟﾉ==3) +'_') [c^_^o];(ﾟДﾟ) ['c'] = ((ﾟДﾟ)+'_') [ (ﾟｰﾟ)+(ﾟｰﾟ)-(ﾟΘﾟ) ];(ﾟДﾟ) ['o'] = ((ﾟДﾟ)+'_') [ﾟΘﾟ];(ﾟoﾟ)=(ﾟДﾟ) ['c']+(ﾟДﾟ) ['o']+(ﾟωﾟﾉ +'_')[ﾟΘﾟ]+ ((ﾟωﾟﾉ==3) +'_') [ﾟｰﾟ] + ((ﾟДﾟ) +'_') [(ﾟｰﾟ)+(ﾟｰﾟ)]+ ((ﾟｰﾟ==3) +'_') [ﾟΘﾟ]+((ﾟｰﾟ==3) +'_') [(ﾟｰﾟ) - (ﾟΘﾟ)]+(ﾟДﾟ) ['c']+((ﾟДﾟ)+'_') [(ﾟｰﾟ)+(ﾟｰﾟ)]+ (ﾟДﾟ) ['o']+((ﾟｰﾟ==3) +'_') [ﾟΘﾟ];(ﾟДﾟ) ['_'] =(o^_^o) [ﾟoﾟ] [ﾟoﾟ];(ﾟεﾟ)=((ﾟｰﾟ==3) +'_') [ﾟΘﾟ]+ (ﾟДﾟ) .ﾟДﾟﾉ+((ﾟДﾟ)+'_') [(ﾟｰﾟ) + (ﾟｰﾟ)]+((ﾟｰﾟ==3) +'_') [o^_^o -ﾟΘﾟ]+((ﾟｰﾟ==3) +'_') [ﾟΘﾟ]+ (ﾟωﾟﾉ +'_') [ﾟΘﾟ]; (ﾟｰﾟ)+=(ﾟΘﾟ); (ﾟДﾟ)[ﾟεﾟ]='\\'; (ﾟДﾟ).ﾟΘﾟﾉ=(ﾟДﾟ+ ﾟｰﾟ)[o^_^o -(ﾟΘﾟ)];(oﾟｰﾟo)=(ﾟωﾟﾉ +'_')[c^_^o];(ﾟДﾟ) [ﾟoﾟ]='\"';(ﾟДﾟ) ['_'] ( (ﾟДﾟ) ['_'] (ﾟεﾟ+(ﾟДﾟ)[ﾟoﾟ]+ (ﾟДﾟ)[ﾟεﾟ]+(ﾟΘﾟ)+ (ﾟｰﾟ)+ (ﾟΘﾟ)+ (ﾟДﾟ)[ﾟεﾟ]+(ﾟΘﾟ)+ (ﾟｰﾟ)+ (ﾟΘﾟ)+ (ﾟДﾟ)[ﾟεﾟ]+(ﾟΘﾟ)+ (ﾟｰﾟ)+ ((ﾟｰﾟ) + (ﾟΘﾟ))+ (ﾟДﾟ)[ﾟεﾟ]+(ﾟΘﾟ)+ ((ﾟｰﾟ) + (ﾟΘﾟ))+ ((o^_^o) +(o^_^o))+ (ﾟДﾟ)[ﾟεﾟ]+(ﾟΘﾟ)+ (ﾟｰﾟ)+ (o^_^o)+ (ﾟДﾟ)[ﾟεﾟ]+(ﾟΘﾟ)+ ((ﾟｰﾟ) + (ﾟΘﾟ))+ ((ﾟｰﾟ) + (o^_^o))+ (ﾟДﾟ)[ﾟεﾟ]+(ﾟΘﾟ)+ (ﾟｰﾟ)+ (ﾟｰﾟ)+ (ﾟДﾟ)[ﾟεﾟ]+(ﾟΘﾟ)+ (ﾟｰﾟ)+ ((ﾟｰﾟ) + (ﾟΘﾟ))+ (ﾟДﾟ)[ﾟoﾟ]) (ﾟΘﾟ)) ('_');
```

## [ADFGVX](https://zh.wikipedia.org/zh-cn/ADFGVX%E5%AF%86%E7%A2%BC)

一战使用栏块密码.  ADFGX 的增补版

**特征**: 只有ADFG**V**X

**测试数据**:

字典 btalpdhozkqfvsngjcuxmrewy

密钥 CARGO

```
FAGDXADDDFDFFFVAXAFAFADG
```

## ADFGX

一战使用栏块密码.

**特征**:  只有ADFGX

**测试数据**:

字典 btalpdhozkqfvsngjcuxmrewy

密钥 CARGO

```
FAXDF ADDDG DGFFF AFAXX AFAFX
```

## [affine](https://zh.wikipedia.org/wiki/%E4%BB%BF%E5%B0%84%E5%AF%86%E7%A2%BC)

仿射密码,它是一个字母对一个字母的。 y = ax + b

**特征**:  无, 看提示,flag还原

**测试数据**: 

y = 5x+8

```
IHHWVC SWFRCP
```

## asciiSum

ascii累计求和

**特征**: 数字，逐渐变大，超过128

**测试数据**:

```
0 97 212 311 416 524 632 664 779 896 1005 1037 1153 1254 1369 1485
```

## [atbash](https://zh.wikipedia.org/wiki/%E9%98%BF%E7%89%B9%E5%B7%B4%E5%B8%8C%E5%AF%86%E7%A2%BC)

埃特巴什码,最后一个字母代表第一个字母，倒数第二个字母代表第二个字母

**特征**:   无, 看提示

**测试数据**:

```
SVOOL
```

## [autoKey](https://zh.wikipedia.org/wiki/%E8%87%AA%E5%8A%A8%E5%AF%86%E9%92%A5%E5%AF%86%E7%A0%81)

自动密钥密码，密钥开头是一个关键词，之后则是明文的重复

**特征**: 

**测试数据**:

key: QUEENLY

```
QNXEPV YT WTWP
```

## [bacon24](https://zh.wikipedia.org/wiki/%E5%9F%B9%E6%A0%B9%E5%AF%86%E7%A2%BC)

培根密码， 字典大小24

**特征**: AB组合 + 分隔符

**测试数据**:

```
BAABAAABBBABAAABAAAB ABAAABAAAB AAAABAAAAAAAABAABBABABBAA 24
```

## [bacon26](https://zh.wikipedia.org/wiki/%E5%9F%B9%E6%A0%B9%E5%AF%86%E7%A2%BC)

培根密码， 字典大小26

**特征**: AB组合 + 分隔符

**测试数据**:

```
BAABBAABBBABAAABAABA ABAAABAABA AAAABAAAAAAAABAABBBAABBAB 26
```

## base64CaseCrack

base64大小爆破, 注意长度不能太长

**特征**: base64字符 + 见提示

**测试数据**:

```
AGVSBG8=
```

## 

## baudot

博多密码

**特征**: 五位01组成 + 分割符

**测试数据**:

```
11001 00011 00111 01001 11000 10000 00100 01110 00110 10110 10100 00001 01010
```

## [beaufort](https://zh.m.wikipedia.org/zh-hans/%E5%8D%9A%E7%A6%8F%E7%89%B9%E5%AF%86%E7%A0%81)

博福特密码,是一种类似于维吉尼亚密码的替代密码,加密算法与解密算法相同

**特征**: 无,见提示

**测试数据**:

key: FORTIFICATIONFORTIFICATIONFO

```
CKMPVCPVWPIWUJOGIUAPVWRIWUUK
```

## [bifid](https://en.wikipedia.org/wiki/Bifid_cipher)

二分密码, 双密码

**特征**: 密钥矩阵,和周期

**测试数据**:

key: ABCDEFGHIKLMNOPQRSTUVWXYZ

周期: 4

```
hbabiqsuto
```



## braille(盲文)

**特征**: 盲文符号

**测试数据**:

```
⡒⡂⡑⡙⡜⡜⡕⡙⡃⡖⡅⡞⡄⡟⡟=
```

## [brain fuck](https://zh.m.wikipedia.org/zh-hans/Brainfuck)

**特征**: 只有  <>.,+-[]  8个符号

**测试数据**:

```
++++++++++[>+++++++>++++++++++>+++>+<<<<-]
>++.>+.+++++++..+++.>++.<<+++++++++++++++.
>.+++.------.--------.>+.>.
```

## bubbleBabble

气泡加密

**特征**: 5个字母 + `-`分割, 最后一个字母是`x`

**测试数据**: 

```
ximil-hymuk-derak-habak-comyk-dorok-homok-sanek-zuruf-gosyf-koxix
```

## [caeser](https://en.wikipedia.org/wiki/Caesar_cipher)

凯撒位移密码

**特征**: 无,见提示

**测试数据**:

```
QEB NRFZH YOLTK CLU GRJMP LSBO QEB IXWV ALD
```

## caeser box

凯撒位移密码,明文字符之间移动

**特征**: 有参数高度

**测试数据**:

height: 3

```
Hlodeor!lWl
```

## cetacean

**特征**: 由`eE`组成,长度为16的倍数

**测试数据**:

```
EeeeEeEeEEeeEEEeEEEEEEEEEeeEEEEEEEEEEEEEEeeEEeEeEEEEEEEEEeEEEeEeEEEEEEEEEeeEEEEEEeeeeeeEeeEEEeEEEeeEEEeEEEEeEEEEEEEEEEEEEEeEeeEEeEEeEeEeEeeeeeeeEeEeeeeEeEeEEeeEEeEEeeeEEEeeeEeEEEEEEEEEEEeeEEEeEEEEEEEEEEeeEeeEEeeeEeeEeEEEEeEEEeEeEEEEEEEEeeEeEeeEEeEeEeeeEEEE
```

## 

## [curveCipher](https://cryptowikis.com/ClassicalCipher/TranspositionCiphers/RouteCipher/)

曲路密码

**特征**: 无, 见提示(需要行列数)

**测试数据**:

5行7列

```
gesfc inpho dtmwu qoury zejre hbxva lookT
```

## DNA

**特征**: 由碱基序列AGCT组成

**测试数据**:

```
AAT ATC AAA TTG AAG AGA ATT ACT ACA CAC
```

## [emojiSubstitute](https://aghorler.github.io/emoji-aes/)

emojiAES 替换算法

**特征**: 由表情Unicode组成

**测试数据**:

```
🙃💵🌿🎤🚪🌏🐎🥋🚫😆✅⏩😎🌉💧😂📂🎤🖐🙃🚪😀🥋🔬🎅😆🌉☂☃🚫📂ℹ🕹👉🍍👑🌊🍍🍵🍌ℹ📮📮🎃👣ℹ✖🌊🐎ℹ⏩ℹ🔪☀✉🔪😡😡😎🚪🐅ℹ🎈👑
```

提示: 解密结果搭配 PBE模块AES-256解密再次解密

## [FenHam费娜姆密码](https://blog.csdn.net/sinat_38235368/article/details/78262148)

**特征**: 01组成,见提示

**测试数据**:

key: crude

```
00010110010111001100100010000001010
```

## [fourSquare](http://practicalcryptography.com/ciphers/four-square-cipher/)

四方密码

**特征**: 无,见提示(有两个长度为25的key)

**测试数据**:

key1: zgptfoihmuwdrcnykeqaxvsbl

key2: mfnbdcrhsaxyogvituewlqzkp

```
TIYBFHTIZBSY
```

## [FracMorse](https://programtalk.com/vs2/?source=python/5968/pycipher/pycipher/fracmorse.py)

**特征**: key长度为26,见提示

**测试数据**:

key: ROUNDTABLECFGHIJKMPQSVWXYZ

```
OAFTCBKHJFRSDIRPFOV
```

## 

## [grayCode](https://zh.m.wikipedia.org/zh/%E6%A0%BC%E9%9B%B7%E7%A0%81)

格雷码

**特征**: 由0,1组成, 见提示

**测试数据**:

```
0101110001010111110110100101101001011000000101110110001011000000100101110100100001011001000101110110110001110111000101110100011101101100000101000111100111101001
```

## gronsfeld

格罗斯费尔德密码,与维吉尼亚类似

**特征**: 无,见提示

**测试数据**:

key: 45329

```
HJIGWHYKGNEXWYJPQRHCLJFCBXQH
```



## handyCode

**特征**: 相同数字+分隔符组成

**测试数据**:

```
9 44 33 777 33 444 7777 333 555 2 4
```

## [hill](https://zh.m.wikipedia.org/wiki/%E5%B8%8C%E5%B0%94%E5%AF%86%E7%A0%81)

希尔密码

**特征**: 有加密矩阵

**测试数据**:

密钥(偏移0) cefjcbdrh

```
pfogoanpgzbn
```

密钥(偏移1) 1 2 0 1

```
dloguszijluswogany
```

## jjencode

**特征**: 特殊符号组成,大部分为$

**测试数据**:

```
$=~[];$={___:++$,$$$$:(![]+"")[$],__$:++$,$_$_:(![]+"")[$],_$_:++$,$_$$:({}+"")[$],$$_$:($[$]+"")[$],_$$:++$,$$$_:(!""+"")[$],$__:++$,$_$:++$,$$__:({}+"")[$],$$_:++$,$$$:++$,$___:++$,$__$:++$};$.$_=($.$_=$+"")[$.$_$]+($._$=$.$_[$.__$])+($.$$=($.$+"")[$.__$])+((!$)+"")[$._$$]+($.__=$.$_[$.$$_])+($.$=(!""+"")[$.__$])+($._=(!""+"")[$._$_])+$.$_[$.$_$]+$.__+$._$+$.$;$.$$=$.$+(!""+"")[$._$$]+$.__+$._+$.$+$.$$;$.$=($.___)[$.$_][$.$_];$.$($.$($.$$+"\""+"\\"+$.__$+$.__$+$.___+$.$$$_+(![]+"")[$._$_]+(![]+"")[$._$_]+$._$+"\\"+$.__$+$._$_+"\\"+$.__$+$.__$+"\\"+$.__$+$.__$+"\\"+$.__$+$.__$+"\"")())();
```

## [Manchester](https://upload-images.jianshu.io/upload_images/7648905-6c4a8341a9f08b3e.png?imageMogr2/auto-orient/strip|imageView2/2/w/1007/format/webp)

通信编码,曼切斯特编码

**特征**: 01组成,见提示

**测试数据**:

非标准,8位反转

```
5555555595555A65556AA696AA6666666955
```

标准

```
0x2559659965656A9A65656996696965A6695669A9695A699569666A5A6A6569666A59695A69AA696569666AA6
```



## [Manchester-diff](https://upload-images.jianshu.io/upload_images/7648905-6c4a8341a9f08b3e.png?imageMogr2/auto-orient/strip|imageView2/2/w/1007/format/webp)

通信编码,差分曼切斯特编码

**特征**: 01组成,见提示

**测试数据**:

```
1010011010100101
```

结果为: 10110010

## [morse](https://zh.wikipedia.org/wiki/%E6%91%A9%E5%B0%94%E6%96%AF%E7%94%B5%E7%A0%81)

摩尔斯电码

**特征**: 由 `.` `-` 分隔符组成

**测试数据**: 

```
-- --- .-. ... . -.-. --- -.. .
```

## [nihilist](https://en.wikipedia.org/wiki/Nihilist_cipher)

**特征**: 数字 +分割符号

**测试数据**:

key：helloworld

```
33 2335 13121441 4511234134 541451 311421 5144334132 3554 4414314515232112
```

## [oneTimePad](https://zh.wikipedia.org/wiki/%E4%B8%80%E6%AC%A1%E6%80%A7%E5%AF%86%E7%A2%BC%E6%9C%AC)

一次性密码本

**特征**: 密钥跟明文长度一样

**测试数据**:

密钥: MASKL NSFLD FKJPQ

```
FHACTFSSPAFWYAU
```

## Ook

brain fuck变种

**特征**:  每一段都包含Ook

**测试数据**:

```
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook. Ook. Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook.
Ook. Ook? Ook. Ook? Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook! Ook. Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook. Ook? Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook! Ook! Ook! Ook!
Ook! Ook! Ook? Ook. Ook? Ook! Ook. Ook? Ook! Ook! Ook! Ook! Ook! Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook. Ook? Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook? Ook. Ook? Ook! Ook. Ook? Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook.
Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook! Ook! Ook! Ook! Ook! Ook! Ook? Ook.
Ook? Ook! Ook. Ook? Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook. Ook! Ook! Ook!
Ook! Ook! Ook! Ook! Ook! Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook!
Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook? Ook.
Ook? Ook! Ook. Ook? Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook!
Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook!
Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook.
Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook? Ook. Ook? Ook! Ook. Ook? Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook. Ook? Ook. Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook. Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook? Ook. Ook? Ook! Ook. Ook? Ook. Ook. Ook! Ook.
Ook? Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook!
Ook! Ook! Ook! Ook! Ook! Ook? Ook. Ook? Ook! Ook. Ook? Ook! Ook! Ook! Ook!
Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook?
Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook?
Ook. Ook? Ook! Ook. Ook? Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook!
Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook.
Ook. Ook! Ook. Ook. Ook. Ook! Ook. Ook. Ook. Ook! Ook. Ook. Ook. Ook! Ook.
Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook!
Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook.
Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook? Ook. Ook? Ook! Ook. Ook? Ook. Ook.
Ook. Ook. Ook. Ook. Ook! Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook.
Ook. Ook. Ook! Ook. Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook. Ook.
Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook. Ook? Ook. Ook. Ook.
Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook.
Ook. Ook? Ook. Ook? Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook.
Ook. Ook. Ook. Ook. Ook! Ook. Ook? Ook.
```



## [pawnShop](https://baike.baidu.com/item/%E5%BD%93%E9%93%BA%E5%AF%86%E7%A0%81/19848621)

当铺密码

**特征**: 中文

**测试数据**:

```
王夫 井工 夫口 由中人 井中 夫夫 由中大
```

## periodicTable

元素周期表

**特征**: 元素符号 + 分隔符

**测试数据**:

```
No Hs Bk Lr Db Uup Lr Rg Rg Fm
```

## [playFair](https://en.wikipedia.org/wiki/Playfair_cipher)

**特征**: 无,见提示

**测试数据**:

密码: playfairexample

```
BMODZBXDNABEKUDMUIMXMOVUIFYE
```

## [polybius](https://en.wikipedia.org/wiki/Polybius_square)

波利比乌斯, 棋盘密码

**特征**: 数字1-5组成

**测试数据**:

```
442315 4145241325 1242345233 213453 2445323543 34511542 442315 31115554 143422
```

## [porta](http://www.practicalcryptography.com/ciphers/classical-era/porta/)

一种多表替换密码

**特征**: 无,见提示

**测试数据**:

key: FORTIFICATION

```
synnjscvrnrlahutukucvryrlany
```

## qwe

键盘密码, 字典为键盘顺序

**特征**: 无,见提示

**测试数据**:

```
ITSSGSTGF
```

## rabbit

兔子流密码

**特征**: U2FsdGVkX1开头,此为openssl加密的格式,见提示

**测试数据**:

```
U2FsdGVkX1/OUupd4hTYG1kXn8p6QA==
```

## railFence

围栏密码,普通

**特征**: 无,见提示

**测试数据**:

栏数: 5

```
AKWTANTTADCA
```

## railFenceW

围栏密码 W型

**特征**: 无,见提示

**测试数据**:

栏数: 7

```
fklneaengh1{coc-oc}
```

## rot13

位移密码,加密和解密相同, caeser密码, 字母偏移13

**特征**: 无,见提示

**测试数据**:

```
EBG5/13/18/47 VF GUR RNFVRFG NAQ LRG CBJRESHY PVCURE!
```

## rot18

rot5 + rot13, 数字字母都偏移

**特征**: 无,见提示

**测试数据**:

```
ebg0/68/63/92 vf gur rnfvrfg naq lrg cbjreshy pvcure!
```

## rot47

对数字、字母、常用符号进行编码,偏移47

**特征**: 无,见提示

**测试数据**:

```
#~%d^`b^`g^cf :D E96 62D:6DE 2?5 J6E A@H6C7F= 4:A96CP
```

## rot5

位移密码, 所有数字偏移5

**特征**: 无,见提示

**测试数据**:

```
ROT0/68/63/92 is the easiest and yet powerful cipher!
```

## rot8000

位移密码, 0-65535中所有可见字符偏移一半

**特征**: 由汉字生僻字组成,见提示

**测试数据**:

```
籝籱籮 籚籾籲籬籴 籋类籸粀籷 籏籸粁 籓籾籶籹籮籭 籘籿籮类 籝籱籮 籕籪粃粂 籍籸籰簷
```



## RSA-crack

RSA解密一把梭

**特征**: RSA题型, 大整数

**测试数据**:

见 [rsa](https://github.com/Leon406/ToolsFx/tree/dev/testdata/ctf/rsa)

## socialCoreValue

社会主义核心价值观

**特征**: 由富强、民主、文明、和谐，自由、平等、公正、法治，爱国、敬业、诚信、友善组成

**测试数据**:

```
公正爱国公正平等公正诚信文明公正诚信文明公正诚信平等友善爱国平等诚信民主诚信文明爱国富强友善爱国平等爱国诚信平等敬业民主诚信自由平等友善平等法治诚信富强平等友善爱国平等爱国平等诚信民主法治诚信自由法治友善自由友善爱国友善平等民主
```

## steg base32

base32隐写, 与base64隐写类似

**特征**: 多行base32编码,行结尾有`=`

**测试数据**:

```
JVXXEYLMNF2HSIDNMF4SAY3PNZZWS43UEBZW63DFNR4SA2LOEB2GQZJAMNXXK4TBM5SSA33GEBWWC23JNZTSAYJAMNUG62LDMUXAU===
JBQXG5DFEB2HE2LQOMQG65TFOIQGS5DTEBXXO3RANBSWK3DTFYFM====
IEQHOYLUMNUGKZBAOBXXIIDOMV3GK4RAMJXWS3DTFYFM====
JBSSA53IN4QGG33NNVSW4Y3FOMQG2YLOPEQHI2DJNZTXGIDGNFXGS43IMVZSAYTVOQQGCIDGMV3S4CW=
KRUGK4TFEBUXGIDON4QHG5LDNAQHI2DJNZTSAYLTEBSGC4TLNZSXG4Z3EBXW43DZEBQSAZTBNFWHK4TFEB2G6IDTMVSS4CW=
IEQGI4TFONZSA2LTEBWGS23FEBQSAYTBOJRGKZBAMZSW4Y3FFYQES5BAOBZG65DFMN2HGIDUNBSSA4DSMVWWS43FOMQHO2LUNBXXK5BAOJSXG5DSNFRXI2LOM4QHI2DFEB3GSZLXFYFD====
K5UGK3RAOR3W7YUATBZSAY3PNVYGC3TZFQQHI2DSMVSSO4ZAORUGKIDSMVZXK3DUEEFA====
I5XWIIDNMFSGKIDSMVWGC5DJOZSXGOZAKRUGC3TLEBDW6ZBAO5SSAY3BNYQGG2DPN5ZWKIDPOVZCAZTSNFSW4ZDTFYFL====
EJLW64TLEBTGC43DNFXGC5DFOMQG2ZJOEIQESIDDMFXCA3DPN5VSAYLUEBUXIIDGN5ZCA2DPOVZHGIJABI======
EJEGC4TEEB3W64TLEBXGK5TFOIQGW2LMNRZSAYLOPFRG6ZDZFYRCAQTVOQQHO2DZEB2GC23FEB2GQZJAOJUXG2Z7BL======
KRUGK4TFEBZWQ33VNRSCAYTFEBQSAYTFOR2GK4RAO5QXSIDUN4QHG5DBOJ2CAYJAMRQXSIDUNBQW4IDXMFVWS3THEB2XAIDFOZSXE6JANVXXE3TJNZTS4CV=
INUGS3DEOJSW4IDJNYQGEYLDNNZWKYLUOMQGGYLVONSSAYLDMNUWIZLOORZS4ICBMNRWSZDFNZ2HGIDJNYQGEYLDNNZWKYLUOMQGGYLVONSSAY3INFWGI4TFNYXAV===
KN2WGY3FONZSA2LTEBQSA4TFNRQXI2LWMUQHIZLSNUXCASLUEBRHE2LOM5ZSA43PEBWWC3TZEBZGK3DBORUXMZLTFYFN====
IV3GK4TZEBWWC3RAONUG65LMMQQG2YLSOJ4S4ICBMZ2GK4RAMFWGYLBANBQXA4DJNZSXG4ZANFZSA3TPOQQHI2DFEBXW43DZEB2GQ2LOM4QGS3RANRUWMZJOBK======
J5XGKIDTNBXXK3DEEBWG65TFEBQW42LNMFWHGLRAKRUGK6JAMFZGKIDTN4QHIYLTOR4S4CW=
JVXW4ZLZEBUXGIDON52CAZLWMVZHS5DINFXGOLRAKRUGK4TFE5ZSATLBON2GK4TDMFZGIIBGEBLGS43BFYFB====
K5SSAY3BNYQGE33UNAQG6ZRAI5XWIIDBNZSCA5DIMUQGIZLWNFWC4CT=
I5XWIIDIMVWHA4ZAORUG643FEB3WQ3ZANBSWY4BAORUGK3LTMVWHMZLTFYFD====
KN2G64TNOMQG2YLLMUQHI4TFMVZSA5DBNNSSAZDFMVYGK4RAOJXW65DTFYFD====
KRUGKIDFMFZGY6JAMJUXEZBAMNQXIY3IMVZSA53POJWS4CR=
LFXXKIDOMV3GK4RANNXG65ZAPFXXK4RANR2WG2ZOBI======
KNXXOIDON52GQ2LOM4WCA4TFMFYCA3TPORUGS3THFYFD====
KRXSAYTFEBRG65DIEBQSA43QMVQWWZLSEBXWMIDXN5ZGI4ZAMFXGIIDBEBSG6ZLSEBXWMIDEMVSWI4ZOBI======
KRUGKIDXMVQWY5DIEBXWMIDUNBSSA3LJNZSCA2LTEB2GQZJAN5XGY6JAO5SWC3DUNAXAU===
JBSSA2LTEBZGSY3IEB2GQYLUEBUGC4ZAMZSXOIDXMFXHI4ZOBI======
K5XXE2ZAO5XW4J3UEBVWS3DMEBRHK5BAO5XXE4TZEB3WS3DMFYFM====
K5UGK3RAMFWGYIDFNRZWKIDJOMQGY33TOQQHI2DFEBTHK5DVOJSSA43UNFWGYIDSMVWWC2LOOMXAV===
JBSSA5DIMF2CA3LBNNSXGIDBEB2GQ2LOM4QHI33PEBTGS3TFFQQGE4TFMFVXGIDJOQXAU===
JF2CA2LTEBSWC43ZEB2G6IDCMUQHO2LTMUQGCZTUMVZCA5DIMUQGK5TFNZ2C4CU=
IEQG2YLOEBRWC3RHOQQHE2LEMUQHS33VOIQGEYLDNMQHK3TMMVZXGIDJOQQGS4ZAMJSW45BOBL======
KBXWY2LUMVXGK43TEBRW643UOMQG433UNBUW4ZZAMFXGIIDHMFUW44ZAMV3GK4TZORUGS3THFYFA====
I5ZGC5DJOR2WIZJANFZSA5DIMUQHG2LHNYQG6ZRANZXWE3DFEBZW65LMOMXAU===
JBXW4ZLTOR4SA2LTEB2GQZJAMJSXG5BAOBXWY2LDPEXAU===
INXXK4TUMVZXSIDJOMQHI2DFEBUW443FOBQXEYLCNRSSAY3PNVYGC3TJN5XCA33GEB3GS4TUOVSS4CX=
KBWGC2LOEBWGS5TJNZTSAYLOMQQGQ2LHNAQHI2DJNZVWS3THFYFC====
K5SWC3DUNAQGS4ZAORUGKIDUMVZXIIDPMYQGCIDNMFXCO4ZAMNUGC4TBMN2GK4ROBJ======
KRXSAYTFEBRG65DIEBQSA43QMVQWWZLSEBXWMIDXN5ZGI4ZAMFXGIIDBEBSG6ZLSEBXWMIDEMVSWI4ZOBK======
KRUGK4TFEBUXGIDON4QHE33ZMFWCA4TPMFSCA5DPEBWGKYLSNZUW4ZZOBJ======
KBSWCY3FEBRW63LFOMQGM4TPNUQHO2LUNBUW4LRAIRXSA3TPOQQHGZLFNMQGS5BAO5UXI2DPOV2CCCU=
KRUGK4TFEBUXGIDON4QGM2LSMUQGY2LLMUQGY5LTOQWCA3TPEBTXE2LQEBWGS23FEBUGC5DFHMQHI2DFOJSSA2LTEBXG6IDOMV2CA3DJNNSSAZDFNR2XG2LPNYWCA3TPEBZGS5TFOIQGY2LLMUQGG4TBOZUW4ZZOBL======
IJUXIZJAN5TGMIDNN5ZGKIDUNBQW4IDPNZSSAY3BNYQGG2DFO4XAV===
IFWGYIDEMVZWS4TFOMQGQYLWMUQGS3RAORUGK3JANRUXI5DMMUQHA3DFMFZWC3TUEB2GC43UMUQGE5LUEBZGC5DIMVZCA3LVMNUCA4DPORSW45DJMFWCA43VMZTGK4TJNZTS4CQ=
IZQWY43FEB3WS5DIEBXW4ZJAMNQW4IDCMUQGMYLMONSSA53JORUCA5DXN4XAU===
KRUGKIDTMVXHG2LCNRSSA3LBNYQGS4ZANZXXIIDJNZTGY5LFNZRWKZBAMJ4SA53IMF2CA33UNBSXEIDQMVXXA3DFEB2GQ2LONMXAU===
IRXSA3TPOQQGEZJANRSWIIDCPEQHEZLQN5ZHI4ZMEBXXEIDUOJQWI2LUNFXW4LBAN5ZCA2DFMFZHGYLZFYFM====
IJUXIZJAN5TGMIDNN5ZGKIDUNBQW4IDPNZSSAY3BNYQGG2DFO4XAV===
JVQXSIDBNRWCAYTFNFXGO4ZAMJSSA2DBOBYHSIDBNZSCA43FMN2XEZJAHMQG2YLZEB2GQZLJOIQG22LOMQQGEZJAMNXW45DFNZ2GKZBBBK======
JEQGGYLOEBRGKY3BOVZWKIDJEB2GQ2LONMQGSIDDMFXC4CT=
KRUGKIDXN5ZGYZBANFZSA2LOEBRW63TUNFXHK33VOMQGM3DVPAQGC3TEEBUXGIDJNVYGK4TNMFXGK3TUFYFH====
INUGC3THMUWCA2LNOBSXE3LBNZSW4Y3FEBUXGIDDNBQXEYLDORSXE2LTORUWGIDPMYQGY2LGMUXAU===
KRUGKIDBOR2GK3TEMFXHIIDTNBXXK3DEEBWWC23FEB2GQZJAOBQXI2LFNZ2CA2DBOBYHSIDBNZSCA2DPOBSWM5LMFYFG====
IJQWIIDMOVRWWIDPMZ2GK3RAMJZGS3THOMQGO33PMQQGY5LDNMXAV===
IZZG63JANVXW2ZLOOQQHI3ZANVXW2ZLOOQWCAYJAO5UXGZJANVQW4IDSMVWW65TFOMQGQ2LTEBXXO3RANFWXA5LSNF2GSZLTFQQGC4ZAMEQHG3LJORUCA4TFNVXXMZLTEB2GQZJAMRZG643TEBXWMIDTNFWHMZLSFYFE====
IEQGE33MMQQGC5DUMVWXA5BANFZSA2DBNRTCA43VMNRWK43TFYFM====
IRXSA33OMUQHI2DJNZTSAYLUEBQSA5DJNVSSYIDBNZSCAZDPEB3WK3DMFYFF====
KRUGC5BANVQW4IDJOMQHI2DFEBZGSY3IMVZXIIDXNBXXGZJAOBWGKYLTOVZGKIDBOJSSA5DIMUQGG2DFMFYGK43UFYFI====
JZSXMZLSEBUGS5BAMEQG2YLOEB3WQZLOEBUGKIDJOMQGI33XNYXAU===
IFWGYIDUNBSSA43QNRSW4ZDPOIQGS3RAORUGKIDXN5ZGYZBANFZSA3TPOQQHO33SORUCAYJAM5XW6ZBAMZZGSZLOMQXAV===
LFXXKIDDMFXCA5DFNRWCA5DIMUQGSZDFMFWHGIDPMYQGCIDOMF2GS33OEBRHSIDJORZSAYLEOZSXE5DJONSW2ZLOORZS4CT=
KNUGC4TQEB2G633MOMQG2YLLMUQGO33PMQQHO33SNMXAU===
KRZHKZJANVQXG5DFOJ4SA33GEBQW46JAONVWS3DMEB2GC23FOMQGCIDMNFTGK5DJNVSS4CR=
IZQWY43FEB3WS5DIEBXW4ZJAMNQW4IDCMUQGMYLMONSSA53JORUCA5DXN4XAV===
JRUWMZJAO5UXI2DPOV2CAYJAMZZGSZLOMQQGS4ZAMRSWC5DIFYFD====
KJUWG2DFOMQGI3ZANZXXIIDBNR3WC6LTEBRHE2LOM4QGQYLQOBUW4ZLTOMXAU===
JRUWWZJANNXG653TEBWGS23FFYFM====
INQXEZJAMJZGS3THOMQGO4TFPEQGQYLJOIXAV===
JRUWWZJAMZQXI2DFOIWCA3DJNNSSA43PNYXAV===
IZXXK4RAONUG64TUEB3W64TEOMQHG5LNEB2XAIDXNBQXIIDIMFZSA3DJMZ2GKZBANVXXG5BAON2WGY3FONZWM5LMEBUW4ZDJOZUWI5LBNRZSAYLCN53GKIDUNBSSAY3SN53WIORAMEQGY2LUORWGKIDCNF2CA3LPOJSS4CR=
IJSXI53FMVXCA5DIMUQGG5LQEBQW4ZBAORUGKIDMNFYCAYJANVXXE43FNQQG2YLZEBZWY2LQFYFE====
JNXG653MMVSGOZJANVQWWZLTEBUHK3LCNRSSYIDJM5XG64TBNZRWKIDNMFVWK4ZAOBZG65LEFYFO====
IJZGK5TJOR4SA2LTEB2GQZJAONXXK3BAN5TCA53JOQXAU===
IFXGOZLSEBRGKZ3JNZZSA53JORUCAZTPNRWHSLBAMFXGIIDFNZSHGIDJNYQHEZLQMVXHIYLOMNSS4CQ=
K5SSA43PN5XCAYTFNRUWK5TFEB3WQYLUEB3WKIDEMVZWS4TFFYFM====
KRUGKIDMN5XGOZLTOQQGIYLZEBUGC4ZAMFXCAZLOMQXCACS=
JBSSA53IN4QGG33NNVSW4Y3FOMQG2YLOPEQHI2DJNZTXGIDGNFXGS43IMVZSAYTVOQQGCIDGMV3S4CW=
IV3GK4TZEBUG64TTMUQHI2DJNZVXGIDJORZSA33XNYQHAYLDNMQGQZLBOZUWK43UFYFC====
JBXXOIDMOVRWW6JAJEQGC3JAORXSA2DBOZSSA43PNVSXI2DJNZTSA5DIMF2CA3LBNNSXGIDTMF4WS3THEBTW633EMJ4WKIDTN4QGQYLSMQXAU===
IEQHA33TNF2GS5TFEBQXI5DJOR2WIZJANVQXSIDON52CA5DINFXGWIDUNFWWKIDBNZSCAZLGMZXXE5BAONYGK3TUEBXW4IDUNBSSA3DJOR2GYZJAORUGS3THOMXAV===
IZQW2ZJANFZSAYJANVQWO3TJMZ4WS3THEBTWYYLTOMXAV===
JVUXGZTPOJ2HK3TFOMQGG33NMUQG63RAO5UW4Z3TEBQW4ZBAMRSXAYLSOQQG63RAMZXW65BOBI======
IFWGYIDUNBUW4Z3TEBQXEZJAMRUWMZTJMN2WY5BAMJSWM33SMUQHI2DFPEQGC4TFEBSWC43ZFYQAU===
IFWGYIDDN53GK5BMEBQWY3BANRXXGZJOBK======
IJ4SA33UNBSXEJ3TEBTGC5LMORZSYIDXNFZWKIDNMVXCAY3POJZGKY3UEB2GQZLJOIQG653OFYFG====
KZUXE5DVMUQGS4ZAORUGKIDPNZWHSIDUOJ2WKIDON5RGS3DJOR4S4CT=
KRUGKIDVNZSXQYLNNFXGKZBANRUWMZJANFZSA3TPOQQHO33SORUCA3DJOZUW4ZZOBI======
IJ2XG2LOMVZXGIDJOMQGE5LTNFXGK43TFYFG====
IVQWG2BANVQW4IDJOMQHI2DFEBQXEY3INF2GKY3UEBXWMIDINFZSA33XNYQGMYLUMUXAV===
JNXG653MMVSGOZJANFZSA3DPNZTSYIDMNFTGKIDJOMQHG2DPOJ2C4CW=
K5SSA2LNOBZG65TFEBXXK4TTMVWHMZLTEBRHSIDWNFRXI33SNFSXGIDPOZSXEIDPOVZHGZLMOZSXGLRAKRUGK4TFEBWXK43UEBRGKIDDN5XHIZLTORZSYIDBNZSCA53FEBWXK43UEB3WS3ROBJ======
IEQG2YLOEBTGC3DMOMQGS3RANRXXMZJAORUHE33VM5UCA2DJOMQGK6LFOMWCAYJAO5XW2YLOEB2GQ4TPOVTWQIDIMVZCAZLBOJZS4CU=
JV4SA3DPOZSSA2LTEBWGS23FEBQW4IDPMNSWC3R3EBUXIIDHN5SXGIDEN53W4IDTN4QGIZLFOAXCACS=
IFWGYIDJOMQG433UEBTWC2LOEB2GQYLUEBUXGIDQOV2CA2LOEB2GQZJAOB2XE43FFYFH====
KN2HE2LLMUQHI2DFEBUXE33OEB3WQ2LMMUQGS5BANFZSA2DPOQXAU===
JNUW4Z3TEBTW6IDNMFSCYIDBNZSCA5DIMUQHAZLPOBWGKIDTOVTGMZLSEBTG64RANF2C4CS=
LFXXK5DIEBWWKYLOOMQGY2LNNF2GYZLTOMQHA33TONUWE2LMNF2GSZLTFYFG====
K5UGK3RAONUGK4DIMVZGI4ZAOF2WC4TSMVWCYIDUNBSSA53PNRTCA2DBOMQGCIDXNFXG42LOM4QGOYLNMUXAU===
JRUWWZJAORZGKZJMEBWGS23FEBTHE5LJOQXAV===
KNYGKZLDNAQGS4ZAORUGKIDJNVQWOZJAN5TCAYLDORUW63TTFYFB====
IJQWIIDXN5ZGW3LFNYQG6ZTUMVXCAYTMMFWWKIDUNBSWS4RAORXW63DTFYFK====
I5UXMZJAMEQGYYLSNMQHI3ZAMNQXIY3IEBQSA23JORSS4CV=
KRUGKIDXN5ZGYZBANFZSA3DJNNSSAYJANVUXE4TPOI5CARTSN53W4IDBOQQGS5DBNZSCA2LUEBTHE33XNZZSAYLUEB4W65J3EBZW22LMMUWCAYLOMQQGS5BAONWWS3DFOMQHI33PFYFJ====
IV3GK4TZEBXG6YTMMUQHO33SNMQGS4ZAMF2CAZTJOJZXIIDJNVYG643TNFRGYZJOBI======
KZUWG5DPOJ4SA53PN3RIBGLUEBRW63LFEB2G6IDNMUQHK3TMMVZXGICJEBTW6IDUN4QGS5D=
KRZHKZJANVQXG5DFOJ4SA33GEBQW46JAONVWS3DMEB2GC23FOMQGCIDMNFTGK5DJNVSS4===
KNUGC4TQEB2G633MOMQG2YLLMUQGO33PMQQHO33SNMXG====
JRXW62ZAMF2CAZLWMVZHSIDPNZSSAZLROVQWY3DZFQQGS4TSMVZXAZLDORUXMZJAN5TCA4TJMNUCA33SEBYG633SFQQG433CNRSSA33SEBXWMIDMN53SAY3BON2GKLT=
JRXXMZJAMF2CAZTJOJZXIIDTNFTWQ5BANZSXMZLSEBUGC4DQMVXHGIDCMVTG64TFEBRHEZLBNNTGC43UFY======
IZQWY43FEB3WS5DIEBXW4ZJAMNQW4IDCMUQGMYLMONSSA53JORUCA5DXN4XC====
IFWGYIDUNBUW4Z3TEBQXEZJAMVQXG6JAORUGC5BAMFZGKIDEN5XGKIDXNFWGY2LOM5WHSLW=
LJSWC3BAO5UXI2DPOV2CA23ON53WYZLEM5SSA2LTEBTGS4TFEB3WS5DIN52XIIDMNFTWQ5D=
IJUXIZJAN5TGMIDNN5ZGKIDUNBQW4IDPNZSSAY3BNYQGG2DFO4XB====
JF2CA5DBNNSXGIDUO5XSA5DPEBWWC23FEBQSA4LVMFZHEZLMF3======
IFWGYIDJOMQGMYLJOIQGS3RAO5QXELQ=
IEQGMYLJOIQGMYLDMUQG2YLZEBUGSZDFEBQSAZTPOVWCA2DFMFZHILT=
KRUGKIDNMFXCA53JORUCAYJANZSXOIDJMRSWCIDJOMQGCIDDOJQW42ZAOVXHI2LMEB2GQZJANFSGKYJAON2WGY3FMVSHGLX=
IRXSA3TPORUGS3THEBRHSIDIMFWHMZLTFZ======
```

## 

## [steg base64](https://blog.csdn.net/Sanctuary1307/article/details/113836907)

base64隐写

**特征**: 多行base64编码,行结尾有`=`

**测试数据**:

```
U3RlZ2Fub2dyYXBoeSBpcyB0aGUgYXJ0IGFuZCBzY2llbmNlIG9m
IHdyaXRpbmcgaGlkZGVuIG1lc3NhZ2VzIGluIHN1Y2ggYSB3YXkgdGhhdCBubyBvbmV=
LCBhcGFydCBmcm9tIHRoZSBzZW5kZXIgYW5kIGludGVuZGVkIHJlY2lwaWVudCwgc3VzcGU=
Y3RzIHRoZSBleGlzdGVuY2Ugb2YgdGhlIG1lc3M=
YWdlLCBhIGZvcm0gb2Ygc2VjdXJpdHkgdGhyb3VnaCBvYnNjdXJpdHkuIFS=
aGUgd29yZCBzdGVnYW5vZ3JhcGh5IGlzIG9mIEdyZWVrIG9yaWdpbiBhbmQgbWVhbnMgImNvbmNlYW==
bGVkIHdyaXRpbmciIGZyb20gdGhlIEdyZWVrIHdvcmRzIHN0ZWdhbm9zIG1lYW5pbmcgImNv
dmVyZWQgb3IgcHJvdGVjdGVkIiwgYW5kIGdyYXBoZWluIG1lYW5pbmcgInRvIHc=
cml0ZSIuIFRoZSBmaXJzdCByZWNvcmRlZCB1c2Ugb2YgdGhlIHRlcm0gd2FzIGluIDE0OTkgYnkgSm9o
YW5uZXMgVHJpdGhlbWl1cyBpbiBoaXMgU3RlZ2Fub2dyYXBoaWEsIGEgdHJlYV==
dGlzZSBvbiBjcnlwdG9ncmFwaHkgYW5kIHN0ZWdhbm9ncmFwaHkgZGlzZ8==
dWlzZWQgYXMgYSBib29rIG9uIG1hZ2ljLiBHZW5lcmFsbHksIG1lc3P=
YWdlcyB3aWxsIGFwcGVhciB0byBiZSBzb21ldGhpbmcgZWxzZTogaW1hZ2VzLCBhcnRp
Y2xlcywgc2hvcHBpbmcgbGlzdHMsIG9yIHNvbWUgb3R=
aGVyIGNvdmVydGV4dCBhbmQsIGNsYXNzaWNhbGx5LCB0aGUgaGlkZGVuIG1lc3NhZ2UgbWF5IGJlIGluIGludmm=
c2libGUgaW5rIGJldHdlZW4gdGhlIHZpc2libGUgbGluZXMgb2YgYSBwcml2YXRlIGxldHRlci4NCg0KVGhl
IGFkdmFudGFnZSBvZiBzdGVnYW5vZ3JhcGh5LCBvdmVyIGNy
eXB0b2dyYXBoeSBhbG9uZSwgaXMgdGhhdCBtZXNzYWdlcyBkbyBub3QgYXR0cmFjdCBhdHRlbnRpb25=
IHRvIHRoZW1zZWx2ZXMuIFBsYWlubHkgdmlzaWJsZSBlbmNyeXB0ZWQgbWVzc2FnZXOXbm8gbWF0dGVyIF==
aG93IHVuYnJlYWthYmxll3dpbGwgYXJvdXNlIHN=
dXNwaWNpb24sIGFuZCBtYXkgaW4gdGhlbXNlbHZlcyBiZSBpbmNyaW1pbmF0aW5nIP==
aW4gY291bnRyaWVzIHdoZXJlIGVuY3J5cHRpb24gaXMgaWxsZWdhbC4gVGhlcmVmb3JlLH==
IHdoZXJlYXMgY3J5cHRvZ3JhcGh5IHByb3RlY3RzIHRoZSBjb250ZW50cyBvZj==
IGEgbWVzc2FnZSwgc3RlZ2Fub2dyYXBoeSBjYW4gYmUgc2FpZCB0byBwcm90ZWN0IGJ=
b3RoIG1lc3NhZ2VzIGFuZCBjb21tdW5pY2F0aW5nIHBhcnRpZXMuDQoNClN0ZWdhbm9ncmFwaHkgaW5jbHW=
ZGVzIHRoZSBjb25jZWFsbWVudCBvZiBpbmZvcm1hdGlvbiB3aXRoaW4gY29t
cHV0ZXIgZmlsZXMuIEluIGRpZ2l0YWwgc3RlZ2Fub2dyYXBoeSwgZWxlY3Ryb25pYyBjb21tdW5pY2F0aW9u
cyBtYXkgaW5jbHVkZSBzdGVnYW5vZ3JhcGhpYyBjb2RpbmcgaW5zaZ==
ZGUgb2YgYSB0cmFuc3BvcnQgbGF5ZXIsIHN1Y2ggYXMgYSBkb2N1bWVudCBmaWxlLCBpbWFnZSBmaWx=
ZSwgcHJvZ3JhbSBvciBwcm90b2NvbC4gTWVkaWEg
ZmlsZXMgYXJlIGlkZWFsIGZvciBzdGVnYW5vZ3JhcGhpYyB0cmFuc21pc3Npb+==
biBiZWNhdXNlIG9mIHRoZWlyIGxhcmdlIHNpemUuIEFzIB==
YSBzaW1wbGUgZXhhbXBsZSwgYSBzZW5kZXIgbWlnaHQgc3RhcnQgd2l0aCBh
biBpbm5vY3VvdXMgaW1hZ2UgZmlsZSBhbmQgYWRqdXN0IHRoZSBjb2xvciBvZiBldmVyeSAxMDB0aCBwaXhlbCD=
dG8gY29ycmVzcG9uZCB0byBhIGxldHRlciBpbiB0aGUgYWxwaGFiZXQsIGF=
IGNoYW5nZSBzbyBzdWJ0bGUgdGhhdCBzb21lb25lIG5vdCBzcGVjaWZpY2FsbHkgbG9va2luZyBm
b3IgaXQgaXMgdW5saWtlbHkgdG8gbm90aWNlIGl0Lg0KDQpUaGU=
IGZpcnN0IHJlY29yZGVkIHVzZXMgb2Ygc3RlZ2Fub2dyYXBoeSBjYW4gYmUgdHJ=
YWNlZCBiYWNrIHRvIDQ0MCBCQyB3aGVuIEhlcm9kb3R1cyBtZW50aW9ucyB0d28gZXhhbXBsZXMgb+==
ZiBzdGVnYW5vZ3JhcGh5IGluIFRoZSBIaXN0b3JpZXMgb2Yg
SGVyb2RvdHVzLiBEZW1hcmF0dXMgc2VudCBhIHdhcm5pbmcgYWJvdXQgYSB=
Zm9ydGhjb21pbmcgYXR0YWNrIHRvIEdyZWVjZSBieSB3
cml0aW5nIGl0IGRpcmVjdGx5IG9uIHRoZSB3b29kZW4gYmFja2luZyBvZiBhIHdheCB0YWJsZXQgYmVm
b3JlIGFwcGx5aW5nIGl0cyBiZWVzd2F4IHN1cmZhY2UuIFdheCB0YWJsZXRzIHdlcmUgaW4gY29tbW9uIHVzZV==
IHRoZW4gYXMgcmV1c2FibGUgd3JpdGluZyBzdXJmYWNlcywgc29tZXRpbWX=
cyB1c2VkIGZvciBzaG9ydGhhbmQuIEFub3RoZXIgYW5jaWVudCBleGFtcGxlIGlzIHRoYXQgb9==
ZiBIaXN0aWFldXMsIHdobyBzaGF2ZWQgdGhlIGhlYWQgb2YgaGlzIG1vc3QgdHJ1c3RlZCBz
bGF2ZSBhbmQgdGF0dG9vZWQgYSBtZXNzYWdlIG9uIGl0LiBBZnRlciBoaXMgaGFpciBoYWQgZ5==
cm93biB0aGUgbWVzc2FnZSB3YXMgaGlkZGVuLiBUaGUgcHVycG9zZSB3YXMgdG+=
IGluc3RpZ2F0ZSBhIHJldm9sdCBhZ2FpbnN0IHRoZSBQZXJzaWFucy4NCg0KU3RlZ2Fub2dyYXBoeSBoYXMgYm==
ZWVuIHdpZGVseSB1c2VkLCBpbmNsdWRpbmcgaW4gcmVjZW50IGhpc3RvcmljYWwgdGltZXMgYW5kIHT=
aGUgcHJlc2VudCBkYXkuIFBvc3NpYmxlIHBlcm11dGF0aW9ucyBhcmUgZW5kbGVzcyBhbmT=
IGtub3duIGV4YW1wbGVzIGluY2x1ZGU6DQoqIEhpZGRlbiBtZXNzYWdlcyB3aXRoaW4gd2F4IHRh
YmxldHM6IGluIGFuY2llbnQgR3JlZWNlLCBwZW9wbGUgd3JvdGUgbWV=
c3NhZ2VzIG9uIHRoZSB3b29kLCB0aGVuIGNvdmVyZWQgaXQgd2l0aCB3YXggdXBvbiB3aGljaCBhbiBpbm5vY2Vu
dCBjb3ZlcmluZyBtZXNzYWdlIHdhcyB3cml0dGVu
Lg0KKiBIaWRkZW4gbWVzc2FnZXMgb24gbWVzc2VuZ2VyJ3MgYm9keTogYWxzbyB1c2VkIGluIGFuY2llbt==
dCBHcmVlY2UuIEhlcm9kb3R1cyB0ZWxscyB0aGUgc3Rvcnkgb1==
ZiBhIG1lc3NhZ2UgdGF0dG9vZWQgb24gYSBzbGF2ZSdzIHNoYXZlZCBoZWFkLCBoaWRkZW4gYnkgdGhl
IGdyb3d0aCBvZiBoaXMgaGFpciwgYW5kIGV4cG9zZWQgYnkgc2hhdmluZyBoaXMgaGVhZM==
IGFnYWluLiBUaGUgbWVzc2FnZSBhbGxlZ2VkbHkgY2FycmllZCBhIHdhcm5pbmcgdG8gR3JlZWNlIGFib5==
dXQgUGVyc2lhbiBpbnZhc2lvbiBwbGFucy4gVGh=
aXMgbWV0aG9kIGhhcyBvYnZpb3VzIGRyYXdiYWNrcyz=
IHN1Y2ggYXMgZGVsYXllZCB0cmFuc21pc3Npb24gd2hpbGUgd2FpdGluZyBmb3IgdGhlIHP=
bGF2ZSdzIGhhaXIgdG8gZ3JvdywgYW5kIHRoZSByZXN0cmljdGlvbnMgb3==
biB0aGUgbnVtYmVyIGFuZCBzaXplIG9mIG1lc3M=
YWdlcyB0aGF0IGNhbiBiZSBlbmNvZGVkIG9uIG9uZSBwZXJzb24=
J3Mgc2NhbHAuDQoqIEluIFdXSUksIHRoZSBGcmVuY2ggUmVzaXN0YW5jZSBzZW50IHNvbWUgbWVzc2FnZXMgd2==
cml0dGVuIG9uIHRoZSBiYWNrcyBvZiBjb3VyaWVycyD=
dXNpbmcgaW52aXNpYmxlIGluay4NCiogSGlkZGVuIG1lc3NhZ2VzIG9uIHBhcGVyIHdy
aXR0ZW4gaW4gc2VjcmV0IGlua3MsIHVuZGVyIG90aGVyIG1lc3NhZ2Vz
IG9yIG9uIHRoZSBibGFuayBwYXJ0cyBvZiBvdGhlct==
IG1lc3NhZ2VzLg0KKiBNZXNzYWdlcyB3cml0dGVuIGluIE1vcnNlIGNvZGUgb24ga25pdHRpbmcgeWFybiBhbmQg
dGhlbiBrbml0dGVkIGludG8gYSBwaWVjZSBvZiBjbG90aGluZyB3b3K=
biBieSBhIGNvdXJpZXIuDQoqIE1lc3NhZ2VzIHdyaXR0ZW4gb24gdGhlIGJhY2sgb5==
ZiBwb3N0YWdlIHN0YW1wcy4NCiogRHVyaW5nIGFuZCBhZnRlcm==
IFdvcmxkIFdhciBJSSwgZXNwaW9uYWdlIGFnZW50cyB1c2VkIHBob3RvZ3JhcGhpY2FsbHkgcO==
cm9kdWNlZCBtaWNyb2RvdHMgdG8gc2VuZCBpbmZvcm1hdGlvbiBiYWNrIGFuZH==
IGZvcnRoLiBNaWNyb2RvdHMgd2VyZSB0eXBpY2FsbHkg
bWludXRlLCBhcHByb3hpbWF0ZWx5IGxlc3MgdGhhbiB0aGUgc2l6ZSBvZiB0aGUgcGVyaW9kIHByb2R=
dWNlZCBieSBhIHR5cGV3cml0ZXIuIFdXSUkgbWljcm9kb3RzIG5lZWRlZCB0byBiZSBlbWJlZGRlZB==
IGluIHRoZSBwYXBlciBhbmQgY292ZXJlZCB3aXRoIGFuIGFkaGVzaXZlIChzdWNoIGFzIGNvbGxvZGlvbikuIFR=
aGlzIHdhcyByZWZsZWN0aXZlIGFuZCB0aHVzIGRldGVjdGFibGUg
Ynkgdmlld2luZyBhZ2FpbnN0IGdsYW5jaW5nIGxpZ2h0LiBBbHRlcm5hdGl2ZSB0ZWNobmlxdWVzIGluY2x1ZGVk
IGluc2VydGluZyBtaWNyb2RvdHMgaW50byBzbGl0cyBjdXQgaW50byB0aGUgZWRnZSBvZv==
IHBvc3QgY2FyZHMuDQoqIER1cmluZyBXb3JsZCBXYXIgSUksIGEgc3B5IGZvciB=
SmFwYW4gaW4gTmV3IFlvcmsgQ2l0eSwgVmVsdmFsZWW=
IERpY2tpbnNvbiwgc2VudCBpbmZvcm1hdGlvbiB0byBhY2NvbW1vZGF0aW9=
biBhZGRyZXNzZXMgaW4gbmV1dHJhbCBTb3V0aCBBbWVyaWO=
YS4gU2hlIHdhcyBhIGRlYWxlciBpbiBkb2xscywgYW5kIG==
aGVyIGxldHRlcnMgZGlzY3Vzc2VkIGhvdyBtYW55IG9mIHRoaXMgb3IgdGhhdCBkb2xs
IHRvIHNoaXAuIFRoZSBzdGVnb3RleHQgd2FzIHRoZSBkb2xsIG9yZGVycywgd2hpbGUgdGhl
IGNvbmNlYWxlZCAicGxhaW50ZXh0IiB3YXMgaXRzZWxmIGVuY2+=
ZGVkIGFuZCBnYXZlIGluZm9ybWF0aW9uIGFib3V0IHNoaXAgbW92ZW1lbnRzLF==
IGV0Yy4gSGVyIGNhc2UgYmVjYW1lIHNvbWV3aGF0IGZh
bW91cyBhbmQgc2hlIGJlY2FtZSBrbm93biBhcyB0aGX=
IERvbGwgV29tYW4uDQoqIENvbGQgV2FyIGNvdW50
ZXItcHJvcGFnYW5kYS4gSW4gMTk2OCwgY3JldyBtZW1iZW==
cnMgb2YgdGhlIFVTUyBQdWVibG8gKEFHRVItMikgaW50ZWxsaWdlbmNlIHNoaXAgaGVsZCBhcyBwcm==
aXNvbmVycyBieSBOb3J0aCBLb3JlYSwgY29tbXVuaWNhdGVkIGluIHNpZ25=
IGxhbmd1YWdlIGR1cmluZyBzdGFnZWQgcGhvdG8gb3Bwb3J0
dW5pdGllcywgaW5mb3JtaW5nIHRoZSBVbml0ZWQgU3RhdGVzIHRoZXkg
d2VyZSBub3QgZGVmZWN0b3JzIGJ1dCByYXRoZXIgd2VyZSBiZWluZyBoZWxkIGNh
cHRpdmUgYnkgdGhlIE5vcnRoIEtvcmVhbnMuIEluIG90aGVyIHBob3Rv
cyBwcmVzZW50ZWQgdG8gdGhlIFVTLCBjcmV3IG1lbWJlcnMgZ2F2ZSAidGhlIGZpbmdlciIgdG8g
dGhlIHVuc3VzcGVjdGluZyBOb3J0aCBLb3JlYW5zLCBpbiBhbiBhdHRlbXB0IHRvIE==
ZGlzY3JlZGl0IHBob3RvcyB0aGF0IHNob3dlZCB0aGVtIHNtaQ==
bGluZyBhbmQgY29tZm9ydGFibGUuDQoNCi0tDQpodHRwOi8vZW4ud2lraXBlZGlhLm9yZw==
L3dpa2kvU3RlZ2Fub2dyYXBoeQ0K
```

## 

## [TapCode](https://zh.wikipedia.org/wiki/%E6%95%B2%E6%89%93%E5%AF%86%E7%A2%BC)

敲击码,又或称为**打拍密码**,常被监狱囚犯用作沟通

**特征**:由 `•`  ` `组成或者 数字

**测试数据**:

```
••••• ••   • •   •••• ••••   • •••••   •••• ••
```

```
5211441542
```

## [trifid](http://www.practicalcryptography.com/ciphers/classical-era/trifid/)

三方密码

**特征**: 无,见提示

**测试数据**:

key: EPSDUCVWYM.ZLKXNBTFGORIJHAQ

```
SUEFECPHSEGYYJIXIMFOFOCEJLBSP
```

## troll script

brain fuck 变种

**特征**: 由trol组成

**测试数据**:

```
Trooloolooloolooloolooloolooloolollooooolooloolooloolooloolooooolooloolooloolooloolooloolooloooooloolooloooooloooloolooloololllllooooloololoooooololooolooloolooloolooloololoolooolooloololooooooloololooooloololooloolooloolooloolooloolooloolooloolooloololooooolooolooloololooollollollollollolllooollollollollollollollollloooooololooooolool
```

## [Twin Hex](https://www.calcresult.com/misc/cyphers/twin-hex.html)

**特征**: 见提示

**测试数据**:

```
6af5gu10o56g5gz5915s0
```

## [virgenene](https://zh.m.wikipedia.org/zh/%E7%BB%B4%E5%90%89%E5%B0%BC%E4%BA%9A%E5%AF%86%E7%A0%81)

维吉尼亚密码

**特征**: 无,见提示

**测试数据**:

密钥: LEMONLEMONLE

```
LXFOPVEFRNHR
```

## [vowel]()

元音, 单表替换密码,替换表如下

```
1 11 12 13 2 21 22 23 3 31 32 33 34 35 4 41 42 43 44 45 5 51 52 53 54 55
```

**特征**: 数字 + 分隔符,且第一个数字不大于5

**测试数据**:

```
51 4 52 33 2 3 44 21 5 35
```

## 

## zwBinary(zeroWidthBinary)

零宽字符, 基于[zero-width-lib ](https://github.com/yuanfux/zero-width-lib)实现

**特征**: 长度与看到的不符合,包含零宽字符

**测试数据**:

```
信‏‏‌‍‌﻿﻿​‌‍‎‍‎﻿​‌‍‎‍‏‌​‌‍‎‍‏‎​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‎​‌‍‎‍‏‏​‌‍‎‍‏‏​‌‍‎‍‎‍​‌‍‎‍‏‌​‌‍‎‍‏‎​‌‍‎‍‏‌​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‌​‌‍‎‍‏‌​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​‌‍‎‍‎﻿​‌‍‎‍‎﻿​‌‍‎‍‏‌​‌‍‎‍‏‎​‌‍‎‍‏‌​‌‍‎‍‏‎​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​‌‍‎‍‎﻿​‌‍‎‍‏‏​﻿‎﻿‌‏‎﻿​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‏​‌‍‎‍‏‌​‌‍‎‍‏‏​‌‍‎‍‎‍​‌‍‎‍‏‌​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‎​‌‍‎‍‎﻿​‌‍‎‍‏‎​‌‍‎‍‏‎​‌‍‎‍‏‏​‌‍‎‍‎‍​‌‍‎‍‏‌​‌‍‎‍‏‌​‌‍‎‍‎﻿​‌‍‎‍‏‎​‌‍‎‍‎﻿​﻿‎﻿‌‏‎﻿​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​‌‍‎‍‏‌​‌‍‎‍‎﻿​﻿‎﻿‌‏‎﻿​‌‍‎‍‎﻿​‌‍‎‍‎﻿​‌‍‎‍‎﻿​‌‍‎‍‎﻿​‌‍‎‍‎‍​‌‍‎‍‏‎​‌‍‎‍‏‌​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‏​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‌​‌‍‎‍‏‏​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‎​‌‍‎‍‏‏​‌‍‎‍‎‍​‌‍‎‍‏‌​‌‍‎‍‎﻿​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‎​‌‍‎‍‏‏​‌‍‎‍‎‍​‌‍‎‍‏‌​‌‍‎‍‏‌​‌‍‎‍‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‏​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‌​‌‍‎‍‏‏​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‏​‌‍‎‍‏‏​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‏​‌‍‎‍‏‌​‌‍‎‍‎‍​‌‍‎‍‏‌​‌‍‎‍‏‌​‌‍‎‍‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‌​‌‍‎‍‏‏​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‎﻿​‌‍‎‍‏‏​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‏​‌‍‎‍‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‎﻿​‌‍‎‍‏‌​‏‌﻿‌‌‌‌​‏‌‎‍‌‌‌​‏‏﻿‍﻿‌‍​‌‏﻿‍‌﻿﻿​‏‏﻿‌﻿‎‌​﻿‎﻿‌‌‌‏息不在这里哦！
```

## zwBinary-morse(zeroWidthMorse)

零宽字符, 基于[morse-encrypt ](https://github.com/rover95/morse-encrypt)实现

**特征**:  长度与看到的不符合,包含零宽字符

**测试数据**:

```
春风再美也比上你的笑，‌‍‌​‍‍‍​‌‌‌‍​‌​‌‍‌‌​‌‍​‌‌‌​‍没见过你的人不会明了
```

## zwUnicode(zeroWidthUnicode)

零宽字符, 基于[unicode_steganography ](https://330k.github.io/misc_tools/unicode_steganography.html)实现

**特征**:  长度与看到的不符合,包含零宽字符

**测试数据**:

默认字典

```
​​​​​Lorem ipsum​​​​​​​ dolor ‌‌‌‌‍﻿‍‍sit​​​​​​​​ amet​​​​​​​​​‌‌‌‌‍﻿‍‌, consectetur ​​​​​​​adipiscing​​​​​​​‌‌‌‌‍‬‍‬ elit​​​​​​​.‌‌‌‌‍‬﻿‌​​​​​​​‌‌‌‌‍‬‌‍ Phasellus quis​​​​​​​ tempus​​​​​​ ante, ​​​​​​​​nec vehicula​​​​​​​​​​​​​​​​ mi​​​​​​​​. ​​​​​​​‌‌‌‌‍‬‍﻿Aliquam nec​​​​​​​​​‌‌‌‌‍﻿‬﻿ nisi ut neque​​​​​​​ interdum auctor​​​​​​​.‌‌‌‌‍﻿‍﻿ Aliquam felis ‌‌‌‌‍‬‬‌orci​​​​​​​, vestibulum ‌‌‌‌‍﻿‬‍sit ​​​​​​​amet​​​​​​​​​ ante‌‌‌‌‍‌﻿‬ at​​​​​​​, consectetur‌‌‌‌‍‌﻿﻿ lobortis eros​​​​​​​​​.‌‌‌‌‍‍‍‌ ‌‌‌‌‍‌‌‌​​​​​​​Orci varius​​​​​​​ ​​​​​​​natoque ‌‌‌‌‍﻿‌﻿penatibus et ‌‌‌‌‍‬‌﻿​​​​​​​magnis‌‌‌‌‌﻿‌‍‌‌‌‌‌﻿‌‍ dis ​​​​​​​‌‌‌‌‍‍﻿﻿parturient montes, ​​​​​​​nascetur ridiculus ‌‌‌‌‌﻿‍‌​​​​​​​​​​​​​​‌‌‌‌‌﻿‬‍mus. In finibus‌‌‌‌‌﻿‌‬ magna​​​​​​‌‌‌‌‌﻿‍﻿ mauris, quis‌‌‌‌‍‬‌‍ auctor ‌‌‌‌‍‬‌‍libero congue quis. ‌‌‌‌‍‬‬‬Duis‌‌‌‌‍‬‌‬ sagittis consequat urna non tristique. Pellentesque eu lorem ‌‌‌‌‍﻿‌‍id‌‌‌‌‍‬‬﻿ quam vestibulum ultricies vel ac purus‌‌‌‌‌﻿‌‍.‌‌‌‌‌
```



[自定义字典](https://c.p2hp.com/hidetext/) \\u200b\\u200c\\u200d\\u200e\\u200f (可不设置,自动解析)

```
你‍‍‌‎‌‎‏‍​‏‏​‌​‌‌‍‎‍‏‏‌‍‏‍‍‍‍好ad
```



## [与佛论禅](https://www.keyfc.net/bbs/tools/tudoucode.aspx)

与佛论禅

**特征**: `佛曰：`开头

**测试数据**:

```
佛曰：麼奢道梵呼舍舍等密爍皤集怯一梵殿缽離罰喝不耶苦
```



## [与佛论禅加密版v1](https://fy.by950.top/)

与佛论禅加密版, 与佛论禅算法勾选加密版本选项, 默认密码 `TakuronDotTop`

**特征**: `佛曰：`开头, 密码可选

**测试数据**:

```
佛曰：楞醯吉数舍苏卢楞烁楞醯阿摩驮娑孕那尼摩楞遮迦萨墀伊阿写罚卢嚧娑婆穆度俱唎烁咩伊输写尼栗喝娑苏豆写醯蒙舍度舍帝咩数输楞娑苏菩醯楞呼怛呼度无谨哆喝遮咩栗穆地漫漫
```

## [与佛论禅加密版v2](http://www.atoolbox.net/Tool.php?Id=1027) [github](https://github.com/takuron/talk-with-buddha)

与佛论禅加密版, 与佛论禅算法勾选加密版本选项, 默认密码 `takuron.top`

**特征**: `佛又曰：`开头, 密码可选

**测试数据**:

```
佛又曰：输啰提尼佛尼唎穆度伽阿孕写罚怛阿婆羯帝喝输唎舍室俱烁谨谨烁阇曳蒙喝漫
```

## 

## [六十四卦](https://blog.csdn.net/weixin_44110537/article/details/107494966)

base64编码,字典为64卦

**特征**: 64卦组成

**测试数据**:

```
升困艮益蛊困蛊无妄井萃噬嗑既济井兑损离巽履晋节恒履蒙归妹鼎讼蛊履大过否噬嗑需井萃未济丰巽萃大有同人小过涣谦
```



## [兽音](http://hi.pcmoe.net/roar.html)

**特征**:  由 ~呜嗷啊组成

**测试数据**:

```
~呜嗷嗷嗷嗷呜啊嗷啊~呜嗷呜呜~呜啊~啊嗷啊呜嗷呜~~~嗷~呜呜呜~~嗷嗷嗷呜啊呜呜啊呜嗷呜呜啊呜嗷呜啊嗷啊呜~嗷啊啊~嗷~呜嗷嗷~啊嗷嗷嗷呜啊嗷啊啊呜嗷呜呜~嗷嗷嗷啊嗷啊呜嗷呜~~~嗷~呜呜嗷呜~嗷嗷嗷呜啊呜啊嗷呜嗷呜呜~嗷啊呜啊嗷啊呜~嗷啊呜~嗷~呜呜嗷嗷啊嗷嗷嗷呜啊嗷嗷啊呜嗷呜呜~嗷呜嗷啊嗷啊呜~嗷啊啊~嗷~呜嗷啊啊~嗷嗷嗷呜啊嗷啊嗷呜嗷呜呜~嗷呜啊啊嗷啊呜嗷嗷啊呜~嗷~呜嗷呜嗷~嗷嗷嗷呜呜呜嗷~呜嗷呜呜啊呜~呜啊嗷啊呜~嗷啊啊~嗷~呜嗷~嗷啊嗷嗷嗷呜啊呜啊嗷呜嗷呜呜~嗷啊呜啊嗷啊呜~啊~啊~嗷~呜呜呜嗷呜嗷嗷嗷呜啊嗷呜嗷呜嗷呜呜~呜~啊啊嗷啊呜嗷嗷呜~~嗷~呜呜嗷呜嗷嗷嗷嗷呜啊呜呜呜啊
```

## [天干地支](https://github.com/chai2010/base60)

```
天干: 甲乙丙丁戊己庚辛壬癸
```

```
地支: 子丑寅卯辰巳午未申酉戌亥
```

**特征**: 天干+地支编码

**测试数据**:

```
乙丑癸巳甲寅己亥丁卯甲申丁未甲午己巳
```

## 

## [新佛曰](http://hi.pcmoe.net/buddha.html)

新约佛论禅

**特征**: `新佛曰：`开头

**测试数据**:

```
新佛曰：諸怖隸僧怖降吽諸陀怖摩隸怖僧缽薩願僧宣摩嚴迦聞般怖眾訶嚤哆愍羅願摩莊怖隸兜修慧怖隸摩哆即嘚嚤陀寂怖諦怖哆愍怖所願怖是願愍怖闍念哆愍喃菩怖般怖祗念阿怖如怖
```

## [熊曰](http://hi.pcmoe.net/index.html)

**特征**:  `熊曰：`开头

**测试数据**:

```
熊曰：呋性呱吖萌盜性森破捕訴嗒喜冬呱唬嗡盜偶哈魚嗥麼噔囑襲達嗥取喜捕嘿家咬嗡性既洞喜達嗒嘿沒類嚁麼常哈現襲啽樣動你嗅嘍嗚爾現氏蜜動眠常嗡嗥破住啽嗥囑更沒常破嘍森唬偶嗅人氏拙怎噔雜很誒
```

PS. 结果是油猴脚本链接

## 百家姓

**特征**:  姓氏组合

```
水褚尤范尤褚柳尤张朱
```

## [阴阳怪气](https://github.com/mmdjiji/yygq.js)

**特征**:  由`就 这 ¿ ` `不 会 吧 ？ `组成

**测试数据**:

```
不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 就 这 ¿ 就 这 ¿ 不 会 吧 ？ 不 会 吧 ？ 不 会 吧 ？ 不 会 吧 ？ 不 会 吧 ？ 就 这 ¿ 就 这 ¿ 就 这 ¿ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 不 会 吧 ？ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 就 这 ¿ 就 这 ¿ 就 这 ¿ 就 这 ¿ 就 这 ¿ 就 这 ¿ 就 这 ¿ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 不 会 吧 ？ 不 会 吧 ？ 就 这 ¿ 
```

## 

## 参考



[ctf-wiki.org](https://ctf-wiki.org/crypto/introduction/)