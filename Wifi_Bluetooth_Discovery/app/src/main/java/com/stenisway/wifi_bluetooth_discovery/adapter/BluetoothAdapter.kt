package com.stenisway.wifi_bluetooth_discovery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.stenisway.wifi_bluetooth_discovery.databinding.CellDeviceBinding
import com.stenisway.wifi_bluetooth_discovery.model.BTItem

class BluetoothAdapter : RecyclerView.Adapter<BluetoothAdapter.BlueToothViewHolder>() {

    private var btList_show = listOf<BTItem>()

    fun replaceBTList(list: List<BTItem>){
        btList_show = list
        notifyDataSetChanged()
    }

    inner class BlueToothViewHolder(val binding: CellDeviceBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlueToothViewHolder {
        val binding = CellDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlueToothViewHolder(binding = binding)
    }

    override fun getItemCount(): Int {
        return btList_show.size
    }


    override fun onBindViewHolder(holder: BlueToothViewHolder, position: Int) {
        holder.binding.txtBtcName.text = btList_show[position].btName
        holder.binding.txtAddress.text = btList_show[position].btAddress
        holder.binding.txtCrssi.text = btList_show[position].bt_rssi.toString()
    }
}