package com.stenisway.wifi_bluetooth_discovery.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.stenisway.wifi_bluetooth_discovery.MainActivity
import com.stenisway.wifi_bluetooth_discovery.R
import com.stenisway.wifi_bluetooth_discovery.adapter.BluetoothAdapter
import com.stenisway.wifi_bluetooth_discovery.adapter.WifiAdapter
import com.stenisway.wifi_bluetooth_discovery.base.BaseTimeFragment
import com.stenisway.wifi_bluetooth_discovery.databinding.FragmentHomeBinding
import com.stenisway.wifi_bluetooth_discovery.databinding.FragmentWifiBinding
import kotlinx.coroutines.launch

class WifiFragment : BaseTimeFragment() {

    companion object {
        fun newInstance() = WifiFragment()
    }

    private val viewModel: WifiViewModel by viewModels()

    private lateinit var binding: FragmentWifiBinding

    private lateinit var acticity : MainActivity

    private lateinit var adapter : WifiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        acticity = requireActivity() as MainActivity
        binding = FragmentWifiBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        binding.timeWifiLayout.tvTitle.text = "WIFI"
        lifecycleScope.launch {
            viewModel.wifiList.collect{ list ->
                if (list == null){
                    binding.rvWifi.visibility = View.GONE
                    binding.pbWifi.visibility = View.VISIBLE
                }else{
                    binding.rvWifi.visibility = View.VISIBLE
                    binding.pbWifi.visibility = View.GONE
                    adapter.replaceWifiList(list)
                }
            }
        }
    }

    override fun onStart() {
        binding.timeWifiLayout.tvTime.updatingNowTime()
        acticity.startWifiScan()
        super.onStart()
    }


    override fun onStop() {
        stopTimeUpdating()
        acticity.stopWifiScan()
        super.onStop()
    }

    fun initAdapter(){
        adapter = WifiAdapter()
        binding.rvWifi.adapter = adapter
        binding.rvWifi.layoutManager = LinearLayoutManager(requireContext())
    }


}