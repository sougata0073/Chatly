package com.sougata.chatly.features.chats.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.MainActivity
import com.sougata.chatly.R
import com.sougata.chatly.common.Keys
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.PrivateChat
import com.sougata.chatly.databinding.FragmentPrivateMessagesBinding
import com.sougata.chatly.features.chats.view_models.PrivateMessagesVM
import com.sougata.chatly.features.chats.view_models.PrivateMessagesVMFactory
import com.sougata.chatly.util.DecoratedViews

class PrivateMessagesFragment : Fragment() {

    private var _binding: FragmentPrivateMessagesBinding? = null
    private val binding get() = this._binding!!

    private lateinit var vm: PrivateMessagesVM

    private lateinit var privateChat: PrivateChat

    private lateinit var recyclerViewAdapter: PrivateMessagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentPrivateMessagesBinding.inflate(inflater, container, false)

        (requireActivity() as MainActivity).binding.apply {
            root.background =
                AppCompatResources.getDrawable(requireContext(), R.color.message_screen_bg)
            toolBar.visibility = View.GONE
            bottomNav.visibility = View.GONE
        }

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

        this.vm =
            ViewModelProvider(
                this,
                PrivateMessagesVMFactory(this.privateChat)
            )[PrivateMessagesVM::class.java]

        this.setupToolBar()
        this.setupMessagesRecyclerView()
        this.registerObservers()
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
                        recyclerViewAdapter.showItemLoader()
                    }

                    if (lastItemPosition == itemCount - 1) {
                        vm.loadPrivateMessages()
                    }
                }
            }
        })
    }

    private fun registerObservers() {
        this.vm.messagesList.observe(this.viewLifecycleOwner) {
            if (it.taskStatus == TaskStatus.STARTED) {

//                this.binding.viewBlocker.parentLayout.visibility = View.VISIBLE

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

//                this.binding.viewBlocker.parentLayout.visibility = View.GONE

//                var s = ""
//                for(item in it.result!!) {
//                    s += "${item.id} "
//                }
//                Log.d("TAGRV", "Ids; $s")

                this.recyclerViewAdapter.setItems(it.result ?: emptyList())

            } else if (it.taskStatus == TaskStatus.FAILED) {

//                this.binding.viewBlocker.parentLayout.visibility = View.GONE
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