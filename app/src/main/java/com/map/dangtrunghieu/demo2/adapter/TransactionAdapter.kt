package com.map.dangtrunghieu.demo2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.map.dangtrunghieu.demo2.R
import com.map.dangtrunghieu.demo2.databinding.TransactionItemBinding
import com.map.dangtrunghieu.demo2.model.Transaction

class TransactionAdapter :
    ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback) {

    companion object TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }


    class TransactionViewHolder(private val binding: TransactionItemBinding) :
        ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.tvTitle.text = transaction.name
            binding.tvAmountValue.text = transaction.amount.toString()
            binding.ivIcon.setImageResource(
                R.drawable.ic_work
            )
            if(transaction.catInOut.inOut.type == 0){
                binding.tvAmountValue.setTextColor(binding.root.context.resources.getColor(R.color.green))
            }else{
                binding.tvAmountValue.setTextColor(binding.root.context.resources.getColor(R.color.red))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding =
            TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}