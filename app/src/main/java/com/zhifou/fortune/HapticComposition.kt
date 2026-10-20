package com.zhifou.fortune

internal enum class HapticPrimitive { Click, Tick, LowTick, QuickRise, SlowRise, QuickFall, Spin, Thud }
internal data class HapticAtom(val primitive: HapticPrimitive, val scale: Float, val delayMs: Int = 0)

/** Select a complete supported score: one unsupported primitive must never silence the whole effect. */
internal object HapticComposition {
    fun score(cue: HapticCue, supported: Set<HapticPrimitive>, durations: Map<HapticPrimitive, Int> = emptyMap()): List<HapticAtom>? {
        fun a(p: HapticPrimitive, s: Float, delay: Int = 0) = HapticAtom(p, s, delay)
        val click = HapticPrimitive.Click
        val tick = HapticPrimitive.Tick
        val low = HapticPrimitive.LowTick
        val rise = HapticPrimitive.QuickRise
        val fall = HapticPrimitive.QuickFall
        val slow = HapticPrimitive.SlowRise
        val thud = HapticPrimitive.Thud
        val rich = when (cue) {
            HapticCue.CardDraw, HapticCue.MenuOpen -> listOf(a(slow, .32f), a(fall, .18f))
            HapticCue.CardTurn -> listOf(a(rise, .30f), a(click, .42f), a(fall, .18f))
            HapticCue.CoinLift -> listOf(a(HapticPrimitive.Spin, .38f))
            HapticCue.DiceImpact -> listOf(a(thud, .60f))
            HapticCue.GestureRelease, HapticCue.ToggleOn, HapticCue.VoiceStart -> listOf(a(rise, .30f), a(click, .48f))
            HapticCue.ToggleOff, HapticCue.NavigateOut, HapticCue.GestureCancel -> listOf(a(fall, .26f))
            HapticCue.CoinFinish -> listOf(a(click, .60f), a(low, .26f, 52))
            HapticCue.DiceFinish -> listOf(a(thud, .65f), a(low, .28f, 55))
            HapticCue.WheelFinish -> listOf(a(tick, .60f), a(fall, .28f))
            else -> emptyList()
        }
        val soft = if (low in supported) low else if (tick in supported) tick else click
        val main = if (click in supported) click else if (tick in supported) tick else low
        val bright = if (tick in supported) tick else main
        val compact = when (cue) {
            HapticCue.Confirm, HapticCue.Send -> listOf(a(main, .52f), a(soft, .24f, 52))
            HapticCue.Reject -> listOf(a(main, .46f), a(main, .46f, 65))
            HapticCue.VoiceCancel -> listOf(a(soft, .38f), a(soft, .22f, 55))
            HapticCue.CoinFinish, HapticCue.DiceFinish -> listOf(a(main, .68f), a(soft, .30f, 55))
            HapticCue.CardDraw -> listOf(a(soft, .20f), a(soft, .38f, 30), a(soft, .18f, 22))
            HapticCue.CardTurn, HapticCue.WheelFinish -> listOf(a(main, .48f), a(soft, .22f, 38))
            HapticCue.CoinLift, HapticCue.VoiceStart -> listOf(a(soft, .24f), a(main, .46f, 30))
            HapticCue.GestureArm, HapticCue.ToggleOn, HapticCue.GestureRelease -> listOf(a(main, .56f))
            HapticCue.DiceImpact, HapticCue.CoinImpact -> listOf(a(main, .62f))
            HapticCue.Selection, HapticCue.PageSnap, HapticCue.VoiceStop -> listOf(a(bright, .44f))
            HapticCue.NavigateIn, HapticCue.MenuOpen -> listOf(a(soft, .24f), a(soft, .34f, 28))
            HapticCue.ToggleOff, HapticCue.NavigateOut, HapticCue.MenuClose -> listOf(a(soft, .30f))
            HapticCue.FineSelection, HapticCue.Shuffle, HapticCue.GestureDisarm, HapticCue.GestureCancel -> listOf(a(soft, .18f))
            HapticCue.GesturePull -> listOf(a(soft, .40f))
            HapticCue.CoinSpin -> listOf(a(soft, .30f), a(soft, .14f, 22))
            HapticCue.CardLand -> listOf(a(soft, .24f))
            HapticCue.WheelTick -> listOf(a(bright, .32f))
            HapticCue.CardBrowse -> listOf(a(bright, .26f))
        }
        return listOf(rich, compact).firstOrNull {
            it.isNotEmpty() && it.all { atom -> atom.primitive in supported } &&
                it.sumOf { atom -> (durations[atom.primitive] ?: 20) + atom.delayMs } <= 220
        }
    }
}
