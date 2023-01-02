package com.exner.tools.fototimerresearch2.ui.models

import android.app.Activity
import android.content.Context
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

class FotoTimerProcessArrayAdapter(
    private val context: Activity,
    private val resource: Int
) : ArrayAdapter<FotoTimerProcess?>(
    context, resource
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: TextView = if (convertView == null) {
            TextView(getContext())
        } else {
            convertView as TextView
        }
        val item = getItem(position)
        rowView.text = item?.name ?: "No name"

        return rowView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: TextView = if (convertView == null) {
            TextView(getContext())
        } else {
            convertView as TextView
        }
        val item = getItem(position)
        rowView.text = item?.name ?: "No name"

        return rowView
    }

}
