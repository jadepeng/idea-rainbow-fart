# idea-rainbow-fart
一个在你编程时疯狂称赞你的 IDEA扩展插件,兼容VSCode版本语音包 | An IDEA extension that keeps giving you compliment while you are coding, it will checks…

独家支持TTS合成，你可以自定义关键词和播报文本。

## 更新

 - 1.0.2 
    - 支持设置语音包类型： TTS(在线语音合成)，Custom(自定义), Builtin(内置)
    - 支持支持通过在线语音合成播报，支持自定义关键词和文本，同时语音合成还支持自定义语音合成发音人.

 - 1.0.1 
    - 支持内置语音包，来源于 https://github.com/SaekiRaku/vscode-rainbow-fart
    - 支持自定义语音包
    
## 使用说明


### 使用内置语音包

打开设置：

- 将voice package type设置为builtin
- 可以选择内置语音包，共三个，一个官方的中文和英文，一个tts合成的（志玲姐姐）

![彩虹屁设置](./docs/builtin.png)


### 使用第三方语音包：

![彩虹屁设置](./docs/custom.png)

- 将voice package type设置为custom
- 可以到 [https://github.com/topics/vscode-rainbow-fart](https://github.com/topics/vscode-rainbow-fart) 查找语音包。

点击确定生效：

### 使用TTS（推荐）

本插件特色功能，支持自定义关键词和文本，鼠标点击表格可以修改关键词和回复语，修改时enter回车换行，一行代表一个

![彩虹屁设置](./docs/tts.png)

TTS 使用科大讯飞提供的流式API。


## 鸣谢

- 插件参考,感谢原作者的贡献
    - 对插件开发不熟悉，参考了 https://github.com/izhangzhihao/intellij-rainbow-fart
    - 语音包引用 https://github.com/SaekiRaku/vscode-rainbow-fart