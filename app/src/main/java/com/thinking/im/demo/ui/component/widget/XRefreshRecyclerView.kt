package com.thinking.im.demo.ui.component.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ViewXrefreshRecyclerBinding

class XRefreshRecyclerView : FrameLayout {

    private val binding: ViewXrefreshRecyclerBinding
    private var needShowNoMoreText = true

    private val observer = object : AdapterDataObserver() {

        override fun onChanged() {
            super.onChanged()
            change()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            change()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            change()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            change()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            change()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            change()
        }

        private fun change() {
            val adapter = getAdapter()
            adapter?.let {
                showEmptyView(it.itemCount > 0)
            }
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.view_xrefresh_recycler, this, true)
        binding = ViewXrefreshRecyclerBinding.bind(view)

        binding.lyRefresh.setRefreshHeader(ClassicsHeader(context))
        binding.lyRefresh.setRefreshFooter(ClassicsFooter(context))

        binding.lyRefresh.setEnableRefresh(false)
        binding.lyRefresh.setEnableLoadMore(false)

    }

    fun setLayoutManager(layoutManager: LayoutManager) {
        binding.recyclerView.layoutManager = layoutManager
    }

    fun showEmptyView(show: Boolean) {
        if (!show) {
            binding.lyEmpty.visibility = View.VISIBLE
            binding.lyRefresh.setEnableFooterFollowWhenNoMoreData(false)
        } else {
            binding.lyRefresh.setEnableFooterFollowWhenNoMoreData(needShowNoMoreText)
            binding.lyEmpty.visibility = View.GONE
        }
    }

    fun setAdapter(
        adapter: Adapter<out RecyclerView.ViewHolder>,
        emptyShow: Boolean = false,
    ) {
        binding.recyclerView.adapter = adapter
        binding.lyEmpty.visibility = if (emptyShow) View.VISIBLE else View.INVISIBLE
    }

    fun getAdapter(): Adapter<RecyclerView.ViewHolder>? {
        return binding.recyclerView.adapter
    }

    fun recyclerView(): RecyclerView {
        return binding.recyclerView
    }

    fun emptyView(): View? {
        if (binding.lyEmpty.children.count() == 0) {
            return null
        }
        return binding.lyEmpty.children.first()
    }

    fun setOnRefreshListener(listener: OnRefreshListener) {
        binding.lyRefresh.setOnRefreshListener(listener)
        binding.lyRefresh.setEnableRefresh(true)
    }

    fun setOnLoadMoreListener(listener: OnLoadMoreListener) {
        binding.lyRefresh.setOnLoadMoreListener(listener)
        binding.lyRefresh.setEnableLoadMore(true)
    }

    fun finishRefresh() {
        binding.lyRefresh.finishRefresh()
    }

    fun finishRefresh(success: Boolean, noMoreData: Boolean = false) {
        binding.lyRefresh.finishRefresh(0, success, noMoreData)
    }

    fun finishLoadMore(success: Boolean) {
        binding.lyRefresh.finishLoadMore(success)
    }

    fun finishLoadMore(success: Boolean, noMoreData: Boolean) {
        binding.lyRefresh.finishLoadMore(0, success, noMoreData)
    }

    fun finishWithNoMoreData() {
        binding.lyRefresh.finishRefreshWithNoMoreData()
    }

    fun setNeedShowNoMoreDataText(need: Boolean) {
        needShowNoMoreText = need
    }
}