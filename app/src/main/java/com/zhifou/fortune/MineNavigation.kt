package com.zhifou.fortune

internal enum class MinePage(val title: String) {
    Overview("我的"),
    History("占卜记录"),
    Settings("设置"),
    Appearance("外观与背景"),
    Interaction("交互反馈"),
    Ai("AI 解读与对话"),
    Speech("语音输入"),
    Privacy("数据来源与说明");

    val parent: MinePage?
        get() = when (this) {
            Overview -> null
            History, Settings -> Overview
            Appearance, Interaction, Ai, Speech, Privacy -> Settings
        }
}
