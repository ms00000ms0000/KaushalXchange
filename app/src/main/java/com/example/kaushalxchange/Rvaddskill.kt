package com.example.kaushalxchange

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kaushalxchange.databinding.ActivityRvaddskillBinding

class Rvaddskill : AppCompatActivity(){
    private lateinit var binding:ActivityRvaddskillBinding
    private lateinit var rvadapter: Rvadapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityRvaddskillBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvadapter=Rvadapter(Constant.getData(),this)
        binding.Rv.layoutManager=LinearLayoutManager(this)
        binding.Rv.adapter=rvadapter

    }
}