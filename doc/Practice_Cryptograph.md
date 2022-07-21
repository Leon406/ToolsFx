## [JWT](http://www.ruanyifeng.com/blog/2018/07/json_web_token-tutorial.html) 

[Json web token (JWT)](https://datatracker.ietf.org/doc/html/rfc7519)

```css
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
```

由三段信息构成的，将这三段信息文本用`.`链接一起就构成了Jwt字符串

### header

- 声明类型，这里是jwt
- 声明加密的算法 通常直接使用 HMAC SHA256

```bash
{
  'typ': 'JWT',
  'alg': 'HS256'
}
```

进行base64编码

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
```

### payload

- 标准中注册的声明
  - **标准中注册的声明** (建议但不强制使用) ：
    - **iss**: jwt签发者
    - **sub**: jwt所面向的用户
    - **aud**: 接收jwt的一方
    - **exp**: jwt的过期时间，这个过期时间必须要大于签发时间
    - **nbf**: 定义在什么时间之前，该jwt都是不可用的.
    - **iat**: jwt的签发时间
    - **jti**: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
- 公共的声明
- 私有的声明

```
{
  "sub": "1234567890",
  "name": "John Doe",
  "admin": true
}

```

进行base64编码

```
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9
```

### signature

- header (base64后的)
- payload (base64后的)
- secret

HS256(base64UrlEncode(header) +"."+base64UrlEncode(payload) ,secret)

```
TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
```

