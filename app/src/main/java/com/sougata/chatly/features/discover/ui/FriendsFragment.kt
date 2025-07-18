package com.sougata.chatly.features.discover.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.User
import com.sougata.chatly.databinding.FragmentFriendsBinding
import com.sougata.chatly.features.discover.view_models.FriendsVM
import com.sougata.chatly.util.DecoratedViews

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: FriendsVM

    private lateinit var recyclerViewAdapter: FriendsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.vm = ViewModelProvider(this)[FriendsVM::class.java]

        this.setupRecyclerView()
        this.registerObservers()
    }

    override fun onDestroy() {
        super.onDestroy()

        this._binding = null
    }

    private fun setupRecyclerView() {
        this.recyclerViewAdapter = FriendsAdapter(mutableListOf())
        this.binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = recyclerViewAdapter
        }

        this.binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!vm.noMoreFriends) {
                    val itemCount = layoutManager.itemCount
                    val lastItemPosition =
                        layoutManager.findLastCompletelyVisibleItemPosition()

                    if (lastItemPosition == itemCount - 5) {
                        recyclerViewAdapter.showItemLoader(User())
                    }

                    if (lastItemPosition == itemCount - 1) {
                        vm.loadFriends()
                    }
                }
            }
        })
    }

    private fun registerObservers() {
        this.vm.friendsList.observe(this.viewLifecycleOwner) {
            if (it.taskStatus == TaskStatus.STARTED) {

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

                this.recyclerViewAdapter.setItems(it.result!!)

            } else if (it.taskStatus == TaskStatus.FAILED) {
                DecoratedViews.showSnackBar(
                    requireView(),
                    null,
                    it.message,
                    Snackbar.LENGTH_LONG
                )
            }
        }
    }

}