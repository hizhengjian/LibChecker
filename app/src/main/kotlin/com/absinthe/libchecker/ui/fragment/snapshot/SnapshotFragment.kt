package com.absinthe.libchecker.ui.fragment.snapshot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.absinthe.libchecker.databinding.FragmentSnapshotBinding
import com.absinthe.libchecker.databinding.LayoutSnapshotDashboardBinding
import com.absinthe.libchecker.recyclerview.SnapshotAdapter
import com.absinthe.libchecker.ui.main.MainActivity
import com.absinthe.libchecker.viewmodel.SnapshotViewModel
import com.blankj.utilcode.util.ConvertUtils
import rikka.material.widget.BorderView
import java.text.SimpleDateFormat
import java.util.*

class SnapshotFragment : Fragment() {

    private val viewModel by viewModels<SnapshotViewModel>()
    private lateinit var binding: FragmentSnapshotBinding
    private lateinit var dashboardBinding: LayoutSnapshotDashboardBinding
    private val adapter = SnapshotAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSnapshotBinding.inflate(inflater, container, false)
        dashboardBinding = LayoutSnapshotDashboardBinding.inflate(inflater, binding.root, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.apply {
            extendedFab.apply {
                (layoutParams as CoordinatorLayout.LayoutParams)
                    .setMargins(
                        0,
                        0,
                        ConvertUtils.dp2px(16f),
                        ConvertUtils.dp2px(16f) + paddingBottom
                    )
                setOnClickListener {
                    viewModel.computeSnapshots()
                }
            }
            rvList.apply {
                adapter = this@SnapshotFragment.adapter
                borderVisibilityChangedListener =
                    BorderView.OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean ->
                        (requireActivity() as MainActivity).appBar?.setRaised(!top)
                    }
            }
        }

        adapter.apply {
            addHeaderView(dashboardBinding.root)
        }

        viewModel.timestamp.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            dashboardBinding.tvSnapshotTimestampText.text = if (it != 0L) {
                getFormatDateString(it)
            } else {
                "None"
            }
        })
        viewModel.snapshotItems.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.setNewInstance(it.toMutableList())
        })
    }

    private fun getFormatDateString(timestamp: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault())
        val date = Date(timestamp)
        return simpleDateFormat.format(date)
    }
}