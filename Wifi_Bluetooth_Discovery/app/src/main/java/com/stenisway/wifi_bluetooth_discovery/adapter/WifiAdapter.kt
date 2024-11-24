package com.stenisway.wifi_bluetooth_discovery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.stenisway.wifi_bluetooth_discovery.databinding.CellDeviceBinding
import com.stenisway.wifi_bluetooth_discovery.model.BTItem
import com.stenisway.wifi_bluetooth_discovery.model.WifiItem

class WifiAdapter : RecyclerView.Adapter<WifiAdapter.WifiViewHolder>(){
    private var wifiList_show = listOf<WifiItem>()

    fun replaceWifiList(list: List<WifiItem>){
        wifiList_show = list
        notifyDataSetChanged()
    }

    inner class WifiViewHolder(val binding: CellDeviceBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
        val binding = CellDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WifiViewHolder(binding = binding)
    }

    override fun getItemCount(): Int {
        return wifiList_show.size
    }


    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        holder.binding.txtBtcName.text = wifiList_show[position].wifiName
        holder.binding.txtAddress.text = wifiList_show[position].wifiMac
        holder.binding.txtCrssi.text = wifiList_show[position].wifi_rssi.toString()
    }
}