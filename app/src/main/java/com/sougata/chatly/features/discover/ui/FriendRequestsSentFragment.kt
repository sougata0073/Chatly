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
import com.sougata.chatly.data.models.FriendRequestSent
import com.sougata.chatly.databinding.FragmentFriendRequestsSentBinding
import com.sougata.chatly.features.discover.view_models.FriendRequestsSentVM
import com.sougata.chatly.util.DecoratedViews

class FriendRequestsSentFragment : Fragment() {

    private var _binding: FragmentFriendRequestsSentBinding? = null
    private val binding get() = this._binding!!

    private lateinit var vm: FriendRequestsSentVM

    private lateinit var recyclerViewAdapter: FriendRequestSentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentFriendRequestsSentBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.vm = ViewModelProvider(this)[FriendRequestsSentVM::class.java]

        this.setupRecyclerView()
        this.registerObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        this._binding = null
    }


    private fun setupRecyclerView() {
        this.recyclerViewAdapter =
            FriendRequestSentAdapter(mutableListOf()) {
                this.vm.deleteFriendRequest(it)
            }

        this.binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = recyclerViewAdapter
        }

        this.binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!vm.noMoreFriendRequestsSent) {
                    val itemCount = layoutManager.itemCount
                    val lastItemPosition =
                        layoutManager.findLastCompletelyVisibleItemPosition()

                    if (lastItemPosition == itemCount - 5) {
                        recyclerViewAdapter.showItemLoader(FriendRequestSent())
                    }

                    if (lastItemPosition == itemCount - 1) {
                        vm.loadFriendRequestsSent()
                    }
                }
            }
        })
    }

    private fun registerObservers() {
        this.vm.friendRequestsSentList.observe(this.viewLifecycleOwner) {
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

        this.vm.deleteFriendRequest.observe(this.viewLifecycleOwner) {
            if (it.taskStatus == TaskStatus.STARTED) {

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

                val response = it.result!!.second

                if (response?.isSuccessful == true) {
                    val friendRequestSent = it.result.first
                    this.recyclerViewAdapter.removeItemById(friendRequestSent.id!!)
                    DecoratedViews.showSnackBar(
                        requireView(),
                        null,
                        "Friend request deleted",
                        Snackbar.LENGTH_LONG
                    )
                } else {
                    DecoratedViews.showSnackBar(
                        requireView(),
                        null,
                        response?.message.toString(),
                        Snackbar.LENGTH_LONG
                    )
                }
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