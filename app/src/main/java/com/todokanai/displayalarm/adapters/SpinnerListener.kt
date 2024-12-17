package com.todokanai.displayalarm.adapters

import android.view.View
import android.widget.AdapterView

class SpinnerListener(
    val onSelected:(position:Int)->Unit
):AdapterView.OnItemSelectedListener {

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        p0?.let{
            onSelected(it.selectedItemPosition)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}