# 知否运势

知否运势是一个独立 Android 手机应用，面向普通用户提供每日运势、三枚铜钱占卜、答案之书和本地历史记录。

这个项目从赛博占卜的用户侧能力拆出，不依赖硬件终端、不请求蓝牙权限，也不包含旧项目中的 BLE、语音模型和设备配网流程。

## 功能

- 今日运势：基于日期、昵称和个人关键词生成稳定的每日提示。
- 三枚铜钱占卜：本地随机起卦，展示本卦、变卦、动爻和行动建议。
- 答案之书：随机抽取短答案与建议。
- 历史记录：占卜结果保存在本机 SharedPreferences。
- 设置：昵称和生日/长期关键词用于个性化每日运势。

## 技术栈

- Kotlin
- Jetpack Compose
- Android Gradle Plugin 8.6.1
- minSdk 26 / targetSdk 35

## 构建

首次构建前需要在 Android Studio 中安装 Android SDK Platform 35 和 Build Tools，并接受 Android SDK 许可证。

```bash
cd /Users/wangyusen/知否运势
./gradlew :app:assembleDebug
```

如果 Gradle 提示找不到 SDK，可在 `local.properties` 中加入：

```properties
sdk.dir=/Users/wangyusen/Library/Android/sdk
```

`local.properties` 不应提交到 Git。
