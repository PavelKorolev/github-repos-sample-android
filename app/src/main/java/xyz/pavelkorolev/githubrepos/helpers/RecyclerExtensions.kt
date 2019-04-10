package xyz.pavelkorolev.githubrepos.helpers

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import xyz.pavelkorolev.githubrepos.R

class CustomItemAnimator : DefaultItemAnimator()

fun RecyclerView.setAdapterFromController(controller: EpoxyController) {
    layoutManager = LinearLayoutManager(context)
    itemAnimator = CustomItemAnimator()
    adapter = controller.adapter
    controller.addModelBuildListener {
        invalidateItemDecorations()
    }
}

fun RecyclerView.setHorizontalAdapterFromController(controller: EpoxyController) {
    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    itemAnimator = CustomItemAnimator()
    adapter = controller.adapter
    controller.addModelBuildListener {
        invalidateItemDecorations()
    }
}

fun RecyclerView.addDefaultSeparators() {
    val horizontalDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    val drawable = ContextCompat.getDrawable(context, R.drawable.lists_divider) ?: return
    horizontalDecoration.setDrawable(drawable)
    addItemDecoration(horizontalDecoration)
}
