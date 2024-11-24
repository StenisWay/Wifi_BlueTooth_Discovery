package com.stenisway.wifi_bluetooth_discovery.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Visibility
import com.stenisway.wifi_bluetooth_discovery.MainActivity
import com.stenisway.wifi_bluetooth_discovery.R
import com.stenisway.wifi_bluetooth_discovery.adapter.BluetoothAdapter
import com.stenisway.wifi_bluetooth_discovery.base.BaseTimeFragment
import com.stenisway.wifi_bluetooth_discovery.databinding.FragmentBlueToothBinding
import kotlinx.coroutines.launch

class BlueToothFragment : BaseTimeFragment() {

    companion object {
        fun newInstance() = BlueToothFragment()
    }

    private lateinit var binding : FragmentBlueToothBinding
    private val viewModel: BlueToothViewModel by viewModels()
    private lateinit var adapter : BluetoothAdapter
    private lateinit var activity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlueToothBinding.inflate(LayoutInflater.from(requireContext()))
        activity = (requireActivity() as MainActivity)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        binding.timeBluetoothLayout.tvTitle.text = "Bluetooth"
        lifecycleScope.launch {
            viewModel.btList_flow.collect{ list ->
                if (list == null){
                    binding.rvBluetooth.visibility = View.GONE
                    binding.pbBluetooth.visibility = View.VISIBLE
                }else{
                    binding.rvBluetooth.visibility = View.VISIBLE
                    binding.pbBluetooth.visibility = View.GONE
                    adapter.replaceBTList(list)
                }
            }
        }
    }

    override fun onStart() {
        binding.timeBluetoothLayout.tvTime.updatingNowTime()
        activity.startBluetoothScan()
        super.onStart()
    }


    override fun onStop() {
        stopTimeUpdating()
        activity.stopBluetoothScan()
        super.onStop()
    }

    fun initAdapter(){
        adapter = BluetoothAdapter()
        binding.rvBluetooth.adapter = adapter
        binding.rvBluetooth.layoutManager = LinearLayoutManager(requireContext())
    }

}