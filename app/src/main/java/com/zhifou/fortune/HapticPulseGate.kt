package com.zhifou.fortune

/** Limits repeated animation events, including multiple events in one frame. */
internal class HapticPulseGate {
    private var lastPulseAt: Long? = null

    fun tryAcquire(nowMs: Long, minimumIntervalMs: Long): Boolean {
        val previous = lastPulseAt
        if (previous != null && nowMs >= previous && nowMs - previous < minimumIntervalMs) {
            return false
        }
        lastPulseAt = nowMs
        return true
    }

    fun reset() {
        lastPulseAt = null
    }
}
