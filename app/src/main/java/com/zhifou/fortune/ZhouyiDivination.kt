package com.zhifou.fortune

data class ClassicReference(
    val hexagramNumber: Int,
    val hexagramName: String,
    val hexagramGlyph: String,
    val textType: String,
    val linePosition: Int = 0,
    val text: String,
    val commentary: String = "",
    val isPrimary: Boolean,
)

data class ClassicHexagramContext(
    val number: Int,
    val name: String,
    val glyph: String,
    val judgment: String,
    val tuan: String,
    val image: String,
)

internal data class ZhouyiDivinationSelection(
    val ruleSummary: String,
    val references: List<ClassicReference>,
)

internal object ZhouyiSelectionRules {
    fun select(
        primary: ZhouyiHexagramText,
        transformed: ZhouyiHexagramText,
        movingPositions: List<Int>,
    ): ZhouyiDivinationSelection {
        val moving = movingPositions.distinct().sorted()
        require(moving.all { it in 1..6 }) { "Moving line positions must be in 1..6" }

        return when (moving.size) {
            0 -> ZhouyiDivinationSelection(
                ruleSummary = "六爻皆静，取本卦卦辞。",
                references = listOf(judgment(primary, isPrimary = true)),
            )

            1 -> ZhouyiDivinationSelection(
                ruleSummary = "一爻变，取本卦动爻爻辞。",
                references = listOf(line(primary, moving.single(), isPrimary = true)),
            )

            2 -> {
                val lower = moving.first()
                val upper = moving.last()
                ZhouyiDivinationSelection(
                    ruleSummary = "两爻变，兼看本卦两条动爻爻辞，以上爻为主。",
                    references = listOf(
                        line(primary, upper, isPrimary = true),
                        line(primary, lower, isPrimary = false),
                    ),
                )
            }

            3 -> ZhouyiDivinationSelection(
                ruleSummary = "三爻变，兼看本卦与变卦卦辞，以本卦为体、变卦为用。",
                references = listOf(
                    judgment(primary, isPrimary = true),
                    judgment(transformed, isPrimary = false),
                ),
            )

            4 -> {
                val unchanged = (1..6).filterNot(moving::contains)
                val lower = unchanged.first()
                val upper = unchanged.last()
                ZhouyiDivinationSelection(
                    ruleSummary = "四爻变，取变卦两条静爻爻辞，以下爻为主。",
                    references = listOf(
                        line(transformed, lower, isPrimary = true),
                        line(transformed, upper, isPrimary = false),
                    ),
                )
            }

            5 -> {
                val unchanged = (1..6).single { it !in moving }
                ZhouyiDivinationSelection(
                    ruleSummary = "五爻变，取变卦唯一静爻的爻辞。",
                    references = listOf(line(transformed, unchanged, isPrimary = true)),
                )
            }

            6 -> {
                val useText = primary.useText
                if (useText != null && primary.number in setOf(1, 2)) {
                    ZhouyiDivinationSelection(
                        ruleSummary = if (primary.number == 1) {
                            "六爻皆变，乾卦取用九。"
                        } else {
                            "六爻皆变，坤卦取用六。"
                        },
                        references = listOf(
                            reference(
                                hexagram = primary,
                                textType = if (primary.number == 1) "用九" else "用六",
                                text = useText,
                                commentary = primary.useCommentary.orEmpty(),
                                isPrimary = true,
                            ),
                        ),
                    )
                } else {
                    ZhouyiDivinationSelection(
                        ruleSummary = "六爻皆变且非乾坤，取变卦卦辞。",
                        references = listOf(judgment(transformed, isPrimary = true)),
                    )
                }
            }

            else -> error("A hexagram cannot have more than six moving lines")
        }
    }

    fun context(hexagram: ZhouyiHexagramText): ClassicHexagramContext =
        ClassicHexagramContext(
            number = hexagram.number,
            name = hexagram.name,
            glyph = hexagram.glyph,
            judgment = hexagram.judgment,
            tuan = hexagram.tuan,
            image = hexagram.image,
        )

    private fun judgment(
        hexagram: ZhouyiHexagramText,
        isPrimary: Boolean,
    ): ClassicReference = reference(
        hexagram = hexagram,
        textType = "卦辞",
        text = hexagram.judgment,
        isPrimary = isPrimary,
    )

    private fun line(
        hexagram: ZhouyiHexagramText,
        position: Int,
        isPrimary: Boolean,
    ): ClassicReference = reference(
        hexagram = hexagram,
        textType = "爻辞",
        linePosition = position,
        text = hexagram.lineText(position),
        commentary = hexagram.lineCommentary(position),
        isPrimary = isPrimary,
    )

    private fun reference(
        hexagram: ZhouyiHexagramText,
        textType: String,
        linePosition: Int = 0,
        text: String,
        commentary: String = "",
        isPrimary: Boolean,
    ): ClassicReference = ClassicReference(
        hexagramNumber = hexagram.number,
        hexagramName = hexagram.name,
        hexagramGlyph = hexagram.glyph,
        textType = textType,
        linePosition = linePosition,
        text = text,
        commentary = commentary,
        isPrimary = isPrimary,
    )
}
