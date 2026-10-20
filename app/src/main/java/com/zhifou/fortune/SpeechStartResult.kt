package com.zhifou.fortune

internal enum class SpeechStartResult(val message: String?) {
    Started(null),
    Recording("正在录音，请先松手结束录音"),
    Transcribing("上一段语音正在转写，请稍候再录音"),
    Finishing("上一段录音正在结束，请稍后再试"),
    ModelUnavailable("离线语音模型尚未就绪，请稍后再试"),
    PermissionDenied("需要麦克风权限才能使用语音输入"),
    MicrophoneUnavailable("无法启动麦克风，请检查麦克风权限及是否被其他应用占用"),
}
