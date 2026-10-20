package com.zhifou.fortune

internal data class DisclosureItem(val title: String, val description: String, val url: String? = null)

internal data class DisclosureSection(val title: String, val items: List<DisclosureItem>)

internal object AppDataDisclosure {
    val sections = listOf(
        DisclosureSection("公开来源与授权", listOf(
            DisclosureItem("农历与黄历 · lunar-java",
                "农历、干支、节气、传统节日、宜忌和传统方位由 lunar-java 在本机计算，组件采用 MIT 许可证。不同黄历流派可能存在差异，应用不计算法定调休。",
                "https://github.com/6tail/lunar-java"),
            DisclosureItem("周易原文 · Kanseki Repository",
                "卦辞、爻辞、《彖》《象》等离线语料整理自 KR1a0001《周易》正文。已去除页码、合并片段并规范章节标识；改编语料依 CC BY-SA 4.0 授权，保留署名与相同方式共享要求。来源方不为本应用背书。",
                "https://www.kanripo.org/text/KR1a0001/"),
            DisclosureItem("古籍参考 · 中国哲学书电子化计划",
                "《道德经》《尚书·洪范》《周易·说卦》等古籍原文用于简短引文与思想参考。现代建议由应用独立编写，不冒充原典或现代译本；相关网站的编排、译注等仍须遵守各自使用条款。",
                "https://ctext.org/zh"),
            DisclosureItem("韦特塔罗牌面 · Wikimedia Commons",
                "完整牌组正面及原十字牌阵牌背采用来源站标注为公有领域的历史韦特图像，绘者为 Pamela Colman Smith，初版于 1909 年。四牌塔罗的金属箔牌背为应用原创。公有领域状态以来源说明及适用地区法律为准。",
                "https://commons.wikimedia.org/wiki/Category:Rider-Waite_tarot_deck_(Roses_%26_Lilies)"),
            DisclosureItem("塔罗文献 · The Pictorial Key to the Tarot",
                "牌义与十字牌阵参考 Arthur Edward Waite 于 1910 年出版的原著。中文牌义、用途和背景介绍为应用编写的现代概括，不是商业译本的复制。",
                "https://en.wikisource.org/wiki/The_Pictorial_Key_to_the_Tarot"),
            DisclosureItem("四牌塔罗 · 现代十字布局",
                "小工具中的四牌十字阵以现状、阻碍、助力、趋向为四个牌位，采用现代通行布局，不冒称韦特原著中的十张牌十字阵。抽取固定后，牌位应用与综合建议由应用编写。",
                "https://taroterra.com/en/rozklady-taro/khrest/"),
            DisclosureItem("离线语音 · Whisper 与 sherpa-onnx",
                "本机识别使用 Whisper 多语言模型与 sherpa-onnx 运行组件，分别遵循 MIT 与 Apache 2.0 许可证；语音识别可能存在误差，请在发送前核对。",
                "https://github.com/k2-fsa/sherpa-onnx"),
            DisclosureItem("答案之书与现代释义",
                "答案之书条目、生活化建议和日常推荐由应用独立整理编写，并非直接复制市售《答案之书》。这些内容是反思提示，不是经典原文或事实判断。"),
            DisclosureItem("第三方组件与许可清单",
                "日历、牌面、文本、语音、渲染与物理组件的版本、原始来源和授权信息集中列于项目第三方声明中；各项第三方权利不因本应用的说明而改变。",
                "https://github.com/moka992/zhifou-fortune/blob/main/THIRD_PARTY_NOTICES.md"),
        )),
        DisclosureSection("计算与解读", listOf(
            DisclosureItem("今日节奏与今日关照",
                "按日期、离线黄历中的日常宜忌、日干与节月五行关系作一致的规则判断，不通过随机数生成每日指数。同义事项去重，宜忌冲突时增加复核，依据不足时保持中性。分值是应用自行设计的文化参考刻度，并非古籍中的评分公式。"),
            DisclosureItem("日常内容筛选",
                "首页建议与每日推荐只采用经审核的日常事项，不把丧葬等条目拼成面向用户的生活建议。完整黄历详情保留传统原始条目，不能据此推断个人命运。"),
            DisclosureItem("个人资料的作用",
                "昵称用于称呼，生日用于年度节点提示，关注词用于建议侧重；修改这些资料不会随机改变今日指数。不提供个人资料、位置或天气，也可使用离线功能。"),
            DisclosureItem("铜钱卦",
                "在本机独立模拟每次三枚铜钱，自初爻至上爻累计六爻，记录阴阳、动爻、本卦与变卦。取用参照《易学启蒙·考变占》所载框架，这是传统方法之一，不声称是唯一标准。"),
            DisclosureItem("韦特塔罗与答案之书",
                "塔罗在本机洗牌、无重复抽牌并生成正逆位，按固定牌位结合本地牌义解释。答案之书从本地条目中随机抽取。随机抽取与每日指数的规则计算是不同机制。"),
            DisclosureItem("AI 补充解读",
                "AI 解释本机已确定的结果，不负责更改抽牌、起卦或分值。事业、关系、财务按当日分项缓存解读，避免重复请求；模型生成的表述可能不同于经典原义，也可能出错。"),
        )),
        DisclosureSection("数据与隐私", listOf(
            DisclosureItem("本机保存",
                "个人资料、日程、聊天、占卜记录和接口配置保存在本机，不参与系统云备份。占卜记录可在“我的 > 占卜记录”中清空。应用不含广告或第三方用户追踪组件。"),
            DisclosureItem("可选 AI 服务",
                "使用 AI 功能时，问题、相关结果、必要的对话上下文及解读所需的个人资料会发送至你配置的平台。API Key 保存在本机，仅用于向该服务认证；连接测试同样需要联网，是否计费以平台规则为准。"),
            DisclosureItem("语音与图片",
                "离线识别不上传录音。开启云端转写后，松手确认的录音才会发往所选平台。背景图片通过系统选择器授权，不要求访问整个媒体库。"),
            DisclosureItem("第三方处理与分享",
                "云端平台的数据留存、用途和费用受其自身条款约束。分享截图、日志或反馈前，请遮盖姓名、生日、日程、聊天、密钥等信息。卸载前请确认重要记录已妥善保留。"),
        )),
        DisclosureSection("使用声明", listOf(
            DisclosureItem("文化参考，不是结果保证",
                "本应用用于传统文化学习、娱乐与自我反思。黄历宜忌、方位、节奏指数及占卜结果不构成科学预测，也不是吉凶、成功或事件发生的概率，不代表任何必然结果。"),
            DisclosureItem("保留独立判断",
                "经典原文、后世流派、应用的现代释义与 AI 生成内容应分别理解，不能混同为权威结论。请依据现实信息自主判断，不因分值或占卜结果恐惧、歧视他人或采取冒险行为。"),
            DisclosureItem("不替代专业意见",
                "所有功能均不提供医疗诊断、法律意见或投资承诺。涉及健康、安全、财务或法律等重要事项，请以专业意见、实际证据及适用规则为准。"),
        )),
    )
}
