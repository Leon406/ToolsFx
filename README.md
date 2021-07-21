# 项目主要功能

### 编解码

- [x] base64
- [x] base64safe
- [x] base16/32
- [x] UrlEncode
- [x] Unicode
- [x] hex
- [x] binary

### 数据摘要(哈希)

支持文件, 支持超大文件,8G文件测试ok

- [x] md系列
- [x] sha1
- [x] sha2系列
- [x] sha3
- [x] SM3
- [x] RIPEMD
- [x] whirpool
- [x] Tiger
- [x] 其他 BouncyCastle支持的算法

### MAC

#### HMAC

- [x] md系列
- [x] sha1
- [x] sha2系列
- [x] sha3
- [x] SM3
- [x] RIPEMD
- [x] whirpool
- [x] Tiger
- [x] 其他 BouncyCastle支持的算法

#### CMAC

- [x] AESCMAC
- [x] BLOWFISHCMAC
- [x] DESCMAC
- [x] DESEDECMAC
- [x] SEED-CMAC
- [x] Shacal-2CMAC
- [x] SM4-CMAC
- [x] Threefish-256CMAC  / Threefish-512CMAC / Threefish-1024CMAC  

#### GMAC

#### POLY1305 

- [ ] POLY1305
- [ ] POLY1305-AES
- [ ] POLY1305-ARIA
- [ ] POLY1305-CAMELLIA
- [ ] POLY1305-CAST6
- [ ] POLY1305-NOEKEON
- [ ] POLY1305-RC6
- [ ] POLY1305-SEED
- [ ] POLY1305-SERPENT
- [ ] POLY1305-SM4
- [ ] POLY1305-Twofish

### 分组对称加密 (block cipher)

#### 加密算法

- [x] DES/3DES
- [x] AES
- [x] SM4
- [x] Blowfish
- [x] Twofish
- [x] RC2
- [x] 其他 BouncyCastle支持的算法

#### 支持mode

- ECB
- CBC
- OFB(n)
- CFB(n)
- SIC (also known as CTR)
- CTS (equivalent to CBC/WithCTS)
- CCM (AEAD)
- EAX (AEAD)
- GCM (AEAD)
- OCB (AEAD)

#### 支持padding scheme

- No padding
- PKCS5/7
- ISO10126/ISO10126-2
- ISO7816-4/ISO9797-1
- X9.23/X923
- TBC
- ZeroByte
- withCTS (if used with ECB mode)


### 流式对称加密 (stream cipher)
- [x] RC4
- [x] HC128/HC256
- [x] ChaCha
- [x] Salsa20
- [x] XSalsa20
- [x] VMPC
- [x] Grainv1
- [x] Grain128
- [x] Zuc128
- [x] Zuc128

### 非对称加密 RSA

- [x]  密钥支持pkcs1 /pkcs8
- [x]  支持512/1024/2048/3072/4096位
- [x]  支持长度大于RSA位数的字符解码,但实际不建议这样操作
- [x]  支持openssl pkcs1/pkcs8 私钥格式

### 数字签名 (待定)

### 证书 (待定)

### 特性

- [x] 支持文件拖入
- [x] 对称key, iv 支持base64 ,hex
- [x] 对称加密支持文件加密解密,输出文件 (测试m3u8 ts文件解密后正常播放)
- [ ] 键盘事件, 快捷键
- [ ] 不同算法提示及限制
- [ ] UI美化


bouncycastle文档 https://www.bouncycastle.org/specifications.html
