var fontFamily = "'Microsoft YaHei',SimHei,FangSong,simsun,tahoma,arial,sans-serif";
var tags = ["html", "body", "div", "button", "span", "select","","a", "p", "h1", "h2", "h3", "h4","li", "h5", "h6", "input", "code","textarea"];
for (var i = 0; i < tags.length; i++) {
    console.log(tags[i]);
    var eles = document.getElementsByTagName(tags[i]);
    for (var j = 0; j < eles.length; j++) {
        eles[j].style.fontFamily = fontFamily;
    }
}
