package xyz.pavelkorolev.githubrepos.components

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class FitsSystemWindowsFrameLayout : FrameLayout {

    private val windowInsets = Rect()
    private val tempInsets = Rect()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun fitSystemWindows(insets: Rect): Boolean {
        windowInsets.set(insets)
        super.fitSystemWindows(insets)
        return false
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        tempInsets.set(windowInsets)
        super.fitSystemWindows(tempInsets)
    }
}
