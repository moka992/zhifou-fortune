package com.zhifou.fortune

enum class TarotWikiFilter(val label: String) {
    ALL("全部"),
    MAJOR("大牌"),
    WANDS("权杖"),
    CUPS("圣杯"),
    SWORDS("宝剑"),
    PENTACLES("星币"),
}

data class TarotWikiTopic(
    val title: String,
    val body: String,
)

data class TarotWikiEntry(
    val card: TarotCard,
    val deckRole: String,
    val artworkAndSymbols: String,
    val usage: String,
    val interpretation: String,
    val historicalBackground: String,
    val distinction: String,
    val reflectionPrompts: List<String>,
) {
    val searchText: String = buildString {
        append(card.nameZh)
        append(' ')
        append(card.nameEn)
        append(' ')
        append(card.classificationLabel)
        append(' ')
        append(card.uprightKeywords.joinToString(" "))
        append(' ')
        append(card.reversedKeywords.joinToString(" "))
        append(' ')
        append(deckRole)
        append(' ')
        append(artworkAndSymbols)
        append(' ')
        append(usage)
        append(' ')
        append(interpretation)
        append(' ')
        append(historicalBackground)
        append(' ')
        append(distinction)
    }.lowercase()
}

object TarotWikiLibrary {
    val overviewTopics = listOf(
        TarotWikiTopic(
            "78 张牌的结构",
            "韦特塔罗由 22 张大阿尔卡那和 56 张小阿尔卡那组成。小阿尔卡那分为权杖、圣杯、宝剑、星币四组，每组包含王牌至十，以及侍从、骑士、王后、国王四张宫廷牌。大牌更常用于观察阶段性主题与转折，小牌更贴近日常处境、行为和关系。",
        ),
        TarotWikiTopic(
            "四组花色的区别",
            "权杖侧重行动、意志与创造；圣杯侧重情感、关系与直觉；宝剑侧重思考、沟通与冲突；星币侧重资源、工作、身体与现实基础。相同数字在不同花色中处于相似的进程阶段，但发生在不同生活领域。",
        ),
        TarotWikiTopic(
            "正位与逆位",
            "正位通常表示牌的力量较直接、外显或顺畅；逆位可表示内化、延迟、阻塞、失衡或过度。逆位不等同于坏结果，正位也不保证好结果。实际解读仍要结合问题、牌阵位置和相邻牌。",
        ),
        TarotWikiTopic(
            "历史脉络",
            "塔罗最早是 15 世纪欧洲的纸牌游戏；它与占卜和神秘学的广泛关联主要形成于 19 世纪。Arthur Edward Waite 与画家 Pamela Colman Smith 合作的这副牌于 1909 年问世。Smith 为小阿尔卡那绘制的叙事场景，使数字牌也能通过人物、动作与环境来阅读。",
        ),
        TarotWikiTopic(
            "本 Wiki 的资料方法",
            "牌名、结构和图像以 Rider-Waite-Smith 原始牌面为准；历史框架参考博物馆研究，牌面与占卜含义以 Waite 的《The Pictorial Key to the Tarot》为基础并作现代中文释义。内容用于学习、反思与文化研究，不代替医疗、法律、财务等专业判断。",
        ),
    )

    val entries: List<TarotWikiEntry> by lazy {
        TarotDeck.cards.map(::buildEntry).also { builtEntries ->
            check(builtEntries.size == 78)
            check(builtEntries.map { it.card.id }.distinct().size == 78)
            builtEntries.forEach { entry ->
                check(entry.deckRole.isNotBlank())
                check(entry.artworkAndSymbols.isNotBlank())
                check(entry.usage.isNotBlank())
                check(entry.interpretation.isNotBlank())
                check(entry.historicalBackground.isNotBlank())
                check(entry.distinction.isNotBlank())
                check(entry.reflectionPrompts.size == 3)
            }
        }
    }

    private val entriesById by lazy { entries.associateBy { it.card.id } }

    fun entry(cardId: String): TarotWikiEntry = requireNotNull(entriesById[cardId]) {
        "Unknown tarot wiki card: $cardId"
    }

    fun search(query: String, filter: TarotWikiFilter): List<TarotWikiEntry> {
        val normalizedQuery = query.trim().lowercase()
        return entries.filter { entry ->
            matchesFilter(entry.card, filter) &&
                (normalizedQuery.isEmpty() || entry.searchText.contains(normalizedQuery))
        }
    }

    private fun matchesFilter(card: TarotCard, filter: TarotWikiFilter): Boolean = when (filter) {
        TarotWikiFilter.ALL -> true
        TarotWikiFilter.MAJOR -> card.arcana == TarotArcana.MAJOR
        TarotWikiFilter.WANDS -> card.suit == TarotSuit.WANDS
        TarotWikiFilter.CUPS -> card.suit == TarotSuit.CUPS
        TarotWikiFilter.SWORDS -> card.suit == TarotSuit.SWORDS
        TarotWikiFilter.PENTACLES -> card.suit == TarotSuit.PENTACLES
    }

    private fun buildEntry(card: TarotCard): TarotWikiEntry {
        return if (card.arcana == TarotArcana.MAJOR) {
            buildMajorEntry(card)
        } else {
            buildMinorEntry(card)
        }
    }

    private fun buildMajorEntry(card: TarotCard): TarotWikiEntry {
        val profile = requireNotNull(majorProfiles[card.number])
        return TarotWikiEntry(
            card = card,
            deckRole = "大阿尔卡那第 ${card.number} 号。${profile.role}",
            artworkAndSymbols = "${card.imageDescription}${profile.symbolism}",
            usage = profile.usage,
            interpretation = interpretationFrame(card),
            historicalBackground = profile.history,
            distinction = profile.distinction,
            reflectionPrompts = reflectionPrompts(card, profile.questionFocus),
        )
    }

    private fun buildMinorEntry(card: TarotCard): TarotWikiEntry {
        val suit = requireNotNull(card.suit)
        val suitProfile = requireNotNull(suitProfiles[suit])
        val rankProfile = requireNotNull(rankProfiles[card.number])
        val previous = TarotDeck.cards.firstOrNull { it.suit == suit && it.number == card.number - 1 }
        val next = TarotDeck.cards.firstOrNull { it.suit == suit && it.number == card.number + 1 }
        val sequenceContrast = when {
            previous != null && next != null ->
                "在${suit.label}序列中，它承接${previous.nameZh}的阶段，并把情势推向${next.nameZh}。"
            previous == null -> "它是${suit.label}序列的起点，后续由${next?.nameZh}把潜能带入关系或选择。"
            else -> "它是${suit.label}宫廷牌的成熟终点，前一阶段${previous.nameZh}更偏向内在掌握。"
        }
        return TarotWikiEntry(
            card = card,
            deckRole = "${suit.label}组的${card.rankLabel}，把“${rankProfile.stage}”放在${suit.domain}的领域中观察。",
            artworkAndSymbols = "${card.imageDescription}Smith 用具体场景呈现${card.uprightKeywords.joinToString("、")}之间的关系，使这张牌不只依赖花色数量，也能从人物姿态、环境与行动方向来阅读。",
            usage = "适合检视${suitProfile.questionDomain}。当它落在牌阵的现状、阻力、建议或结果位置时，应分别理解为当前模式、待处理的张力、可采取的态度或在条件延续下的趋势。",
            interpretation = interpretationFrame(card),
            historicalBackground = "${suitProfile.history}${rankProfile.history}在 Rider-Waite-Smith 体系中，${card.nameZh}以“${card.imageDescription.removeSuffix("。")}”把这一阶段改写成可观察的生活场景。",
            distinction = "${suitProfile.distinction}${rankProfile.distinction}$sequenceContrast",
            reflectionPrompts = reflectionPrompts(card, suitProfile.questionDomain),
        )
    }

    private fun interpretationFrame(card: TarotCard): String =
        "这张牌的核心不是单一吉凶，而是“${card.uprightKeywords.joinToString("、")}”如何在问题中运作。正位可读作：${card.uprightMeaning}逆位可读作：${card.reversedMeaning}解读时应先看牌阵位置，再用相邻牌确认这种力量是资源、阻力还是正在变化的过程。"

    private fun reflectionPrompts(card: TarotCard, focus: String): List<String> = listOf(
        "在${focus}中，什么最符合“${card.uprightKeywords.first()}”？",
        "“${card.reversedKeywords.first()}”是现实限制，还是我尚未处理的内在反应？",
        "下一步有哪些事实可以核对，又有哪些小行动可以验证判断？",
    )

    private data class MajorProfile(
        val role: String,
        val symbolism: String,
        val usage: String,
        val history: String,
        val distinction: String,
        val questionFocus: String,
    )

    private val majorProfiles = mapOf(
        0 to MajorProfile(
            "它代表进入经验之前的开放状态，以及自由与风险同时存在的起点。",
            "悬崖强调边界，白犬既可视为提醒也可视为同行本能；轻装与白玫瑰把纯粹动机和未经验证的可能放在一起。",
            "适合询问新计划、旅行、身份转换及是否值得尝试；落在建议位时重在开放探索，同时检查底线与准备。",
            "愚人在早期塔罗中常是无固定序号的特殊牌。RWS 将其标为 0，使它既能位于序列之前，也能被理解为穿行于整组大牌的自由变量。",
            "它与魔术师都能表示开始；愚人偏向尚未定形的可能，魔术师则强调已经集中并可运用的能力。",
            "新的开始与可接受的风险",
        ),
        1 to MajorProfile(
            "它象征把意图、知识和已有资源组织成可执行行动。",
            "抬起与下指的双手建立上下之间的通道，桌上的四种花色说明不同资源都已进入工作台，无限符号强调持续的专注。",
            "适合询问能力、执行、沟通和资源整合；建议位通常要求明确目标、工具和第一步。",
            "此牌承接早期塔罗中的“杂耍者”或“魔术师”形象。RWS 强化仪式工作者的姿态，使主题从机巧扩展到意志与资源的有意识运用。",
            "与愚人相比，它更有计划；与女祭司相比，它把力量向外表达，而女祭司把知识保留在内在与未显之处。",
            "我能实际调动的能力和资源",
        ),
        2 to MajorProfile(
            "它位于行动之后，代表尚未公开的知识、直觉和等待成熟的信息。",
            "黑白双柱呈现对立，帷幕提示边界，卷轴与月亮把隐秘知识、周期和不完全显露的答案联系起来。",
            "适合询问信息是否完整、直觉是否可信以及何时应保持沉默；它更支持观察和核对，不鼓励仓促定论。",
            "它由早期塔罗的“女教皇”形象演变而来。RWS 改用 High Priestess 的名称，并将其置于更明确的神秘知识与内在意识语境中。",
            "与魔术师的主动显化不同，女祭司强调接收和保留；与月亮相比，她的沉静更有秩序，月亮则更突出不确定与投射。",
            "尚未显露的信息与内在判断",
        ),
        3 to MajorProfile(
            "它代表孕育、照料、感官经验和让事物持续生长的环境。",
            "麦田、流水、森林与金星符号共同表现生命力、丰饶、关系和创造过程；柔软环境并不等于没有边界。",
            "适合询问创作、关系培育、家庭与身体照料；建议位常要求提供时间、资源和可持续的滋养。",
            "皇后属于早期塔罗就已存在的世俗权力形象。RWS 将她与自然、金星和母性创造力紧密结合，扩大了传统宫廷身份之外的含义。",
            "与皇帝相比，她通过滋养形成秩序，皇帝通过结构形成秩序；与星币王后相比，她是更广泛的生命原则，后者更贴近日常资源管理。",
            "需要被培育的人、关系或作品",
        ),
        4 to MajorProfile(
            "它代表规则、边界、责任与可以承载长期目标的结构。",
            "石座和裸露山地表现稳定与严峻，羊首联系主动意志；铠甲说明秩序也承担保护与防御功能。",
            "适合询问管理、责任、制度和边界；建议位要求把模糊愿望改写成规则、权限与可执行安排。",
            "皇帝是早期塔罗的核心世俗权力牌之一。RWS 保留统治者主题，并以更强的石质、山地和白羊象征突出结构化意志。",
            "与皇后相比，它强调框架而非滋养；与教皇相比，皇帝维护世俗秩序，教皇更偏向传统知识和共同信念。",
            "需要被明确的规则、责任和边界",
        ),
        5 to MajorProfile(
            "它代表传统、传授、共同价值与通过成熟体系学习的方法。",
            "双钥匙、祝福手势和求教者构成知识传承关系，也提醒制度既能提供路径，也可能要求服从。",
            "适合询问学习、资格、组织规范和价值共识；建议位可指向导师、经典或已验证流程。",
            "它由早期塔罗的“教皇”牌发展而来。RWS 使用 Hierophant 这一名称，将重点从特定宗教职位转向仪式、传统与知识传递。",
            "与皇帝的世俗规则不同，它强调意义和传承；与隐士相比，它通过群体和导师学习，隐士通过独处与经验寻找答案。",
            "值得遵循或重新审视的传统",
        ),
        6 to MajorProfile(
            "它把关系、吸引和价值选择放在同一张牌中，要求选择与承诺一致。",
            "伊甸园式场景、天使与两棵不同的树让欲望、知识、关系和后果同时出现，强调连结并非脱离选择。",
            "适合询问关系、合作和价值冲突；它不只回答“是否相爱”，也检验双方选择能否承担现实后果。",
            "早期“恋人”牌常包含爱情或选择场景。RWS 采用亚当、夏娃与天使的构图，将个人吸引提升为价值、自由选择与结合的主题。",
            "与圣杯二相比，它更强调人生价值与重大选择；圣杯二更聚焦双方的直接交换与互信。",
            "关系与选择背后的共同价值",
        ),
        7 to MajorProfile(
            "它代表在相反力量之间维持方向，并以自律推动局面。",
            "黑白斯芬克斯没有明显缰绳，说明前进依赖驾驭者的内在协调；城市与星冠把出发、目标和身份联系起来。",
            "适合询问推进、竞争、迁移和自我控制；建议位强调先统一方向，再增加速度。",
            "战车作为凯旋形象长期存在于大牌序列。RWS 用相反颜色的斯芬克斯替代普通马匹，突出意志对矛盾力量的整合。",
            "与力量牌相比，战车通过方向和控制前进，力量牌通过耐心和关系驯服冲动；与权杖骑士相比，它更重视持续驾驭。",
            "正在互相拉扯但必须被协调的力量",
        ),
        8 to MajorProfile(
            "它代表温和而持续的勇气，以及与本能合作而非压制本能。",
            "女子与狮子的接触没有暴力，无限符号把耐心、意识和持久力量置于身体冲动之上。",
            "适合询问信心、情绪调节、健康恢复和冲突处理；建议位通常要求稳定、耐心和柔韧边界。",
            "力量源自塔罗中的德性牌“坚毅”。RWS 将力量编号为 VIII、正义编号为 XI，这与常见马赛体系的次序不同；这也是辨认 RWS 序列的重要特征。",
            "与战车相比，它不靠速度或控制取胜；与皇帝相比，它的权威来自自我掌握，而不是外部规则。",
            "需要耐心驯服而非强行压制的冲动",
        ),
        9 to MajorProfile(
            "它代表暂时离开喧闹，以经验、审慎和有限但可靠的光寻找方向。",
            "灯只照亮脚前一段路，手杖提供现实支撑；高处与灰袍说明距离、简朴和经验的沉淀。",
            "适合询问研究、独处、导师和长期方向；建议位支持缩小范围、深入调查并保留必要距离。",
            "此牌与早期序列中的“老人”或时间形象有关。RWS 将它明确塑造成携灯的隐士，使其从衰老主题转向经验、求索与指引。",
            "与女祭司相比，隐士依靠经验搜索，女祭司依靠接收与直觉；与宝剑四相比，隐士是主动求索，宝剑四更偏休整。",
            "独处能帮助我看清的核心问题",
        ),
        10 to MajorProfile(
            "它代表周期、条件变化和个人无法完全控制的转折。",
            "轮盘、升降形象和四角读书者把变动与持续法则并置，说明机会与反复都发生在更大周期中。",
            "适合询问时机、转折和重复模式；建议位要求辨认周期、调整位置，而不是假设一切都能靠意志控制。",
            "命运之轮来自中世纪广泛流传的 Fortune's Wheel 图像，在早期塔罗中已经出现。RWS 叠加字母、炼金与守护形象，强化周期背后的秩序。",
            "与世界相比，它是周期中的转动而非完成；与审判相比，它更偏外部条件变化，审判更偏清醒回应与决定。",
            "正在发生的周期与我能调整的位置",
        ),
        11 to MajorProfile(
            "它代表基于事实、比例和后果的判断，以及对选择负责。",
            "天平衡量，直剑裁断，正面端坐的姿态强调公开、清楚和不可回避的责任。",
            "适合询问合同、决定、公平与责任；建议位要求补足证据，统一标准，并接受选择的实际后果。",
            "正义是早期塔罗中的德性牌。RWS 将其编号为 XI，与力量 VIII 配对；常见马赛序列通常把正义列为 VIII。",
            "与宝剑国王相比，正义是更普遍的因果与衡量原则；宝剑国王则是具体人物或制度如何执行理性权威。",
            "事实、标准与我应承担的后果",
        ),
        12 to MajorProfile(
            "它代表主动暂停、换位观察和通过放下旧控制获得新理解。",
            "倒悬姿势与平静神情形成反差，光环提示这不是普通受害，而是改变观看方式后的觉察。",
            "适合询问停滞、牺牲、等待和观点冲突；建议位常意味着暂缓推进，并检验付出是否真正有意义。",
            "早期“倒吊人”常带有惩罚叛徒或羞辱的社会意涵。RWS 保留倒悬姿态，却加入光环和平静表情，将重点转向自愿暂停与视角转换。",
            "与宝剑二的僵局相比，它更主动地接受暂停；与死神相比，它先改变视角，死神则要求阶段真正结束。",
            "需要暂停或换一个角度理解的事",
        ),
        13 to MajorProfile(
            "它代表不可逆的阶段结束、形式转换和为下一阶段腾出空间。",
            "骷髅骑士面向不同身份的人前进，倒下的王冠说明地位不能阻止变化；远处日出提示结束与新阶段并存。",
            "适合询问离开、转型、关系或工作阶段的结束；通常不应把它字面理解为死亡预言。",
            "死亡作为第 XIII 张大牌在早期塔罗中已有稳定位置，部分旧牌甚至不印标题。RWS 延续骷髅形象，并加入远方日出以强化转化而非单纯毁灭。",
            "与高塔相比，死神是阶段性终结与过渡，高塔是结构被突然事实击穿；与世界相比，死神重在放下，世界重在整合完成。",
            "已经结束却仍被我抓住的阶段",
        ),
        14 to MajorProfile(
            "它代表调配、节制、修复和在差异之间形成可持续比例。",
            "两杯之间的水流表现交换与调和，一脚入水一脚着地把感受与现实连接起来，远方道路强调渐进。",
            "适合询问健康习惯、协作、资源配比和关系磨合；建议位要求减少极端，通过小幅迭代寻找平衡。",
            "节制是传统德性牌之一，在早期塔罗中已有重要位置。RWS 保留倒水动作，并用天使、路径和双重落脚强化调和过程。",
            "与正义的精确衡量不同，节制是流动中的调试；与星币二相比，它是更深层的整合，后者更偏日常调度。",
            "哪些差异需要通过调整比例来整合",
        ),
        15 to MajorProfile(
            "它代表依附、欲望、恐惧和那些看似牢固却可能被看见并松开的束缚。",
            "松弛锁链说明束缚并非完全无解，倒置象征与火把把本能、权力和失去觉察后的依赖放在一起。",
            "适合询问成瘾、控制、权力关系和物质执着；建议位要求诚实识别收益、代价与可退出的环节。",
            "恶魔形象在后期塔罗中逐渐稳定。RWS 借鉴有角神怪与倒置象征，并让人物锁链保持可脱离状态，突出参与者与束缚之间的关系。",
            "与恋人牌都有人物成对出现，但恋人强调自由选择与价值一致，恶魔强调选择如何被欲望、恐惧或依赖缩窄。",
            "我明知有代价却仍难以离开的模式",
        ),
        16 to MajorProfile(
            "它代表不稳固结构被突然揭穿，以及真相迫使系统重建。",
            "雷击、坠落王冠和破裂塔身让外部冲击与内部缺陷同时可见，火焰和坠落人物强调旧位置无法维持。",
            "适合询问危机、意外揭示和制度失败；建议位重在保护基本安全、承认事实并优先重建关键结构。",
            "塔牌在欧洲传统中有“上帝之屋”等不同名称和图像。RWS 采用遭雷击的高塔，延续傲慢结构被击破的视觉传统。",
            "与死神相比，高塔更突然、更外显；与宝剑十相比，高塔针对整体结构，宝剑十更像某一冲突或思维模式触底。",
            "哪些结构看似稳定却缺少真实基础",
        ),
        17 to MajorProfile(
            "它代表风暴后的希望、坦诚、修复和重新建立长期信任。",
            "裸体人物表示不加遮掩，双重水流同时滋养水域与土地；一颗主星和七颗小星把方向与节奏结合。",
            "适合询问恢复、创作愿景和长期目标；建议位支持诚实、持续滋养和以小进展重建信心。",
            "星星属于早期塔罗的天体牌组。RWS 将倒水人物置于开阔自然中，使希望与实际滋养、循环和脆弱中的开放相连。",
            "与太阳相比，星星是安静而长期的恢复，太阳是已变得清晰可见的活力；与圣杯王牌相比，它的修复范围更广。",
            "值得长期滋养的希望与恢复路径",
        ),
        18 to MajorProfile(
            "它代表信息不完整时的想象、投射、潜意识和不稳定感。",
            "犬与狼、双塔与曲折小路构成驯化和本能的边界；水中生物出现，说明深层内容正在进入意识。",
            "适合询问模糊关系、梦、焦虑和隐情；建议位要求放慢判断、验证信息并区分感受与事实。",
            "月亮是传统天体大牌。RWS 的犬狼、双塔与水中生物构成鲜明叙事，使旧有月亮象征转向潜意识、边界与不确定的旅程。",
            "与女祭司相比，月亮更容易出现投射和迷失；与宝剑七相比，月亮未必有人刻意隐瞒，也可能只是信息和感受尚未清楚。",
            "哪些判断可能受恐惧、想象或信息缺口影响",
        ),
        19 to MajorProfile(
            "它代表清晰、生命力、公开表达与可以共同确认的成果。",
            "无遮掩的孩童、白马、红旗和向日葵把坦率、成长与可见性放在明亮环境中。",
            "适合询问成果、活力、家庭与公开表达；建议位鼓励把信息说清，并让进展可以被看见和验证。",
            "太阳属于传统天体牌组。RWS 以孩童、白马与向日葵重构画面，使光明不只是天象，也成为直接、生命力和成长的经验。",
            "与星星相比，太阳更外显、更接近确认；与圣杯十相比，太阳强调个人与整体生命力，圣杯十更侧重共享情感结构。",
            "已经能够被公开确认的事实和成果",
        ),
        20 to MajorProfile(
            "它代表复盘之后的觉醒、回应召唤和对过去作出新的判断。",
            "号角唤起不同人物，打开的棺木强调旧状态被重新审视；红十字旗把讯息与回应连接起来。",
            "适合询问重大决定、复盘、第二次机会和身份召唤；建议位要求从经验中形成判断，而非继续被旧评价困住。",
            "此牌承接基督教“最后审判”的传统图像。RWS 保留号角与复起人物，并在现代解读中常被用于觉醒、复盘与回应。",
            "与正义相比，审判是经历后的召唤与重估，正义是当下依据事实衡量；与世界相比，它是完成前的确认和回应。",
            "过去经验正在要求我作出的清醒回应",
        ),
        21 to MajorProfile(
            "它代表一个周期的完成、不同部分的整合和进入更广阔阶段。",
            "花环形成边界也形成入口，舞者手持双杖，四角形象呼应固定秩序，说明完成并非静止而是协调后的流动。",
            "适合询问完成、毕业、长期项目和跨阶段转变；建议位强调收尾、整合经验并确认新的边界。",
            "世界通常是传统大牌序列中的最高王牌。RWS 延续花环与四角形象，并让中央人物保持动态，表现完成后的持续展开。",
            "与命运之轮相比，世界是周期闭合后的整合；与权杖四相比，它的完成范围更全面，后者多为阶段性稳定与庆祝。",
            "哪些部分已经具备完整收尾与整合条件",
        ),
    )

    private data class SuitProfile(
        val questionDomain: String,
        val history: String,
        val distinction: String,
    )

    private val suitProfiles = mapOf(
        TarotSuit.WANDS to SuitProfile(
            "行动、动力、创作、事业推进和意志投入",
            "权杖承接意大利纸牌的棍棒花色；在现代 RWS 语境中常与火、行动和创造性意志关联。",
            "与同数字的圣杯相比它更重行动，与宝剑相比更重动力而非判断，与星币相比更重启动而非沉淀。",
        ),
        TarotSuit.CUPS to SuitProfile(
            "情感、关系、直觉、归属感和内在满足",
            "圣杯承接意大利纸牌的杯花色；在现代 RWS 语境中常与水、情绪、关系和直觉经验关联。",
            "与同数字的权杖相比它更重感受，与宝剑相比更重连结而非分析，与星币相比更重内在价值而非现实资源。",
        ),
        TarotSuit.SWORDS to SuitProfile(
            "思考、语言、决定、边界、冲突和事实核验",
            "宝剑承接意大利纸牌的剑花色；在现代 RWS 语境中常与风、思维、沟通、判断和冲突关联。",
            "与同数字的权杖相比它更重判断，与圣杯相比更重事实与表达，与星币相比更重观念和决定。",
        ),
        TarotSuit.PENTACLES to SuitProfile(
            "资源、工作、金钱、身体、技能和长期现实基础",
            "星币承接意大利纸牌的钱币花色；RWS 使用五芒星币图案，现代语境常与土、物质条件、身体和劳动关联。",
            "与同数字的权杖相比它更重落实，与圣杯相比更重现实条件，与宝剑相比更重可衡量的资源和结果。",
        ),
    )

    private data class RankProfile(
        val stage: String,
        val history: String,
        val distinction: String,
    )

    private val rankProfiles = mapOf(
        1 to RankProfile(
            "潜能出现与一个周期的种子",
            "王牌在牌组中表示花色力量的集中起点；RWS 四张王牌都以云中之手呈现尚待承接的机会。",
            "王牌关注“有什么开始出现”，尚不等于已经形成稳定结果。",
        ),
        2 to RankProfile(
            "两极、交换、选择或初步平衡",
            "数字二把单一潜能带入两个位置之间的关系，可能表现为合作、权衡或张力。",
            "二比王牌更强调关系与选择，又尚未达到三的扩展与初步成果。",
        ),
        3 to RankProfile(
            "形成、扩展与可被看见的初步结果",
            "数字三常把前一阶段的关系推进为协作、表达或增长；具体表现由花色决定。",
            "三比二更具外向发展，也比四更开放、尚未完全稳定。",
        ),
        4 to RankProfile(
            "稳定、边界、停驻与结构形成",
            "数字四常表现一个暂时稳定的结构，这种稳定既可能提供安全，也可能变成封闭。",
            "四比三更稳定；与五相比，它仍处在冲突或变化打破结构之前。",
        ),
        5 to RankProfile(
            "结构受到挑战后的摩擦、损失或调整",
            "数字五常让既有稳定遭遇现实考验，因此四个花色的五都带有不同形式的紧张。",
            "五不是固定的坏结果，而是从四的稳定进入必须回应的变化。",
        ),
        6 to RankProfile(
            "在挑战之后重新分配、过渡或恢复秩序",
            "数字六经常表现五的冲突之后出现的移动、支持、认可或记忆重组。",
            "六比五更有恢复方向，但尚未进入七所代表的再次检验。",
        ),
        7 to RankProfile(
            "检验、评估、选择与立场确认",
            "数字七常要求面对多个可能或评估已有投入，重点是判断而非立即完成。",
            "七比六更需要个人立场；与八相比，它仍停留在选择、评估或守位。",
        ),
        8 to RankProfile(
            "力量进入持续运作、移动或训练",
            "数字八常把花色主题带入有节奏的过程，可能是快速推进、熟练练习，也可能是限制结构。",
            "八通常比七更有持续方向，并为九的累积结果做准备。",
        ),
        9 to RankProfile(
            "接近完成时的累积、独立与最后考验",
            "数字九位于十之前，常同时包含成果与压力：已经走得很远，也需要判断如何完成。",
            "九是接近终点的个人经验，十则把结果扩展为完整周期或更大的结构。",
        ),
        10 to RankProfile(
            "一个花色周期的完成、饱和与向下一周期过渡",
            "数字十把花色主题推到完整或过量状态，因此可能同时出现成果、责任和必须转化的负担。",
            "十比九更完整，也更容易显出累积后的系统性后果。",
        ),
        11 to RankProfile(
            "侍从式的学习、消息、好奇与尚未成熟的尝试",
            "侍从承接欧洲宫廷牌的年轻随从或步兵角色；在解读中常表示学习者、消息或某种花色力量的初次人格化。",
            "侍从重学习和消息；骑士更重移动与执行，王后和国王则代表更成熟的掌握。",
        ),
        12 to RankProfile(
            "骑士式的移动、追求、任务与花色力量的主动推进",
            "骑士是传统四张宫廷牌之一，坐骑的动作和方向是 RWS 中判断其速度与风格的重要视觉线索。",
            "骑士比侍从更主动但可能更极端；与国王相比，它重在追求过程而非稳定治理。",
        ),
        13 to RankProfile(
            "王后式的内在掌握、维系环境与成熟回应",
            "王后是传统宫廷牌的一部分；RWS 通过宝座、环境和手中花色物来表现她如何容纳并运用该元素。",
            "王后通常偏向内在掌握和关系环境；国王更偏外部决策、组织与责任。",
        ),
        14 to RankProfile(
            "国王式的外在掌握、治理、责任与结果承担",
            "国王是每组宫廷牌的最高等级；RWS 以王座、服饰和周围环境显示不同花色的权威风格。",
            "国王表示较稳定的外部掌握；骑士更像执行中的力量，王后更偏内在成熟和情境维系。",
        ),
    )
}
