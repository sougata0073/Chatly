package com.sougata.chatly.features.chats.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.R
import com.sougata.chatly.common.Keys
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.PrivateChat
import com.sougata.chatly.data.models.PrivateMessage
import com.sougata.chatly.databinding.FragmentPrivateMessagesBinding
import com.sougata.chatly.features.chats.view_models.PrivateMessagesVM
import com.sougata.chatly.features.chats.view_models.PrivateMessagesVMFactory
import com.sougata.chatly.util.Animations
import com.sougata.chatly.util.DateTime
import com.sougata.chatly.util.DecoratedViews
import io.github.jan.supabase.auth.auth

class PrivateMessagesFragment : Fragment() {

    private var _binding: FragmentPrivateMessagesBinding? = null
    private val binding get() = this._binding!!

    private lateinit var vm: PrivateMessagesVM

    private lateinit var privateChat: PrivateChat

    private lateinit var recyclerViewAdapter: PrivateMessagesAdapter

    private val currentUserId = MySupabaseClient.getInstance().auth.currentUserOrNull()!!.id

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        this._binding = FragmentPrivateMessagesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.privateChat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(Keys.PRIVATE_CHAT, PrivateChat::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable(Keys.PRIVATE_CHAT)
        }!!

        this.vm = ViewModelProvider(
            this,
            PrivateMessagesVMFactory(this.privateChat)
        )[PrivateMessagesVM::class.java]

        this.setupUI()
        this.setupToolBar()
        this.setupMessagesRecyclerView()
        this.setupSendButton()
        this.registerObservers()
    }

    private fun setupUI() {
        Glide.with(this.requireContext())
            .load(this.privateChat.otherUser?.profileImageUrl)
            .placeholder(R.drawable.ic_user_placeholder)
            .error(R.drawable.ic_user_placeholder)
            .into(this.binding.ivOtherUserProfileImage)

        val userInfoClickHandler: (View) -> Unit = {
            val user = privateChat.otherUser
            val bundle = Bundle().apply {
                putString(Keys.USER_ID, user?.id)
            }
            it.findNavController()
                .navigate(R.id.userProfileFragment, bundle, Animations.FRAGMENT_SLIDE)
        }

        this.binding.apply {
            tvOtherUserName.text = privateChat.otherUser?.name

            val phoneNumber = privateChat.otherUser?.phoneNumber
            val email = privateChat.otherUser?.email

            tvOtherUserContact.text = email ?: phoneNumber ?: "No contact available"

            ivOtherUserProfileImage.setOnClickListener(userInfoClickHandler)
            tvOtherUserName.setOnClickListener(userInfoClickHandler)
            tvOtherUserContact.setOnClickListener(userInfoClickHandler)
        }
    }

    private fun setupToolBar() {
        this.binding.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupMessagesRecyclerView() {
        this.recyclerViewAdapter = PrivateMessagesAdapter(mutableListOf())
        this.binding.rvMessages.apply {
            adapter = recyclerViewAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        }

        this.binding.rvMessages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val layoutManager = binding.rvMessages.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!vm.noMoreMessages) {
                    val itemCount = layoutManager.itemCount
                    val lastItemPosition =
                        layoutManager.findLastCompletelyVisibleItemPosition()

                    if (lastItemPosition == itemCount - 5) {
                        recyclerViewAdapter.showItemLoader(PrivateMessage())
                    }

                    if (lastItemPosition == itemCount - 1) {
                        vm.loadPrivateMessages()
                    }
                }
            }
        })
    }

    private fun setupSendButton() {
        this.binding.btnSendMessage.setOnClickListener {
            val text = this.binding.etMessageBox.text.toString()
            val receiverId = this.privateChat.otherUser?.id

            val tempId = this.recyclerViewAdapter.getItemIdAt(0)?.plus(1L) ?: 0L
            val tempPrivateMessage = PrivateMessage(
                id = tempId,
                text = text,
                senderId = this.currentUserId,
                receiverId = receiverId,
                mediaType = null,
                mediaUrl = null,
                createdAt = DateTime.getCurrentISOTimestamp()
            )
            this.recyclerViewAdapter.insertItemAtFirst(tempPrivateMessage)
            this.binding.rvMessages.scrollToPosition(0)
            this.binding.etMessageBox.text.clear()

            if (text.isNotEmpty()) {
                this.vm.insertMessage(tempPrivateMessage)
            }
        }
    }

    private fun registerObservers() {
        this.vm.messagesList.observe(this.viewLifecycleOwner) {
            if (it.taskStatus == TaskStatus.STARTED) {

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

                this.recyclerViewAdapter.setItems(it.result ?: emptyList())

            } else if (it.taskStatus == TaskStatus.FAILED) {
                DecoratedViews.showSnackBar(
                    requireView(),
                    null,
                    it.message,
                    Snackbar.LENGTH_LONG
                )
            }
        }

        this.vm.messageSent.observe(this.viewLifecycleOwner) {
            if (it.taskStatus == TaskStatus.STARTED) {

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

                val tempId = it.result!!.first
                val privateMessage = it.result.second!!

                this.recyclerViewAdapter.updateItemById(tempId, privateMessage)

            } else if (it.taskStatus == TaskStatus.FAILED) {

                val tempId = it.result!!.first
                this.recyclerViewAdapter.removeItemById(tempId)

                DecoratedViews.showSnackBar(
                    requireView(),
                    null,
                    it.message,
                    Snackbar.LENGTH_LONG
                )
            }
        }

        this.vm.messageReceived.observe(this.viewLifecycleOwner) {
            if (it != null) {
                this.recyclerViewAdapter.insertItemAtFirst(it.second)
                this.binding.rvMessages.scrollToPosition(it.first)
            }
        }

        this.vm.messageUpdated.observe(this.viewLifecycleOwner) {
            if (it != null) {
                this.recyclerViewAdapter.updateItemAt(it.first, it.second)
            }
        }

        this.vm.messageDeleted.observe(this.viewLifecycleOwner) {
            if (it != null) {
                this.recyclerViewAdapter.removeItemAt(it.first)
            }
        }
    }

}