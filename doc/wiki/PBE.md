PBE 基于密码加密, 由hash函数衍生出key和iv

**openssl密码加密特征:**  密文以`U2FsdGVkX1`开头, base64解码为`Salted__`

算法介绍

## [AES](https://www.sojson.com/encrypt_aes.html)

常见基于CryptoJs实现, 使用AES-256-CBC算法及PKCS5Padding/PKCS7Padding,生成key,iv长度为32字节,16字节

对应算法为 `MD5and256bitAES-CBC-OPENSSL`,其他默认即可

## [DES](https://www.sojson.com/encrypt_des.html)

常见基于CryptoJs实现,使用DES-CBC算法及PKCS5Padding/PKCS7Padding,生成key,iv长度为8字节

对应算法为 `MD5andDES`,其他默认即可

## [RC4](https://www.sojson.com/encrypt_rc4.html)

常见基于CryptoJs实现常,使用RC4算法, 生成key长度32字节(openssl 16字节)

对应算法为 `MD5andRC4`,其他默认即可

## [3DES](https://www.sojson.com/encrypt_triple_des.html)

常见基于CryptoJs实现,使用DESede-CBC算法及PKCS5Padding/PKCS7Padding,生成key,iv长度为24字节,8字节

对应算法为 `MD5andTripleDES`,其他默认即可

### 其他算法

- RC2
- TwoFish
- IDEA