package com.example.kaushalxchange

object Constant {
    private lateinit var dataList:ArrayList<Rvmodel>
    fun getData():ArrayList<Rvmodel>{

        dataList=ArrayList<Rvmodel>()

        dataList.add(Rvmodel(R.drawable.python,"Python"))
        dataList.add(Rvmodel(R.drawable.java,"Java"))
        dataList.add(Rvmodel(R.drawable.c,"C"))
        dataList.add(Rvmodel(R.drawable.html,"HTML"))
        dataList.add(Rvmodel(R.drawable.css,"CSS"))
        dataList.add(Rvmodel(R.drawable.javascript,"Javascript"))
        dataList.add(Rvmodel(R.drawable.word,"Word"))
        dataList.add(Rvmodel(R.drawable.powerpoint,"Powerpoint"))
        dataList.add(Rvmodel(R.drawable.excel,"Excel"))
        dataList.add(Rvmodel(R.drawable.canva,"Canva"))
        dataList.add(Rvmodel(R.drawable.python,"Python"))
        dataList.add(Rvmodel(R.drawable.python,"Python"))

        return dataList

    }
}