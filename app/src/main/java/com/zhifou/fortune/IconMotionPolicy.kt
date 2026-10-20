package com.zhifou.fortune

internal enum class SemanticIconKind {
    Home, Calendar, Sparkles, Tools, Profile, Book, Coins, Tarot, Dice, Wallet, Work,
    Heart, Settings, Clock, Search, Pencil, Mic, Haptics, Picture, Shield, Check, Add,
    Arrow, Refresh, Send, None,
}

internal const val SEMANTIC_ICON_DURATION_MS = 680

internal fun semanticIconKind(name: String): SemanticIconKind = when (name.substringAfterLast('.')) {
    "Home" -> SemanticIconKind.Home
    "CalendarMonth", "DateRange", "Today" -> SemanticIconKind.Calendar
    "AutoAwesome" -> SemanticIconKind.Sparkles
    "GridView" -> SemanticIconKind.Tools
    "PersonOutline", "Person", "AccountCircle" -> SemanticIconKind.Profile
    "Book", "MenuBook", "AutoStories" -> SemanticIconKind.Book
    "Toll", "Paid", "Payments" -> SemanticIconKind.Coins
    "Style" -> SemanticIconKind.Tarot
    "Casino" -> SemanticIconKind.Dice
    "AccountBalanceWallet" -> SemanticIconKind.Wallet
    "WorkOutline", "Work" -> SemanticIconKind.Work
    "FavoriteBorder", "Favorite" -> SemanticIconKind.Heart
    "Settings" -> SemanticIconKind.Settings
    "Schedule", "AccessTime" -> SemanticIconKind.Clock
    "Search" -> SemanticIconKind.Search
    "Edit" -> SemanticIconKind.Pencil
    "Mic" -> SemanticIconKind.Mic
    "Vibration" -> SemanticIconKind.Haptics
    "Wallpaper", "Image", "AddPhotoAlternate" -> SemanticIconKind.Picture
    "Shield" -> SemanticIconKind.Shield
    "Check", "CheckCircle" -> SemanticIconKind.Check
    "Add" -> SemanticIconKind.Add
    "ArrowBack", "KeyboardArrowLeft", "KeyboardArrowRight", "ChevronRight" -> SemanticIconKind.Arrow
    "Refresh" -> SemanticIconKind.Refresh
    "Send" -> SemanticIconKind.Send
    else -> SemanticIconKind.None
}

internal fun iconStage(progress: Float, start: Float, end: Float): Float {
    val safe = if (progress.isFinite()) progress else 1f
    val t = ((safe - start) / (end - start)).coerceIn(0f, 1f)
    return (t * t * (3f - 2f * t)).coerceIn(0f, 1f)
}

internal fun shouldAnimateSemanticIcon(kind: SemanticIconKind, active: Boolean, reduced: Boolean, visible: Boolean): Boolean =
    kind != SemanticIconKind.None && active && !reduced && visible
