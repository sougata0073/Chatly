package com.sougata.chatly.features.discover.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.R
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.SearchedUser
import com.sougata.chatly.databinding.FragmentAddFriendBinding
import com.sougata.chatly.features.discover.view_models.AddFriendVM
import com.sougata.chatly.util.DecoratedViews

class AddFriendFragment : Fragment() {

    private var _binding: FragmentAddFriendBinding? = null
    private val binding get() = this._binding!!

    private lateinit var vm: AddFriendVM

    private lateinit var recyclerViewAdapter: AddFriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentAddFriendBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.vm = ViewModelProvider(this)[AddFriendVM::class.java]

        this.setupToolBar()
        this.setupSearchView()
        this.setupRecyclerView()
        this.registerObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        this._binding = null
    }

    private fun setupToolBar() {
        this.binding.toolBar.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }
    }

    private fun setupSearchView() {
        this.binding.searchView.apply {
            val editText =
                this.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

            editText.apply {
                setTextColor(
                    AppCompatResources.getColorStateList(
                        requireContext(),
                        R.color.bw
                    )
                )
                hint = "Enter name or contact"
                setHintTextColor(
                    AppCompatResources.getColorStateList(
                        requireContext(),
                        R.color.grey
                    )
                )
            }
        }

        this.binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    vm.reset()
                    recyclerViewAdapter.setItems(emptyList())
                } else {
                    vm.loadSearchedUsers(newText)
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        this.recyclerViewAdapter = AddFriendAdapter(mutableListOf())
        this.binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = recyclerViewAdapter
        }

        this.binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!vm.noMoreSearchedUsers) {
                    val itemCount = layoutManager.itemCount
                    val lastItemPosition =
                        layoutManager.findLastCompletelyVisibleItemPosition()

                    if (lastItemPosition == itemCount - 5) {
                        recyclerViewAdapter.showItemLoader(SearchedUser())
                    }

                    if (lastItemPosition == itemCount - 1) {
                        vm.loadMoreSearchedUsers()
                    }
                }
            }
        })
    }

    private fun registerObservers() {
        this.vm.searchedUsersList.observe(this.viewLifecycleOwner) {
            if (it.taskStatus == TaskStatus.STARTED) {

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

                if (!it.result.isNullOrEmpty()) {
                    this.recyclerViewAdapter.setItems(it.result)
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