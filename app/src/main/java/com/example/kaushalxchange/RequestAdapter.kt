package com.example.kaushalxchange

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RequestAdapter(
    private val data: List<Pair<String, MatchRequest>>,
    private val listener: OnDecisionClickListener
) : RecyclerView.Adapter<RequestAdapter.VH>() {

    interface OnDecisionClickListener {
        fun onDecision(requestId: String, request: MatchRequest, accepted: Boolean)
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val requester: TextView = view.findViewById(R.id.req_from)
        val wantToLearn: TextView = view.findViewById(R.id.req_skill)
        val offers: TextView = view.findViewById(R.id.req_offers)
        val accept: Button = view.findViewById(R.id.btn_accept)
        val reject: Button = view.findViewById(R.id.btn_reject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val (id, req) = data[position]

        // Show first name
        val firstName = req.fromName.split("\\s+".toRegex()).firstOrNull() ?: req.fromName
        holder.requester.text = firstName

        // Nicely join lists for display
        holder.wantToLearn.text = "Skill wants to learn: " + (if (req.skills_want_to_learn.isEmpty()) "—" else req.skills_want_to_learn.joinToString(", "))

        // FIX: show what requester offers (skills_can_teach), not skills_want_to_learn again
        holder.offers.text = "Skills wish to teach: " + (if (req.skills_can_teach.isEmpty()) "—" else req.skills_can_teach.joinToString(", "))

        val decided = req.status == "accepted" || req.status == "rejected"
        holder.accept.isEnabled = !decided
        holder.reject.isEnabled = !decided
        holder.accept.alpha = if (decided) 0.5f else 1f
        holder.reject.alpha = if (decided) 0.5f else 1f

        holder.accept.setOnClickListener { listener.onDecision(id, req, true) }
        holder.reject.setOnClickListener { listener.onDecision(id, req, false) }
    }

    override fun getItemCount(): Int = data.size
}
