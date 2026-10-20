package com.zhifou.fortune

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/** Optional local layout fixture, excluded from normal builds; never records or transmits text. */
class LayoutTestIme : InputMethodService() {
    override fun onEvaluateFullscreenMode() = false
    override fun onEvaluateInputViewShown() = true

    override fun onCreateInputView(): View {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            minimumHeight = (280 * resources.displayMetrics.density).toInt()
            setBackgroundColor(0xffe1e9ed.toInt())
            contentDescription = "布局测试键盘"
            addView(TextView(this@LayoutTestIme).apply { text = "Layout test keyboard"; textSize = 20f })
            addView(Button(this@LayoutTestIme).apply {
                text = "插入测试长文本"
                setOnClickListener {
                    currentInputConnection?.commitText("1234567890 ".repeat(18) + "987654321000", 1)
                }
            })
            addView(Button(this@LayoutTestIme).apply {
                text = "清空测试长文本"
                setOnClickListener {
                    currentInputConnection?.deleteSurroundingText(1000, 1000)
                }
            })
        }
    }
}
