package com.example.kaushalxchange

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kaushalxchange.databinding.RvItemBinding

class Rvadapter(var dataList: ArrayList<Rvmodel>,var context: Context):RecyclerView.Adapter<Rvadapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding=RvItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return MyViewHolder(binding)


    }

    override fun getItemCount(): Int {
         return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.profile.setImageResource(dataList.get(position).profile)
        holder.binding.name.text=dataList.get(position).name

    }

    inner class MyViewHolder(var binding:RvItemBinding): RecyclerView.ViewHolder(binding.root)

}