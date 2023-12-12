<div align=center><img  src="art/tb.png"/></div>

<h1 align="center">ToolsFx</h1>
<p align="center">
<a href="https://github.com/Leon406/ToolsFx/releases/latest"><img src="https://img.shields.io/github/release/Leon406/ToolsFx.svg"/></a>
<a href="https://github.com/Leon406/ToolsFx/actions/workflows/app-test.yml"><img src="https://github.com/Leon406/ToolsFx/actions/workflows/app-test.yml/badge.svg"/></a>
<a href="https://github.com/Leon406/ToolsFx/actions/workflows/detekt.yml"><img src="https://github.com/Leon406/ToolsFx/actions/workflows/detekt.yml/badge.svg"/></a>
<img src="https://img.shields.io/badge/language-kotlin-orange.svg"/>
<a href="changelog.md"><img src="https://img.shields.io/badge/updates-%E6%9B%B4%E6%96%B0%E6%97%A5%E5%BF%97-brightgreen"/></a>
<img src="https://img.shields.io/badge/license-ISC-green"/>
<img src="https://img.shields.io/github/downloads/Leon406/Toolsfx/total"/>
<a target="_blank" href="https://qm.qq.com/cgi-bin/qm/qr?k=RfiFeARrf_XDHsT0_TzwbWPIpxDTCx4Z&jump_from=webapi&authKey=zDsFRjKKhpfstcAP/XouVrrFO7m+vfjU3S7j5ZuXo1SnxTpPKIQyLX2da+bowY1P"><img border="0" src="https://pub.idqqimg.com/wpa/images/group.png" alt="ToolsFx交流群" title="ToolsFx交流群"></a>
<a href="https://gitter.im/ToolsFx/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge"><img src="https://badges.gitter.im/ToolsFx/community.svg"/></a>
</p>
<p align="center">
<a href="README.md">English</a>|<a href="README-zh.md">中文</a>
</p>

<h4 align="center">Visitors :eyes:</h4>

<p align="center"><img src="https://profile-counter.glitch.me/Leon406_ToolsFx/count.svg" alt="ToolsFx :: Visitor's Count" />
 <img width=0 height=0 src="https://profile-counter.glitch.me/Leon406/count.svg" alt="Leon406:: Visitor's Count" />
</p>

------

## Function

### Encoding  [wiki](https://github.com/Leon406/ToolsFx/wiki/%E7%BC%96%E8%A7%A3%E7%A0%81(Encoding))

- [x] base64
- [x] urlBase64
- [x] base16/32/36/45/58/62/85/91/92/100
- [x] base58check
- [x] UrlEncode
- [x] Unicode
- [x] js hex(\x61)/js octal(\140)
- [x] binary/octal/decimal/hex
- [x] custom base serial dict
- [x] puny code
- [x] quote printable
- [x] uuEncode
- [x] xxEncode
- [x] escape/escapeAll
- [x] auto decode(crack)

![encode](./art/encode.gif)

![one key decode](./art/one_key_decode.gif)

**String Process(e.g. Split)**

![encode_split](./art/encode_split.gif)

### Encoding Transfer (not raw data)

- [x] Transfer

![encode](./art/encode_transfer.gif)

### Digest(Hash)

support file, big file which is larger than 8Gi

- [x] md serial
- [x] sha1
- [x] sha2
- [x] sha3
- [x] SM3
- [x] RIPEMD
- [x] whirlpool
- [x] Tiger
- [x] dictionary hash mapping(crack)
- [x] etc.

![hash](./art/hash.gif)

### MAC

#### HMAC

- [x] md serial
- [x] sha1
- [x] sha2
- [x] sha3
- [x] SM3
- [x] RIPEMD
- [x] whirpool
- [x] Tiger
- [x] etc.

#### CMAC

- [x] AESCMAC
- [x] BLOWFISHCMAC
- [x] DESCMAC
- [x] DESEDECMAC
- [x] SEED-CMAC
- [x] Shacal-2CMAC
- [x] SM4-CMAC
- [x] Three-fish-256CMAC / Three-fish-512CMAC / Three-fish-1024CMAC

#### GMAC

#### POLY1305

- [x] POLY1305
- [x] POLY1305-AES
- [x] POLY1305-ARIA
- [x] POLY1305-CAMELLIA
- [x] POLY1305-CAST6
- [x] POLY1305-NOEKEON
- [x] POLY1305-RC6
- [x] POLY1305-SEED
- [x] POLY1305-SERPENT
- [x] POLY1305-SM4
- [x] POLY1305-Twofish

### Symmetric Crypto(block cipher)

#### Encrypt Algorithm

- [x] DES/3DES
- [x] AES
- [x] SM4
- [x] Blowfish
- [x] Twofish
- [x] RC2
- [x] etc.

<details>
<summary>support mode</summary>
<ul>
<li>ECB</li>
<li>CBC</li>
<li>OFB(n)</li>
<li>CFB(n)</li>
<li>SIC (also known as CTR)</li>
<li>CTS (equivalent to CBC/WithCTS)</li>
<li>CCM (AEAD)</li>
<li>EAX (AEAD)</li>
<li>GCM (AEAD)</li>
<li>OCB (AEAD)</li>
</ul>
</details>

<details>
<summary>support padding scheme</summary>
<ul>
<li>No padding</li>
<li>PKCS5/7</li>
<li>ISO10126/ISO10126-2</li>
<li>ISO7816-4/ISO9797-1</li>
<li>X9.23/X923</li>
<li>TBC</li>
<li>ZeroByte</li>
<li>withCTS (if used with ECB mode)</li>
</ul>
</details>

![sym](./art/sym.gif)

### Symmetric Crypto (stream cipher)

- [x] RC4
- [x] HC128/HC256
- [x] ChaCha/ChaCha20/ChaCha20-Poly1305
- [x] Salsa20
- [x] XSalsa20
- [x] VMPC
- [x] Grain v1
- [x] Grain128
- [x] Zuc128
- [x] etc.

### Asymmetric Crypto RSA

- [x]  support pkcs1 /pkcs8 key
- [x]  support 512/1024/2048/3072/4096 bit
- [x]  support plain text length longer than key size
- [x]  support public key encrypt and private key encrypt
- [x]  support openssl pkcs1/pkcs8 private key format
- [x]  support certification cer file
- [x]  support pem and pk8 format :new:

![sym](./art/asy.gif)

### Digital Signature

- [x] RSA serial
- [x] DSA
- [x] ECDSA
- [x] EC
- [x] EdDSA(ED448/ED25192)
- [x] SM2
- [ ] other

### Classical Crypto (for CTF) 

- [x] caesar
- [x] rot5/rot13/rot18/rot47
- [x] affine
- [x] virgenene
- [x] atbash
- [x] morse
- [x] qwe keyboard
- [x] polybius
- [x] bacon 24/bacon 26
- [x] one time pad
- [x] socialist core value
- [x] ADFGX/ADFGVX
- [x] Auto Key
- [x] rail-fence normal /rail-fence w-type
- [x] playfair
- [x] brainfuck/troll/ook
- [x] Braille
- [x] alphabet index
- [x] 01248
- [x] BubbleBabble
- [x] Element Periodic Table
- [x] PawnShop Cipher
- [x] Handy code
- [x] Beaufort
- [x] Porta Cipher
- [x] Bifid/Trifid/FourSquare Cipher
- [x] Gronsfeld Cipher
- [x] Gray code
- [x] Buddha Says(佛曰)
- [x] Hill Cipher
- [x] 新佛曰/兽音/熊曰(online)
- [x] rabbit
- [x] aaencode/jjencode
- [x] RSA crack (nec,pqec)
- [x] etc.

![ctf](./art/ctf.gif)
![rsa_nec](./art/rsa_nec.gif)

### PBE

![pbe](./art/pbe.gif)

### misc模块

- timestamp to date
- date to timestamp
- uuid
- port scan
- ip scan
- ping
- tcping

### Others

- [x] Qrcode/OCR
- [x] String Process
- [x] Big Integer Calculator
- [x] ECC Calculator
- [ ] TBD

### Features

- [x] support drag file
- [x] Symmetric Crypto support base64/hex encoded key, iv
- [x] Digest and Symmetric Crypto support multi files
- [x] i18n
- [x] CTF related
- [x] PBE
- [x] module configurable,support online url

[bouncy castle document](https://www.bouncycastle.org/specifications.html)

## Downloads

[GitHub release](https://github.com/Leon406/ToolsFx/releases)

[gitte mirror(for Chinese user)](https://gitee.com/LeonShih/ToolsFx)

download boost https://leon.lanzoui.com/b0d9av2kb code：52pj plugin download https://leon.lanzoub.com/b0d9w4cof 提取码：ax63

### Issues, PRs are welcome!!!

## Version Choose

- with jre environment (for developer)
    - jdk8                         ----   choose suffix with jdk8-all-platform
    - jdk11+                     ----   choose suffix with  jdk17-no-jfx-all-platform & also need to config javafx environment
    
- w/o jre environment(Windows user only,normal user)
    - 64bit Windows       ----   withjre-win-x64(latest LTS version)
    - 32/64bit Windows  ----   jdk8-withjre-windows-x86  (if have no idea, choose this)
    
- beta (jar file, for geeker)
  copy jar file to lib directory and delete ToolsFx-xxx.jar or app-xx.jar
  
- [github action nightly(same as beta,build when code change)](https://github.com/Leon406/ToolsFx/actions/workflows/app-test.yml)

    [nightly download ](https://nightly.link/Leon406/ToolsFx/workflows/app-test/dev/artifact.zip)

## How to Run

- Linux/macOS double-click ToolsFx in root directory
- Windows double-click ToolsFx.bat or vbs file(remove black command window)

## How to Config

When Application is running ,it will generate ToolsFx.properties automatically , just modify the value. Below are the
details.

| key                     | value                                                |
| ----------------------- |------------------------------------------------------|
| isEnableClassical       | Classical module switch,default is false             |
| isEnablePBE             | PBE module switch,default is false                   |
| isEnableSignature       | Signature module switch,default is  true             |
| isEnableMac             | MAC module switch,default is  true                   |
| isEnableSymmetricStream | Symmetric( Stream) module switch,default is  true    |
| isEnableQrcode          | Qrcode module switch,default is  true                |
| isEnableInternalWebview | Internal Browser switch,default is false             |
| offlineMode             | offline mode, hide online functions,default is false |
| uiScale                 | Application UI scale rate, default is -1             |
| extUrls                 | Internal Browser favourite urls, spit with comma     |

## [PLUGIN](README-plugin.md)

- ApiPost Network Debug Tools

## CHANGE LOG

see [changelog.md](changelog.md)

## CREDIT

[bouncy castle](https://github.com/bcgit/bc-java)

[tornadofx](https://github.com/edvin/tornadofx)

[JetBrains](https://jb.gg/OpenSourceSupport)

[badge maker](https://shields.io/)

## DONATE

[donate](https://afdian.net/a/leon406) (now only support alipay and wechat pay)

## Stargazers over time

[![Stargazers over time](https://starchart.cc/Leon406/ToolsFx.svg)](https://starchart.cc/Leon406/ToolsFx)

## LICENSE

```
ISC License

Copyright (c) 2021, Leon406

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
```

[Go Top](#top)

