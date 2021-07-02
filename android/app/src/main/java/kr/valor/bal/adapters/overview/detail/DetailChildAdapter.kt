package kr.valor.bal.adapters.overview.detail

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.valor.bal.adapters.RecyclerviewItemClickListener
import kr.valor.bal.adapters.ViewHolder
import kr.valor.bal.adapters.ViewHolderFactory
import kr.valor.bal.data.entities.WorkoutSet
import kr.valor.bal.databinding.SetInfoItemGridBinding

class WorkoutDetailChildAdapter: ListAdapter<WorkoutSet, ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ChildViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as ChildViewHolder).bind(item, position)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WorkoutSet>() {
            override fun areItemsTheSame(oldItem: WorkoutSet, newItem: WorkoutSet): Boolean {
                return oldItem.setId == newItem.setId
            }

            override fun areContentsTheSame(oldItem: WorkoutSet, newItem: WorkoutSet): Boolean {
                return oldItem == newItem
            }
        }
    }

}

class ChildViewHolder private constructor (private val binding: SetInfoItemGridBinding): ViewHolder(binding) {
    fun bind(workoutSet: WorkoutSet, position: Int) {
        binding.item = workoutSet
        binding.index = position
        binding.executePendingBindings()
    }

    override fun <T> bind(item: T, vararg listeners: RecyclerviewItemClickListener<*>, itemPosition: Int?) {
        TODO("Not yet implemented")
    }

    companion object: ViewHolderFactory {
        override fun create(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = SetInfoItemGridBinding.inflate(layoutInflater, parent, false)
            return ChildViewHolder(binding)
        }
    }
}

class ItemOffsetDecoration(context : Context, itemOffsetId : Int) : RecyclerView.ItemDecoration() {
    private val itemOffset = context.resources.getDimensionPixelSize(itemOffsetId)
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(itemOffset, 0, itemOffset, itemOffset)
    }
}