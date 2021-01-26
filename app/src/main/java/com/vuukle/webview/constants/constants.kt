package com.vuukle.webview.constants

object constants {

    const val powerBarUrl = "https://cdntest.vuukle.com/widgets/powerbar.html?amp=false&apiKey=664e0b85-5b2c-4881-ba64-3aa9f992d01c&host=relaxed-beaver-76304e.netlify.com&articleId=Index&img=https%3A%2F%2Fwww.gettyimages.ie%2Fgi-resources%2Fimages%2FHomepage%2FHero%2FUK%2FCMS_Creative_164657191_Kingfisher.jpg&title=Index&url=https%3A%2F%2Frelaxed-beaver-76304e.netlify.app%2F&tags=123&author=123&lang=en&gr=false&darkMode=false&defaultEmote=1&items=&standalone=0&mode=horizontal"
    const val jsDisableZoom = "document.getElementsByTagName('head')[0].innerHTML = document.getElementsByTagName('head')[0].innerHTML + \"<meta name='viewport' content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0'/>\""
    const val profileScreenScript = "document.getElementsByClassName(\"upload-cont\")[0].children[0].style.display = 'none'};"
    const val addPowerBarScript = "var iframeTop = document.createElement('iframe'); iframeTop.setAttribute('style', 'width:100%;border:none;height:80px;'); iframeTop.setAttribute('src', '$powerBarUrl'); document.getElementsByTagName('body')[0].prepend(iframeTop)"
}