package com.exner.tools.fototimerresearch2.ui.models

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exner.tools.fototimerresearch2.R
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess

class FotoTimerProcessListAdapter :
    ListAdapter<FotoTimerProcess, FotoTimerProcessListAdapter.ProcessViewHolder>(
        ProcessComparator()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessViewHolder {
        return ProcessViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ProcessViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ProcessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val processIdView: TextView = itemView.findViewById(R.id.processIdView)
        private val processNameView: TextView = itemView.findViewById(R.id.processNameView)
        private val processTimeView: TextView = itemView.findViewById(R.id.processTimeView)

        fun bind(fotoTimerProcess: FotoTimerProcess) {
            processIdView.text = fotoTimerProcess.uid.toString()
            processNameView.text = fotoTimerProcess.name
            val timeString =
                fotoTimerProcess.processTime.toString() + "/" + fotoTimerProcess.intervalTime.toString()
            processTimeView.text = timeString
        }

        init {
            itemView.setOnClickListener { view ->
                Log.i("jexner", "Click detected on $view, finding uid...")
                val idView = view.findViewById<TextView>(R.id.processIdView)
                val uid = idView.text.toString().toLongOrNull()
                Log.i("jexner", "Looks like the process tapped was $uid...")
                // now go to ProcessOverviewActivity
//                val intent = Intent(context, ProcessOverviewActivity::class.java)
//                intent.putExtra("PROCESS_ID", uid)
//                startActivity(context, intent, null)
            }
        }

        companion object {
            fun create(parent: ViewGroup): ProcessViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return ProcessViewHolder(view)
            }
        }
    }

    class ProcessComparator :
        DiffUtil.ItemCallback<FotoTimerProcess>() {
        override fun areItemsTheSame(
            oldItem: FotoTimerProcess,
            newItem: FotoTimerProcess
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: FotoTimerProcess,
            newItem: FotoTimerProcess
        ): Boolean {
            return oldItem.uid == newItem.uid
        }
    }
}