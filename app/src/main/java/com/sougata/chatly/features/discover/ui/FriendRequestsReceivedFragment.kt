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
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.FriendRequestReceived
import com.sougata.chatly.data.models.ServerResponse
import com.sougata.chatly.databinding.FragmentFriendRequestsReceivedBinding
import com.sougata.chatly.features.discover.view_models.FriendRequestsReceivedVM
import com.sougata.chatly.util.DecoratedViews

class FriendRequestsReceivedFragment : Fragment() {

    private var _binding: FragmentFriendRequestsReceivedBinding? = null
    private val binding get() = this._binding!!

    private lateinit var vm: FriendRequestsReceivedVM

    private lateinit var recyclerViewAdapter: FriendRequestReceivedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentFriendRequestsReceivedBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.vm = ViewModelProvider(this)[FriendRequestsReceivedVM::class.java]

        this.setupRecyclerView()
        this.registerObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        this._binding = null
    }

    private fun setupRecyclerView() {
        val accept: (FriendRequestReceived) -> Unit = {
            this.vm.acceptFriendRequest(it)
        }
        val reject: (FriendRequestReceived) -> Unit = {
            this.vm.rejectFriendRequest(it)
        }

        this.recyclerViewAdapter =
            FriendRequestReceivedAdapter(mutableListOf(), accept, reject)

        this.binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = recyclerViewAdapter
        }

        this.binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!vm.noMoreFriendRequestsReceived) {
                    val itemCount = layoutManager.itemCount
                    val lastItemPosition =
                        layoutManager.findLastCompletelyVisibleItemPosition()

                    if (lastItemPosition == itemCount - 5) {
                        recyclerViewAdapter.showItemLoader(FriendRequestReceived())
                    }

                    if (lastItemPosition == itemCount - 1) {
                        vm.loadFriendRequestsReceived()
                    }
                }
            }
        })
    }

    private fun registerObservers() {
        this.vm.friendRequestsReceivedList.observe(this.viewLifecycleOwner) {
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
        this.vm.acceptFriendRequest.observe(this.viewLifecycleOwner) {
            if (it.taskStatus == TaskStatus.STARTED) {

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

                this.onAcceptOrRejectCompleted(it, "Friend request accepted")

            } else if (it.taskStatus == TaskStatus.FAILED) {
                DecoratedViews.showSnackBar(
                    requireView(),
                    null,
                    it.message,
                    Snackbar.LENGTH_LONG
                )
            }
        }
        this.vm.rejectFriendRequest.observe(this.viewLifecycleOwner) {
            if (it.taskStatus == TaskStatus.STARTED) {

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

                this.onAcceptOrRejectCompleted(it, "Friend request rejected")

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

    private fun onAcceptOrRejectCompleted(
        result: TaskResult<Pair<FriendRequestReceived, ServerResponse?>>,
        successMessage: String,
    ) {
        val response = result.result!!.second

        if (response?.isSuccessful == true) {
            val friendRequestReceived = result.result.first
            this.recyclerViewAdapter.removeItemById(friendRequestReceived.id!!)
            DecoratedViews.showSnackBar(
                requireView(),
                null,
                successMessage,
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
    }
}