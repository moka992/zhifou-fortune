package com.zhifou.fortune

internal fun mainBackDestination(tab: Tab): Tab? = when (tab) {
    Tab.Home -> null
    Tab.Schedule, Tab.Oracle, Tab.Tools, Tab.Mine -> Tab.Home
}
