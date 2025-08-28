package com.example.kaushalxchange

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import com.example.kaushalxchange.databinding.ListItemBinding
import java.util.*

class ListAdapter(
    private val context: Context,
    private var dataList: ArrayList<ListData?>
) : BaseAdapter(), Filterable {

    private var filteredList: ArrayList<ListData?> = ArrayList(dataList)
    private val fullList: ArrayList<ListData?> = ArrayList(dataList)

    private val prefs: SharedPreferences =
        context.getSharedPreferences("LikedSkills", Context.MODE_PRIVATE)

    override fun getCount(): Int = filteredList.size
    override fun getItem(position: Int): ListData? = filteredList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ListItemBinding
        val view: View

        if (convertView == null) {
            binding = ListItemBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ListItemBinding
            view = convertView
        }

        val item = getItem(position)
        binding.listName.text = item?.name
        if (item != null) {
            binding.listImage.setImageResource(item.image)

            val isLiked = prefs.getBoolean(item.name, false)
            binding.heartIcon.setImageResource(
                if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            )

            binding.heartIcon.setOnClickListener {
                val newLiked = !prefs.getBoolean(item.name, false)
                prefs.edit().putBoolean(item.name, newLiked).apply()
                notifyDataSetChanged()
            }
        }
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase(Locale.ROOT)?.trim()
                val results = FilterResults()

                results.values = if (query.isNullOrEmpty()) {
                    ArrayList(fullList)
                } else {
                    fullList.filter {
                        it?.name?.lowercase(Locale.ROOT)?.contains(query) == true
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as? ArrayList<ListData?>)
                    ?: ArrayList()
                notifyDataSetChanged()
            }
        }
    }
}
