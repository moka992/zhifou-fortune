package com.zhifou.fortune

import androidx.annotation.DrawableRes
import java.security.SecureRandom
import java.util.Random

enum class TarotArcana(val label: String) {
    MAJOR("大阿尔卡那"),
    MINOR("小阿尔卡那"),
}

enum class TarotSuit(val label: String, val englishName: String, val domain: String) {
    WANDS("权杖", "Wands", "行动、意志与创造力"),
    CUPS("圣杯", "Cups", "情感、关系与直觉"),
    SWORDS("宝剑", "Swords", "思考、沟通与冲突"),
    PENTACLES("星币", "Pentacles", "资源、工作与现实基础"),
}

data class TarotCard(
    val id: String,
    val number: Int,
    val nameZh: String,
    val nameEn: String,
    val arcana: TarotArcana,
    val suit: TarotSuit?,
    val rankLabel: String,
    val imageDescription: String,
    val uprightKeywords: List<String>,
    val reversedKeywords: List<String>,
    val uprightMeaning: String,
    val reversedMeaning: String,
    @DrawableRes val imageRes: Int,
) {
    val classificationLabel: String
        get() = if (arcana == TarotArcana.MAJOR) {
            "${arcana.label} · 第${number}号"
        } else {
            "${arcana.label} · ${suit?.label.orEmpty()} · ${suit?.domain.orEmpty()}"
        }
}

data class TarotPosition(
    val index: Int,
    val name: String,
    val prompt: String,
)

data class TarotDraw(
    val positionIndex: Int,
    val cardId: String,
    val reversed: Boolean,
) {
    val position: TarotPosition
        get() = TarotDeck.positions.first { it.index == positionIndex }

    val card: TarotCard
        get() = TarotDeck.card(cardId)

    val orientationLabel: String
        get() = if (reversed) "逆位" else "正位"

    val keywords: List<String>
        get() = if (reversed) card.reversedKeywords else card.uprightKeywords

    val meaning: String
        get() = if (reversed) card.reversedMeaning else card.uprightMeaning
}

data class TarotLocalResult(
    val title: String,
    val body: String,
    val advice: String,
)

data class TarotAiContext(
    val nickname: String = "",
    val birthDate: String = "",
    val keywords: String = "",
    val dateLabel: String = "",
    val lunarLabel: String = "",
    val dayGanZhi: String = "",
    val solarTerm: String = "",
    val suitable: List<String> = emptyList(),
    val avoid: List<String> = emptyList(),
    val directions: String = "",
    val clash: String = "",
)

object TarotDeck {
    private val secureRandom = SecureRandom()

    val positions = listOf(
        TarotPosition(1, "核心现状", "覆盖问题的主要气氛与当前影响"),
        TarotPosition(2, "交叉阻力", "横跨现状的阻碍、张力或需要整合的力量"),
        TarotPosition(3, "意识目标", "当事人追求的方向、理想与可见目标"),
        TarotPosition(4, "深层基础", "已经存在的根基、动机与较深层原因"),
        TarotPosition(5, "近期过去", "正在减弱或离开的影响"),
        TarotPosition(6, "近期发展", "接下来较近阶段可能进入局面的影响"),
        TarotPosition(7, "自我立场", "当事人的态度、角色与应对方式"),
        TarotPosition(8, "外部环境", "他人、关系与客观环境带来的影响"),
        TarotPosition(9, "希望与担忧", "期待、恐惧以及两者交织之处"),
        TarotPosition(10, "综合趋向", "在当前条件延续时，局面的可能结果与总结"),
    )

    val cards: List<TarotCard> = majorCards() + minorCards()

    private val cardsById = cards.associateBy(TarotCard::id)

    init {
        check(cards.size == 78) { "Rider-Waite deck must contain 78 cards." }
        check(cardsById.size == cards.size) { "Tarot card ids must be unique." }
        check(cards.count { it.arcana == TarotArcana.MAJOR } == 22)
        TarotSuit.entries.forEach { suit ->
            check(cards.count { it.suit == suit } == 14) { "Each tarot suit must contain 14 cards." }
        }
    }

    fun card(id: String): TarotCard = requireNotNull(cardsById[id]) {
        "Unknown tarot card: $id"
    }

    fun drawCelticCross(random: Random = secureRandom): List<TarotDraw> {
        val shuffled = cards.toMutableList()
        for (index in shuffled.lastIndex downTo 1) {
            val other = random.nextInt(index + 1)
            val card = shuffled[index]
            shuffled[index] = shuffled[other]
            shuffled[other] = card
        }
        return positions.mapIndexed { index, position ->
            TarotDraw(
                positionIndex = position.index,
                cardId = shuffled[index].id,
                reversed = random.nextBoolean(),
            )
        }
    }

    fun interpret(draws: List<TarotDraw>, question: String): TarotLocalResult {
        require(draws.size == positions.size)
        val ordered = draws.sortedBy(TarotDraw::positionIndex)
        val core = ordered[0]
        val obstacle = ordered[1]
        val nearFuture = ordered[5]
        val outcome = ordered[9]
        val majorCount = ordered.count { it.card.arcana == TarotArcana.MAJOR }
        val reversedCount = ordered.count(TarotDraw::reversed)
        val dominantSuit = ordered
            .mapNotNull { it.card.suit }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.takeIf { it.value >= 3 }

        val subject = question.trim().takeIf(String::isNotEmpty)
            ?.let { "围绕“${it.take(80)}”，" }
            .orEmpty()
        val structure = buildString {
            append(subject)
            append("核心牌是${core.card.nameZh}${core.orientationLabel}，")
            append(core.meaning)
            append(" 交叉位置的${obstacle.card.nameZh}${obstacle.orientationLabel}提示：")
            append(obstacle.meaning)
            append(" 近期发展由${nearFuture.card.nameZh}${nearFuture.orientationLabel}呈现，")
            append(nearFuture.meaning)
            append(" 综合趋向为${outcome.card.nameZh}${outcome.orientationLabel}，")
            append(outcome.meaning)
        }
        val pattern = buildString {
            append("十张牌中有${majorCount}张大阿尔卡那、${reversedCount}张逆位牌。")
            dominantSuit?.let { (suit, count) ->
                append("${suit.label}出现${count}张，${suit.domain}是本次牌阵较集中的观察维度。")
            }
            if (reversedCount >= 6) {
                append("逆位较多更适合被理解为内在阻力、延迟或尚未整合的力量，而不是简单的坏结果。")
            }
        }
        return TarotLocalResult(
            title = "十字牌阵 · ${outcome.card.nameZh}",
            body = "$structure\n\n$pattern",
            advice = "先核对“${obstacle.position.name}”指出的现实限制，再以一项可验证的小行动回应“${nearFuture.position.name}”。塔罗用于整理视角与选择，不替代事实核验、专业意见或个人决定。",
        )
    }

    private fun majorCards() = listOf(
        major(0, "愚人", "The Fool", "旅人立于崖边，白犬相随，手持白玫瑰与行囊。", "新起点|开放|自发", "轻率|逃避|散乱", "新的经验正在打开，适合带着好奇心出发，同时保留对现实边界的觉察。", "冲动、准备不足或拒绝承担后果，提醒先看清脚下再行动。", R.drawable.tarot_major_00),
        major(1, "魔术师", "The Magician", "人物一手指天一手指地，桌上陈列四种花色的象征物。", "主动|能力|整合", "操控|分心|潜能闲置", "可用资源已经在手，关键在于集中意志并把想法转成具体行动。", "能力没有被妥善使用，也可能出现夸大、操控或目标分散。", R.drawable.tarot_major_01),
        major(2, "女祭司", "The High Priestess", "女祭司端坐黑白柱之间，身后帷幕遮住深层水域。", "直觉|沉静|隐秘", "直觉受阻|表面判断|封闭", "答案尚未完全显露，安静观察与倾听直觉比仓促表态更重要。", "内在信息被压抑或误读，可能过度依赖表象与他人的解释。", R.drawable.tarot_major_02),
        major(3, "皇后", "The Empress", "皇后身处丰饶田野，麦穗、流水与金星符号环绕。", "滋养|创造|丰盛", "依赖|过度照料|创造受阻", "适合培育关系、作品或生活基础，让事物在稳定照料中生长。", "付出失衡或创造力受阻，需把照顾他人与照顾自己重新平衡。", R.drawable.tarot_major_03),
        major(4, "皇帝", "The Emperor", "皇帝坐在石座上，山脉与羊首象征秩序和意志。", "结构|责任|稳定", "僵化|控制|失序", "需要明确边界、规则与责任，以可靠结构承载目标。", "控制欲或僵硬规则正在压缩空间，也可能欠缺应有的纪律与担当。", R.drawable.tarot_major_04),
        major(5, "教皇", "The Hierophant", "宗教导师向两名求教者祝福，钥匙置于脚前。", "传统|学习|价值", "教条|盲从|另寻路径", "可从成熟传统、导师或共同价值中获得方法与秩序。", "既有规范可能变成束缚，宜分辨原则与教条，再决定是否另辟路径。", R.drawable.tarot_major_05),
        major(6, "恋人", "The Lovers", "男女在天使之下相对而立，象征吸引、选择与价值结合。", "连结|选择|一致", "失衡|分歧|不明智选择", "关系或选择要求内在价值与实际行动保持一致。", "期待与价值出现错位，沟通不足或选择没有承担相应责任。", R.drawable.tarot_major_06),
        major(7, "战车", "The Chariot", "驾驭者立于战车，两只不同颜色的斯芬克斯在前。", "方向|意志|推进", "失控|冲突|受阻", "明确方向并协调相反力量，持续自律可推动局面前进。", "力量彼此拉扯或推进过猛，需先恢复方向感与节奏。", R.drawable.tarot_major_07),
        major(8, "力量", "Strength", "女子温和合上狮口，无限符号悬于头顶。", "勇气|耐心|柔韧", "自我怀疑|力量失衡|急躁", "真正的力量来自温和而持续的自我掌控，不必靠强迫取胜。", "信心不足或用力过猛都可能削弱局面，宜先安定情绪与边界。", R.drawable.tarot_major_08),
        major(9, "隐士", "The Hermit", "老人独立山巅，以灯与手杖照亮有限的前路。", "内省|审慎|指引", "孤立|过度谨慎|回避", "暂时退后有助于辨认真正的问题，并从经验中找到可靠指引。", "独处变成隔绝或谨慎变成拖延，需在反思后重新与现实连接。", R.drawable.tarot_major_09),
        major(10, "命运之轮", "Wheel of Fortune", "轮盘被多重象征环绕，表现周期、变动与因果交织。", "转折|周期|机会", "停滞|抗拒变化|反复", "局势处在转折点，理解周期并顺势调整比固守原状更有效。", "相似问题可能循环出现，抗拒变化会放大停滞感。", R.drawable.tarot_major_10),
        major(11, "正义", "Justice", "人物持剑与天平端坐，象征判断、比例与责任。", "公正|责任|判断", "偏见|失衡|逃避责任", "应以事实、比例和后果作判断，并愿意承担选择带来的责任。", "信息或立场可能失衡，也需警惕苛刻判断与推卸责任。", R.drawable.tarot_major_11),
        major(12, "倒吊人", "The Hanged Man", "人物倒悬树上却神情平静，头部有光环。", "暂停|换位|放下", "拖延|抗拒|无效牺牲", "主动暂停与改变视角，可能比继续用旧方法推进更有价值。", "停顿已变成消耗，或为了维持现状而作无意义牺牲。", R.drawable.tarot_major_12),
        major(13, "死神", "Death", "黑甲骑士经过众人，远处太阳在两塔之间升起。", "结束|转化|过渡", "停滞|抗拒结束|难以放手", "某个阶段需要真正结束，腾出的空间才能容纳下一种形式。", "对结束的抗拒让变化停滞，宜辨认已经失去生命力的部分。", R.drawable.tarot_major_13),
        major(14, "节制", "Temperance", "天使在两杯间调和水流，一脚在水中一脚在岸上。", "调和|适度|耐心", "失衡|过量|冲突", "通过配比、协作与耐心调试，可以找到可持续的中间路径。", "节奏或资源配比失衡，极端做法正在增加摩擦。", R.drawable.tarot_major_14),
        major(15, "恶魔", "The Devil", "有角形象俯视被松链束缚的男女，表现依附与欲望。", "束缚|欲望|依赖", "看见束缚|软弱|尚未脱离", "需要诚实看见欲望、恐惧或习惯如何限制选择。", "已经意识到束缚却尚未完全行动，也可能在自由与依赖间反复。", R.drawable.tarot_major_15),
        major(16, "高塔", "The Tower", "雷击高塔，王冠坠落，人物从破裂结构中跌出。", "突变|揭示|结构破裂", "压抑危机|惧怕改变|延迟震荡", "不稳固的结构正在被事实打破，震荡也会暴露真正需要重建之处。", "问题可能被压住而未消失，越回避必要改变，后续调整成本越高。", R.drawable.tarot_major_16),
        major(17, "星星", "The Star", "女子在星空下向水池与土地倾倒清水。", "希望|修复|启发", "灰心|失去信任|自我膨胀", "经历波动后可逐步恢复信任，以坦诚和长期视角重新滋养目标。", "希望感减弱或期待失真，宜回到可验证的小进展。", R.drawable.tarot_major_17),
        major(18, "月亮", "The Moon", "犬与狼在月下相对，小径穿过双塔，生物从水中浮现。", "不确定|想象|潜意识", "迷雾渐散|残余误解|焦虑", "信息仍有模糊处，情绪与想象可能放大风险，暂不宜过早定论。", "部分误解开始显露，但焦虑和惯性判断仍需继续核对。", R.drawable.tarot_major_18),
        major(19, "太阳", "The Sun", "孩童骑白马立于向日葵前，明亮太阳照耀全景。", "清晰|喜悦|活力", "延迟满足|光芒减弱|过度乐观", "局面趋向清楚与开放，适合以真诚和可见成果推进。", "积极因素仍在，但成果可能延迟，需避免把乐观当成已经完成。", R.drawable.tarot_major_19),
        major(20, "审判", "Judgement", "天使吹响号角，众人从棺中起身回应召唤。", "觉醒|复盘|召唤", "犹疑|自我否定|延迟决定", "过去经验正在汇成一次清醒判断，适合回应真正重要的召唤。", "因自我怀疑或害怕评价而迟迟不决，需要区分反省与自我惩罚。", R.drawable.tarot_major_20),
        major(21, "世界", "The World", "人物在花环中起舞，四角出现四种守护象征。", "完成|整合|抵达", "未完成|停滞|收尾延迟", "一个周期具备完成与整合条件，也可能由此进入更广阔阶段。", "关键环节尚未闭合，先完成收尾再追求新的扩张。", R.drawable.tarot_major_21),
    )

    private fun minorCards(): List<TarotCard> =
        wandsCards() + cupsCards() + swordsCards() + pentaclesCards()

    private fun wandsCards() = listOf(
        minor(TarotSuit.WANDS, 1, "王牌", "Ace", "云中之手握住萌芽权杖，远处城堡与山地展开。", "灵感|开端|创造", "启动受阻|热情下降|机会未用", "行动的火花已经出现，适合把灵感落实为第一个具体步骤。", "开端缺少条件或热情无法聚焦，先补足资源而非勉强启动。", R.drawable.tarot_wands_01),
        minor(TarotSuit.WANDS, 2, "二", "Two", "人物立于城墙，手握地球，在两根权杖间眺望远方。", "规划|选择|掌控", "顾虑|视野受限|意外", "已有初步成果，接下来需要在不同方向间作出有远见的选择。", "对未知的顾虑限制了规划，也需为突发变化保留余地。", R.drawable.tarot_wands_02),
        minor(TarotSuit.WANDS, 3, "三", "Three", "人物背向观者看船远行，三根权杖立在高地。", "拓展|远见|协作", "延迟|摩擦|计划受阻", "先前投入开始向外拓展，合作与更长视角会放大成果。", "进展晚于预期或合作衔接不顺，应检查路线与时机。", R.drawable.tarot_wands_03),
        minor(TarotSuit.WANDS, 4, "四", "Four", "四根权杖撑起花环，人群在远处城堡前庆祝。", "稳定|庆祝|归属", "基础不稳|私人喜悦|过渡", "阶段成果值得确认，稳定的关系与环境提供了休整空间。", "表面庆祝下仍有基础问题，或喜悦更适合在较小范围内分享。", R.drawable.tarot_wands_04),
        minor(TarotSuit.WANDS, 5, "五", "Five", "五名青年各执权杖交错挥动，场面像竞赛也像争执。", "竞争|摩擦|磨合", "暗中冲突|争执未解|避免对抗", "不同意志正在碰撞，竞争也可用来检验方案与能力。", "冲突转入隐性或迟迟不解决，需重建清楚规则。", R.drawable.tarot_wands_05),
        minor(TarotSuit.WANDS, 6, "六", "Six", "戴桂冠的骑手持杖行进，人群随行庆贺。", "认可|胜利|好消息", "认可延迟|自负|支持动摇", "努力有望得到看见，适合分享成果并承担随之而来的责任。", "外界认可不足或来得较晚，也要防止把面子置于实际之上。", R.drawable.tarot_wands_06),
        minor(TarotSuit.WANDS, 7, "七", "Seven", "人物站在高处，以一根权杖抵挡下方六根权杖。", "守位|勇气|坚持", "压力过大|犹疑|防线松动", "已有位置需要被维护，坚持原则同时保持策略弹性。", "持续防守造成消耗，宜判断哪些战线真正值得投入。", R.drawable.tarot_wands_07),
        minor(TarotSuit.WANDS, 8, "八", "Eight", "八根权杖在空中平行飞过开阔景观。", "速度|推进|消息", "延误|混乱|急躁", "事件进入快速推进期，信息与行动需要及时对齐。", "延误、误传或过度急切容易打乱节奏，先确认再加速。", R.drawable.tarot_wands_08),
        minor(TarotSuit.WANDS, 9, "九", "Nine", "受伤人物倚杖警戒，身后八根权杖形成防线。", "韧性|边界|最后坚持", "疲惫|戒备过度|阻碍", "经验让你更懂得保护边界，再坚持一段但不必独自硬撑。", "疲惫削弱判断，或过去伤害造成过度戒备，应先恢复体力。", R.drawable.tarot_wands_09),
        minor(TarotSuit.WANDS, 10, "十", "Ten", "人物弯身抱着十根权杖走向城镇。", "负担|责任|接近完成", "过载|难以放手|责任失衡", "目标接近完成，但承担过多已压缩选择空间，需要重新分配。", "负担超过承受范围，继续全揽只会降低完成质量。", R.drawable.tarot_wands_10),
        minor(TarotSuit.WANDS, 11, "侍从", "Page", "青年在荒地中观察手中权杖，衣袍饰有火蜥蜴。", "探索|消息|热情", "不稳定|坏消息|缺少方向", "新的想法或消息带来探索动力，适合先试验再扩大。", "热情来得快去得也快，消息可能不完整，需避免草率承诺。", R.drawable.tarot_wands_11),
        minor(TarotSuit.WANDS, 12, "骑士", "Knight", "骑士策马扬起权杖，姿态充满速度与火元素。", "行动|冒险|变化", "鲁莽|中断|冲突", "行动力明显增强，适合主动推进，但仍要看清路线。", "速度压过判断，可能引发中断、争执或反复转向。", R.drawable.tarot_wands_12),
        minor(TarotSuit.WANDS, 13, "王后", "Queen", "王后手持向日葵与权杖，黑猫伏在脚边。", "自信|温暖|独立", "嫉妒|对立|表里不一", "以坦率、自信和务实热情影响周围，同时保留独立判断。", "自信可能转为防御或嫉妒，关系中需减少试探与比较。", R.drawable.tarot_wands_13),
        minor(TarotSuit.WANDS, 14, "国王", "King", "国王坐在饰有狮子与火蜥蜴的王座上，手握权杖。", "领导|远见|担当", "强硬|支配|急于求成", "适合以远见、诚实与承担后果的方式领导行动。", "权威变得强硬或只要求结果，需让目标与执行条件重新匹配。", R.drawable.tarot_wands_14),
    )

    private fun cupsCards() = listOf(
        minor(TarotSuit.CUPS, 1, "王牌", "Ace", "云中之手托起圣杯，水流倾入池中，白鸽从上方降下。", "情感开启|丰沛|直觉", "情绪受阻|失衡|内在变化", "新的情感、关系或直觉经验正在打开，适合真诚接纳与表达。", "感受被压住或向外溢出，先照顾内在容量再寻求回应。", R.drawable.tarot_cups_01),
        minor(TarotSuit.CUPS, 2, "二", "Two", "一对人物交换圣杯，上方是双蛇杖与有翼狮首。", "互相吸引|合作|共识", "误解|关系失衡|虚假和谐", "双方具有建立互信与合作的可能，关键是平等交换。", "关系中的承诺、期待或沟通不对称，需要把分歧说清。", R.drawable.tarot_cups_02),
        minor(TarotSuit.CUPS, 3, "三", "Three", "三名女子高举圣杯围成圆圈，脚边是丰收果实。", "友谊|庆祝|支持", "过度享乐|小圈子|分心", "社群、朋友与共同庆祝能带来支持，也标志一个小阶段完成。", "社交可能流于消耗或排他，需分清真正支持与短暂热闹。", R.drawable.tarot_cups_03),
        minor(TarotSuit.CUPS, 4, "四", "Four", "人物坐在树下，对云中递来的圣杯无动于衷。", "倦怠|内省|忽略机会", "重新投入|新关系|觉察", "情绪疲倦让现有机会难以被看见，适合先辨认真实需要。", "注意力开始回到外部，新机会可被重新评估，但无需急于接受。", R.drawable.tarot_cups_04),
        minor(TarotSuit.CUPS, 5, "五", "Five", "黑斗篷人物面对三只倾倒的杯，身后仍有两杯直立。", "失落|悲伤|仍有余地", "修复|回归|难以释怀", "失去值得被承认，但仍有资源与关系没有消失。", "开始从失落中恢复，或反复回看过去而尚未真正转身。", R.drawable.tarot_cups_05),
        minor(TarotSuit.CUPS, 6, "六", "Six", "庭院中孩童递出盛花圣杯，场景带有旧日记忆。", "回忆|善意|过去", "走向未来|离开旧模式|理想化", "过去经验、熟悉关系或单纯善意正在影响当前。", "需要减少对过去的理想化，把经验带入未来而非停留其中。", R.drawable.tarot_cups_06),
        minor(TarotSuit.CUPS, 7, "七", "Seven", "人物面对云中的七只杯，每只杯呈现不同诱惑与象征。", "选择|想象|幻象", "作出决定|聚焦|计划", "选项很多但真假混杂，先辨认价值与代价再选择。", "迷雾开始收束，适合减少选项并把决定转成计划。", R.drawable.tarot_cups_07),
        minor(TarotSuit.CUPS, 8, "八", "Eight", "人物在月下离开整齐排列的八只圣杯，走向山地。", "离开|寻找意义|转向", "回避离开|重返旧处|追逐表面快乐", "已有成果仍无法满足更深需求，离开可能是寻找真实意义的一步。", "明知需要改变却难以动身，或用短暂满足回避核心问题。", R.drawable.tarot_cups_08),
        minor(TarotSuit.CUPS, 9, "九", "Nine", "人物满意地坐在九只排列整齐的圣杯前。", "满足|愿望达成|享受", "过量|不满足|愿望失真", "阶段愿望有实现条件，可以享受成果并保持适度。", "表面拥有仍未带来满足，需检查愿望是否真正属于自己。", R.drawable.tarot_cups_09),
        minor(TarotSuit.CUPS, 10, "十", "Ten", "家庭在彩虹下举手欢庆，彩虹中排列十只圣杯。", "情感圆满|家庭|共同幸福", "关系冲突|表面和谐|价值分裂", "共享价值与稳定关系构成长期满足的基础。", "理想化的和谐掩盖了分歧，需要重建真实沟通。", R.drawable.tarot_cups_10),
        minor(TarotSuit.CUPS, 11, "侍从", "Page", "青年端详杯中探出的鱼，海面在身后起伏。", "感性消息|好奇|想象", "情绪幼稚|诱惑|消息失真", "意外的情感讯息或创意值得倾听，以开放但不失判断的方式回应。", "情绪表达不成熟或消息带有想象成分，暂不宜过度解读。", R.drawable.tarot_cups_11),
        minor(TarotSuit.CUPS, 12, "骑士", "Knight", "骑士缓行并托举圣杯，像在递出邀请。", "邀请|浪漫|想象力", "不切实际|欺瞒|情绪反复", "有人或某种机会以温和方式靠近，适合用感受与价值共同判断。", "承诺可能过于理想化，需验证言辞后面的实际行动。", R.drawable.tarot_cups_12),
        minor(TarotSuit.CUPS, 13, "王后", "Queen", "王后临水凝视封闭华丽的圣杯，宝座饰有水元素。", "共情|直觉|情感智慧", "依赖|情绪混乱|不可靠", "细腻感受有助于理解未说出口的内容，同时需要清晰边界。", "共情变成情绪卷入或依赖，宜把感受与事实分开处理。", R.drawable.tarot_cups_13),
        minor(TarotSuit.CUPS, 14, "国王", "King", "国王坐在海中石座上，浪涛与船只在身后。", "情绪稳定|包容|成熟", "操控|双重态度|情感失控", "面对波动仍能保持稳定，以成熟、负责的方式容纳感受。", "平静可能只是表面，情绪操控或回避责任值得警惕。", R.drawable.tarot_cups_14),
    )

    private fun swordsCards() = listOf(
        minor(TarotSuit.SWORDS, 1, "王牌", "Ace", "云中之手竖起宝剑，剑尖托着王冠与枝叶。", "清晰|真相|突破", "混乱|伤害性言辞|力量滥用", "清楚判断与直接沟通能够切开混乱，形成新的认知起点。", "思路或信息失真，锋利表达也可能先造成伤害。", R.drawable.tarot_swords_01),
        minor(TarotSuit.SWORDS, 2, "二", "Two", "蒙眼人物在海边交叉双剑，背后水面平静。", "僵局|权衡|艰难选择", "真相显露|失衡|决定瓦解", "信息不足或双方势均力敌，暂缓决定可争取核实空间。", "被压住的矛盾开始显露，继续回避已难以维持平衡。", R.drawable.tarot_swords_02),
        minor(TarotSuit.SWORDS, 3, "三", "Three", "三把剑贯穿雨幕中的红心。", "心痛|分离|事实刺痛", "恢复|混乱|伤口未愈", "令人难受的事实需要被看见，诚实承认痛感是修复的开始。", "伤口进入恢复期但仍有残余混乱，不必强迫自己立刻释怀。", R.drawable.tarot_swords_03),
        minor(TarotSuit.SWORDS, 4, "四", "Four", "骑士像躺在墓台上休息，一把剑在下方、三把悬于墙上。", "休整|暂停|恢复", "不安|重返行动|休息不足", "暂时撤离噪声有助于恢复判断力并准备下一阶段。", "休息被焦虑打断或过早重返行动，应重新评估体力。", R.drawable.tarot_swords_04),
        minor(TarotSuit.SWORDS, 5, "五", "Five", "一人收起宝剑露出复杂笑容，另外两人离开海岸。", "冲突|代价|空洞胜利", "懊悔|冲突延续|尝试和解", "即使赢得争执也可能损害关系，需评估胜利的真实代价。", "冲突的后果尚未消散，若要和解需先停止重复伤害。", R.drawable.tarot_swords_05),
        minor(TarotSuit.SWORDS, 6, "六", "Six", "船夫载着两人渡水，六把剑竖立船头。", "过渡|离开困境|渐进变化", "停滞|旧问题随行|转变未完", "正在从困难环境转向较平静处，变化缓慢但方向明确。", "地理或形式改变了，旧问题却仍被带在身上，需要完成内在转变。", R.drawable.tarot_swords_06),
        minor(TarotSuit.SWORDS, 7, "七", "Seven", "人物抱走五把剑并回望营地，另有两剑留在原处。", "策略|隐秘|迂回", "坦白|策略失效|需要建议", "灵活策略可能必要，但隐瞒与捷径会增加后续风险。", "计划容易被看穿或良心压力增加，诚实沟通可能更有效。", R.drawable.tarot_swords_07),
        minor(TarotSuit.SWORDS, 8, "八", "Eight", "蒙眼被缚人物被八把剑围住，脚边地面仍留有通路。", "受限|自我束缚|困惑", "松绑|看见选择|残余焦虑", "限制真实存在，但其中一部分来自认知与恐惧，仍有可移动空间。", "开始看见出口，解除束缚需要具体行动而不只是想通。", R.drawable.tarot_swords_08),
        minor(TarotSuit.SWORDS, 9, "九", "Nine", "人物夜间从床上惊醒，九把剑横列墙面。", "忧虑|失眠|内疚", "恐惧缓解|羞愧|持续怀疑", "反复思虑正在放大压力，适合把可验证事实与想象分开。", "焦虑有机会减轻，但羞愧或怀疑仍需要被温和处理。", R.drawable.tarot_swords_09),
        minor(TarotSuit.SWORDS, 10, "十", "Ten", "人物倒地被十把剑贯穿，远处天空已出现晨光。", "痛苦终点|结束|触底", "逐步恢复|旧痛回返|短暂改善", "某种模式已到极限，承认结束才能开始恢复。", "最坏阶段可能过去，但恢复尚不稳固，勿急于重复旧模式。", R.drawable.tarot_swords_10),
        minor(TarotSuit.SWORDS, 11, "侍从", "Page", "青年在强风中举剑警觉站立，云层快速移动。", "警觉|求知|观察", "流言|准备不足|过度戒备", "保持好奇和警觉有助于发现关键信息，先调查再发言。", "消息可能未经证实，过度戒备或传播流言会制造额外冲突。", R.drawable.tarot_swords_11),
        minor(TarotSuit.SWORDS, 12, "骑士", "Knight", "骑士在狂风下举剑冲锋，马匹全速前进。", "果断|迅速|勇敢", "鲁莽|失控|能力不足", "清晰目标能释放强大行动力，适合快速处理明确问题。", "冲锋速度超过判断与能力，容易造成不必要的破坏。", R.drawable.tarot_swords_12),
        minor(TarotSuit.SWORDS, 13, "王后", "Queen", "王后侧坐举剑，另一只手向前伸出，天空云层清晰。", "独立|清醒|界限", "尖刻|偏执|苦涩", "以经验形成的清晰判断能保护边界，也要保留理解他人的空间。", "受伤经验让判断变得尖锐，需避免把防御当成唯一真相。", R.drawable.tarot_swords_13),
        minor(TarotSuit.SWORDS, 14, "国王", "King", "国王正坐高举宝剑，王座与天空表现理性权威。", "理性权威|公正|原则", "滥权|冷酷|操控判断", "应依据原则、证据与长期后果作决定，而非一时情绪。", "理性被用作控制工具，权威或规则可能失去公正。", R.drawable.tarot_swords_14),
    )

    private fun pentaclesCards() = listOf(
        minor(TarotSuit.PENTACLES, 1, "王牌", "Ace", "云中之手托起星币，下方花园通向远山。", "现实机会|资源|稳固开端", "机会流失|资源误用|物质焦虑", "可触及的资源或机会正在出现，适合从可验证的小基础开始建设。", "现实条件尚未落实，或过度关注得失而错用已有资源。", R.drawable.tarot_pentacles_01),
        minor(TarotSuit.PENTACLES, 2, "二", "Two", "人物以无限符号环绕两枚星币，身后船只在浪中起伏。", "调度|变化|保持平衡", "超载|失序|勉强维持", "多项事务需要灵活调度，平衡来自持续调整而非静止。", "任务超过容量，表面轻松难以掩盖实际失衡，应减少并行事项。", R.drawable.tarot_pentacles_02),
        minor(TarotSuit.PENTACLES, 3, "三", "Three", "工匠在建筑内与两人讨论，三枚星币嵌于拱顶。", "技艺|协作|认可", "敷衍|合作不良|水平不足", "专业能力通过协作与反馈得到看见，适合按共同标准推进。", "分工或质量标准不清，个人技巧也可能没有被认真投入。", R.drawable.tarot_pentacles_03),
        minor(TarotSuit.PENTACLES, 4, "四", "Four", "人物紧抱一枚星币，头顶与脚下还有三枚。", "持有|安全|边界", "控制过度|资源阻塞|放手", "守住资源和边界能带来安全，但也要让价值保持流动。", "因害怕失去而过度控制，反而让资源与关系失去活力。", R.drawable.tarot_pentacles_04),
        minor(TarotSuit.PENTACLES, 5, "五", "Five", "两人在雪中经过彩色教堂窗，显得疲惫而匮乏。", "困难|被排除|支持在旁", "缓慢恢复|混乱|求助", "现实压力或孤立感明显，但附近可能仍有尚未使用的支持。", "困境开始出现出口，恢复需要主动求助与重建实际基础。", R.drawable.tarot_pentacles_05),
        minor(TarotSuit.PENTACLES, 6, "六", "Six", "商人手持天平向两名跪者分配钱币。", "给予与接受|公平交换|援助", "不平等|债务|带条件的给予", "资源交换应兼顾需要、能力与尊严，也要看清双方位置。", "帮助可能附带控制或交换严重失衡，需明确边界与偿还条件。", R.drawable.tarot_pentacles_06),
        minor(TarotSuit.PENTACLES, 7, "七", "Seven", "农人倚杖观察结满星币的藤蔓，等待收成。", "评估|耐心|长期投入", "焦躁|回报不佳|重新配置", "投入已进入等待与评估阶段，宜依据长期回报决定下一步。", "迟迟不见成果引发焦躁，应检查方法而非继续机械投入。", R.drawable.tarot_pentacles_07),
        minor(TarotSuit.PENTACLES, 8, "八", "Eight", "工匠专注雕刻星币，完成品整齐悬挂。", "练习|技艺|专注工作", "敷衍|虚荣|技巧误用", "重复练习与专注细节会形成可靠能力，过程本身就是积累。", "为了速度或认可而省略基本功，容易产生表面完成的结果。", R.drawable.tarot_pentacles_08),
        minor(TarotSuit.PENTACLES, 9, "九", "Nine", "女子站在丰盛葡萄园中，戴手套的手上停着猎鹰。", "独立|成果|自律享受", "依赖|成果受损|不可靠手段", "自律与长期积累带来较高自主性，可以从容享受成果。", "独立基础可能不稳，或为了维持体面而依赖不可持续方式。", R.drawable.tarot_pentacles_09),
        minor(TarotSuit.PENTACLES, 10, "十", "Ten", "多代家庭在拱门下相聚，星币按生命之树结构排列。", "传承|长期稳定|家庭资源", "基础动摇|传承冲突|短期得失", "长期积累、家庭或组织结构提供支持，决策宜考虑跨周期影响。", "资源或价值传承出现裂缝，短期利益可能损害长期稳定。", R.drawable.tarot_pentacles_10),
        minor(TarotSuit.PENTACLES, 11, "侍从", "Page", "青年在田野中专注托举星币，远处土地等待耕作。", "学习|落实|现实消息", "分心|浪费|不利消息", "适合认真学习并把目标拆成可执行步骤，现实信息值得核实。", "注意力不足或资源使用松散，计划需要更具体的执行框架。", R.drawable.tarot_pentacles_11),
        minor(TarotSuit.PENTACLES, 12, "骑士", "Knight", "骑士骑在静止黑马上，双手稳稳托着星币。", "可靠|耐心|持续执行", "停滞|懒散|机械重复", "稳定、负责与按部就班比追求速度更能产生长期结果。", "稳定变成停滞，重复工作缺少检验，需在可靠性中加入调整。", R.drawable.tarot_pentacles_12),
        minor(TarotSuit.PENTACLES, 13, "王后", "Queen", "王后在繁茂花园中凝视星币，兔子出现在脚边。", "务实照料|富足|安全感", "不安|过度操心|资源焦虑", "以务实照料创造安全感，兼顾资源、身体与日常环境。", "为维持安全承担太多，或内在不安让现实判断变窄。", R.drawable.tarot_pentacles_13),
        minor(TarotSuit.PENTACLES, 14, "国王", "King", "国王坐在葡萄与牛首装饰的王座上，城堡在身后。", "经营能力|稳定成果|现实掌控", "贪婪|腐化|固守利益", "经验与经营能力可以把资源转成稳定成果，宜以责任约束权力。", "对利益的固守削弱判断，也要防范不透明或不可持续的获利方式。", R.drawable.tarot_pentacles_14),
    )

    private fun major(
        number: Int,
        nameZh: String,
        nameEn: String,
        imageDescription: String,
        uprightKeywords: String,
        reversedKeywords: String,
        uprightMeaning: String,
        reversedMeaning: String,
        @DrawableRes imageRes: Int,
    ) = TarotCard(
        id = "major_${number.toString().padStart(2, '0')}",
        number = number,
        nameZh = nameZh,
        nameEn = nameEn,
        arcana = TarotArcana.MAJOR,
        suit = null,
        rankLabel = number.toString(),
        imageDescription = imageDescription,
        uprightKeywords = uprightKeywords.split('|'),
        reversedKeywords = reversedKeywords.split('|'),
        uprightMeaning = uprightMeaning,
        reversedMeaning = reversedMeaning,
        imageRes = imageRes,
    )

    private fun minor(
        suit: TarotSuit,
        number: Int,
        rankZh: String,
        rankEn: String,
        imageDescription: String,
        uprightKeywords: String,
        reversedKeywords: String,
        uprightMeaning: String,
        reversedMeaning: String,
        @DrawableRes imageRes: Int,
    ) = TarotCard(
        id = "${suit.name.lowercase()}_${number.toString().padStart(2, '0')}",
        number = number,
        nameZh = "${suit.label}$rankZh",
        nameEn = "$rankEn of ${suit.englishName}",
        arcana = TarotArcana.MINOR,
        suit = suit,
        rankLabel = rankZh,
        imageDescription = imageDescription,
        uprightKeywords = uprightKeywords.split('|'),
        reversedKeywords = reversedKeywords.split('|'),
        uprightMeaning = uprightMeaning,
        reversedMeaning = reversedMeaning,
        imageRes = imageRes,
    )
}
