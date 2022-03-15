var fontFamily = "tahoma,arial,'Microsoft YaHei',SimHei,FangSong,simsun,sans-serif";
var tags = ["div", "span", "a", "p", "h1", "h2", "h3", "h4", "h5", "h6", "input"];
for (var i = 0; i < tags.length; i++) {
    console.log(tags[i]);
    var eles = document.getElementsByTagName(tags[i]);
    for (var j = 0; j < eles.length; j++) {
        eles[j].style.fontFamily = fontFamily;
    }
}
