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

**特征**: key长度未26,见提示

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

**特征**: U2FsdGVkX1开头

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

**特征**: 有汉字生僻字组成,见提示

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

## zeroWidthBinary

零宽字符, 基于[zero-width-lib ](https://github.com/yuanfux/zero-width-lib)实现

**特征**: 长度与看到的不符合,包含零宽字符

**测试数据**:

```
信‏‏‌‍‌﻿﻿​‌‍‎‍‎﻿​‌‍‎‍‏‌​‌‍‎‍‏‎​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‎​‌‍‎‍‏‏​‌‍‎‍‏‏​‌‍‎‍‎‍​‌‍‎‍‏‌​‌‍‎‍‏‎​‌‍‎‍‏‌​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‌​‌‍‎‍‏‌​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​‌‍‎‍‎﻿​‌‍‎‍‎﻿​‌‍‎‍‏‌​‌‍‎‍‏‎​‌‍‎‍‏‌​‌‍‎‍‏‎​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​‌‍‎‍‎﻿​‌‍‎‍‏‏​﻿‎﻿‌‏‎﻿​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‏​‌‍‎‍‏‌​‌‍‎‍‏‏​‌‍‎‍‎‍​‌‍‎‍‏‌​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‎​‌‍‎‍‎﻿​‌‍‎‍‏‎​‌‍‎‍‏‎​‌‍‎‍‏‏​‌‍‎‍‎‍​‌‍‎‍‏‌​‌‍‎‍‏‌​‌‍‎‍‎﻿​‌‍‎‍‏‎​‌‍‎‍‎﻿​﻿‎﻿‌‏‎﻿​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​‌‍‎‍‏‌​‌‍‎‍‎﻿​﻿‎﻿‌‏‎﻿​‌‍‎‍‎﻿​‌‍‎‍‎﻿​‌‍‎‍‎﻿​‌‍‎‍‎﻿​‌‍‎‍‎‍​‌‍‎‍‏‎​‌‍‎‍‏‌​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‏​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‌​‌‍‎‍‏‏​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‎​‌‍‎‍‏‏​‌‍‎‍‎‍​‌‍‎‍‏‌​‌‍‎‍‎﻿​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‎​‌‍‎‍‏‏​‌‍‎‍‎‍​‌‍‎‍‏‌​‌‍‎‍‏‌​‌‍‎‍‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‏​﻿‎﻿‌‏‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‌​‌‍‎‍‏‏​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‏​‌‍‎‍‏‏​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‏​‌‍‎‍‏‌​‌‍‎‍‎‍​‌‍‎‍‏‌​‌‍‎‍‏‌​‌‍‎‍‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‌​‌‍‎‍‏‏​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‎﻿​‌‍‎‍‏‏​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‏‏​‌‍‎‍‎﻿​‌‍‎‍‎‍​﻿‎﻿‌‏‎﻿​‌‍‎‍‎﻿​‌‍‎‍‏‌​‏‌﻿‌‌‌‌​‏‌‎‍‌‌‌​‏‏﻿‍﻿‌‍​‌‏﻿‍‌﻿﻿​‏‏﻿‌﻿‎‌​﻿‎﻿‌‌‌‏息不在这里哦！
```

## zeroWidthMorse

零宽字符, 基于[morse-encrypt ](https://github.com/rover95/morse-encrypt)实现

**特征**:  长度与看到的不符合,包含零宽字符

**测试数据**:

```
春风再美也比上你的笑，‌‍‌​‍‍‍​‌‌‌‍​‌​‌‍‌‌​‌‍​‌‌‌​‍没见过你的人不会明了
```

## zeroWidthUnicode

零宽字符, 基于[unicode_steganography ](https://330k.github.io/misc_tools/unicode_steganography.html)实现

**特征**:  长度与看到的不符合,包含零宽字符

**测试数据**:

默认字典

```
​​​​​​​Lorem ipsum​​​​​​​ dolor ‌‌‌‌‍﻿‍‍sit​​​​​​​​ amet​​​​​​​​​‌‌‌‌‍﻿‍‌, consectetur ​​​​​​​adipiscing​​​​​​​‌‌‌‌‍‬‍‬ elit​​​​​​​.‌‌‌‌‍‬﻿‌​​​​​​​‌‌‌‌‍‬‌‍ Phasellus quis​​​​​​​ tempus​​​​​​ ante, ​​​​​​​​nec vehicula​​​​​​​​​​​​​​​​ mi​​​​​​​​. ​​​​​​​‌‌‌‌‍‬‍﻿Aliquam nec​​​​​​​​​‌‌‌‌‍﻿‬﻿ nisi ut neque​​​​​​​ interdum auctor​​​​​​​.‌‌‌‌‍﻿‍﻿ Aliquam felis ‌‌‌‌‍‬‬‌orci​​​​​​​, vestibulum ‌‌‌‌‍﻿‬‍sit ​​​​​​​amet​​​​​​​​​ ante‌‌‌‌‍‌﻿‬ at​​​​​​​, consectetur‌‌‌‌‍‌﻿﻿ lobortis eros​​​​​​​​​.‌‌‌‌‍‍‍‌ ‌‌‌‌‍‌‌‌​​​​​​​Orci varius​​​​​​​ ​​​​​​​natoque ‌‌‌‌‍﻿‌﻿penatibus et ‌‌‌‌‍‬‌﻿​​​​​​​magnis‌‌‌‌‌﻿‌‍‌‌‌‌‌﻿‌‍ dis ​​​​​​​‌‌‌‌‍‍﻿﻿parturient montes, ​​​​​​​nascetur ridiculus ‌‌‌‌‌﻿‍‌​​​​​​​​​​​​​​‌‌‌‌‌﻿‬‍mus. In finibus‌‌‌‌‌﻿‌‬ magna​​​​​​‌‌‌‌‌﻿‍﻿ mauris, quis‌‌‌‌‍‬‌‍ auctor ‌‌‌‌‍‬‌‍libero congue quis. ‌‌‌‌‍‬‬‬Duis‌‌‌‌‍‬‌‬ sagittis consequat urna non tristique. Pellentesque eu lorem ‌‌‌‌‍﻿‌‍id‌‌‌‌‍‬‬﻿ quam vestibulum ultricies vel ac purus‌‌‌‌‌﻿‌‍.‌‌‌‌‌
```



[自定义字典](https://c.p2hp.com/hidetext/) \\u200b\\u200c\\u200d\\u200e\\u200f

```
你‍‍‌‎‌‎‏‍​‏‏​‌​‌‌‍‎‍‏‏‌‍‏‍‍‍‍好ad
```



## [佛曰](https://www.keyfc.net/bbs/tools/tudoucode.aspx)

与佛论禅

**特征**: `佛曰：`开头

**测试数据**:

```
佛曰：麼奢道梵呼舍舍等密爍皤集怯一梵殿缽離罰喝不耶苦
```

## [六十四卦](https://blog.csdn.net/weixin_44110537/article/details/107494966)

base64编码,字典为64卦

**特征**: 64卦组成

**测试数据**:

```
升困艮益蛊困蛊无妄井萃噬嗑既济井兑损离巽履晋节恒履蒙归妹鼎讼蛊履大过否噬嗑需井萃未济丰巽萃大有同人小过涣谦
```

## 

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



## [阴阳怪气](https://github.com/mmdjiji/yygq.js)

**特征**:  由`就 这 ¿ ` `不 会 吧 ？ `组成

**测试数据**:

```
不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 就 这 ¿ 就 这 ¿ 不 会 吧 ？ 不 会 吧 ？ 不 会 吧 ？ 不 会 吧 ？ 不 会 吧 ？ 就 这 ¿ 就 这 ¿ 就 这 ¿ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 不 会 吧 ？ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 就 这 ¿ 就 这 ¿ 就 这 ¿ 就 这 ¿ 就 这 ¿ 就 这 ¿ 就 这 ¿ 不 会 吧 ？ 就 这 ¿ 不 会 吧 ？ 不 会 吧 ？ 不 会 吧 ？ 就 这 ¿ 
```

## 

## 参考



[ctf-wiki.org](https://ctf-wiki.org/crypto/introduction/)