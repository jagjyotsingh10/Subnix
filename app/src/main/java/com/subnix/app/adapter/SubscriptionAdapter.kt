package com.subnix.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.subnix.app.R
import com.subnix.app.data.Subscription
import com.subnix.app.util.DateUtils
import com.subnix.app.util.SubscriptionStatus
import java.util.Locale

class SubscriptionAdapter(
    private val onClick: (Subscription) -> Unit,
    private val onLongClick: (Subscription) -> Unit
) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    private val items = mutableListOf<Subscription>()

    fun submitList(list: List<Subscription>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subscription, parent, false)
        return SubscriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.itemView.setOnClickListener { onClick(item) }
        holder.itemView.setOnLongClickListener {
            onLongClick(item)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    class SubscriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAppName: TextView = itemView.findViewById(R.id.tvAppName)
        private val tvLastUsed: TextView = itemView.findViewById(R.id.tvLastUsed)
        private val tvCost: TextView = itemView.findViewById(R.id.tvCost)
        private val tvBadge: TextView = itemView.findViewById(R.id.tvBadge)
        private val tvActiveState: TextView = itemView.findViewById(R.id.tvActiveState)

        fun bind(subscription: Subscription) {
            tvAppName.text = subscription.appName
            tvLastUsed.text = DateUtils.formatLastUsed(subscription.lastUsed)
            tvCost.text = String.format(Locale.CANADA, "CAD $%.2f/month", subscription.monthlyCost)
            tvActiveState.text = if (subscription.isActive) "Enabled" else "Paused"

            val status = DateUtils.computeStatus(subscription.lastUsed)
            when (status) {
                SubscriptionStatus.ACTIVE -> {
                    tvBadge.text = itemView.context.getString(R.string.status_active)
                    tvBadge.setBackgroundResource(R.drawable.bg_badge_active)
                }

                SubscriptionStatus.IDLE -> {
                    tvBadge.text = itemView.context.getString(R.string.status_idle)
                    tvBadge.setBackgroundResource(R.drawable.bg_badge_idle)
                }

                SubscriptionStatus.FORGOTTEN -> {
                    tvBadge.text = itemView.context.getString(R.string.status_forgotten)
                    tvBadge.setBackgroundResource(R.drawable.bg_badge_forgotten)
                }
            }
        }
    }
}
