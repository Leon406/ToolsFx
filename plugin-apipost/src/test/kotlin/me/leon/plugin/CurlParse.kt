package me.leon.plugin

import org.junit.Test

class CurlParse {
    @Test
    fun parse() {

        //https://httpbin.org/
        //GET
        "curl https://catonmat.net"
        "curl -o response.txt https://catonmat.net" //Send a GET Request and Save the Response to a File
        "curl -G --data-urlencode 'comment=this cookbook is awesome' https://catonmat.net" //query
        "curl -G -d 'q=kitties' -d 'count=20' https://google.com/search"
        "curl -H 'Accept-Language: en-US' https://google.com"
        "curl -H 'Accept-Language: en-US' -H 'Secret-Message: xyzzy' https://google.com"
        "curl -H 'Puppies;' https://google.com"
        "curl -A '' https://google.com"
        "curl -A 'Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0' https://google.com"
        "curl -b 'session=abcdef' -b 'loggedin=true' https://google.com"
        "curl -L http://catonmat.net" //redirect
        "curl -u 'bob:12345' https://google.com/login" //Authorization: Basic Ym9iOjEyMzQ1
        "curl -k https://catonmat.net" //Don't Verify SSL Certificate Details
        "curl -e 'https://google.com?q=cats' http://catonmat.net" //Add a Referrer

        "curl -I https://catonmat.net" //HEAD
        //POST
        "curl -d 'login=emma&password=123' -X POST https://google.com/login"
        "curl -X POST https://catonmat.net"
        "curl -d 'login=emma' -d 'password=123' https://google.com/login"
        "curl -d 'login=emma&password=123' https://google.com/login" //-d 默认post
        ////-d 默认post
        "curl -d '{\"login\": \"emma\", \"pass\": \"123\"}' -H 'Content-Type: application/json' https://google.com/login"
        "curl -d 'hello world' -H 'Content-Type: text/plain' https://google.com/login"
        "curl -d '@data.txt' https://google.com/login" //文件
        "curl -F 'file=@photo.png' https://google.com/profile"
        "curl -F 'file=@photo.png;type=image/png' https://google.com/profile"
        //PROXY
        "curl -x socks4://james:cats@myproxy.com:8080 https://catonmat.net"
        "curl -x james:cats@myproxy.com:8080 https://catonmat.net" //http
    }
}