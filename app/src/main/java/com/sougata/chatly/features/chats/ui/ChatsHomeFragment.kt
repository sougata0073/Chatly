package com.sougata.chatly.features.chats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.R
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.PrivateChat
import com.sougata.chatly.databinding.FragmentChatsHomeBinding
import com.sougata.chatly.features.chats.view_models.ChatsHomeVM
import com.sougata.chatly.features.discover.DiscoverButtonClickHandler
import com.sougata.chatly.util.DecoratedViews

class ChatsHomeFragment : Fragment() {

    private var _binding: FragmentChatsHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: ChatsHomeVM

    private lateinit var recyclerViewAdapter: ChatsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentChatsHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.vm = ViewModelProvider(this)[ChatsHomeVM::class.java]



        this.setupToolBar()
        this.setupRecyclerView()
        this.registerObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        this._binding = null
    }

    private fun setupToolBar() {
        this.binding.incToolBar.tvToolbarHeader.visibility = if (this.vm.isSearchViewExpanded) {
            View.GONE
        } else {
            View.VISIBLE
        }
        this.binding.incToolBar.apply {

            btnAdd.setOnClickListener(
                DiscoverButtonClickHandler(
                    btnAdd,
                    requireContext()
                )
            )

            searchView.apply {
                val searchViewEditText =
                    this.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

                searchViewEditText.apply {
                    setTextColor(
                        AppCompatResources.getColorStateList(
                            requireContext(),
                            R.color.black
                        )
                    )
                    hint = "Search"
                    setHintTextColor(
                        AppCompatResources.getColorStateList(
                            requireContext(),
                            R.color.grey
                        )
                    )
                }

                setOnSearchClickListener {
                    binding.incToolBar.tvToolbarHeader.visibility = View.GONE
                    vm.isSearchViewExpanded = true
                }
                setOnCloseListener {
                    binding.incToolBar.tvToolbarHeader.visibility = View.VISIBLE
                    vm.isSearchViewExpanded = false
                    false
                }
            }
        }
    }

    private fun setupRecyclerView() {
        this.recyclerViewAdapter = ChatsListAdapter(mutableListOf())
        this.binding.rvChatList.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )
        }

        this.binding.rvChatList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val layoutManager = binding.rvChatList.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!vm.noMoreChats) {
                    val itemCount = layoutManager.itemCount
                    val lastItemPosition =
                        layoutManager.findLastCompletelyVisibleItemPosition()

                    if (lastItemPosition == itemCount - 5) {
                        recyclerViewAdapter.showItemLoader(PrivateChat())
                    }

                    if (lastItemPosition == itemCount - 1) {
                        vm.loadPrivateChats()
                    }
                }
            }
        })
    }

    private fun registerObservers() {
        this.vm.chatsList.observe(this.viewLifecycleOwner) {
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

        this.vm.messageReceived.observe(this.viewLifecycleOwner) {
            if (it != null) {
                if (it.first != -1) {
                    this.recyclerViewAdapter.removeItemAt(it.first)
                }
                this.recyclerViewAdapter.insertItemAtFirst(it.second)
            }
        }

        this.vm.messageUpdated.observe(this.viewLifecycleOwner) {
            if (it != null) {
                this.recyclerViewAdapter.updateItemAt(it.first, it.second)
            }
        }

        this.vm.messageDeleted.observe(this.viewLifecycleOwner) {
            if (it != null) {
                val pc = it.second
                if (pc != null) {
                    this.recyclerViewAdapter.updateItemAt(it.first, pc)
                } else {
                    this.recyclerViewAdapter.removeItemAt(it.first)
                }
            }
        }
    }

}