package com.sougata.chatly.features.chats.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.databinding.FragmentChatsHomeBinding
import com.sougata.chatly.features.chats.view_models.ChatsHomeVM
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

        this.recyclerViewAdapter = ChatsListAdapter(mutableListOf())
        this.binding.rvChatList.adapter = this.recyclerViewAdapter
        this.binding.rvChatList.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )

        this.registerObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        this._binding = null
    }

    private fun registerObservers() {
        this.vm.chatsList.observe(this.viewLifecycleOwner) {
            Log.d("TAG", "Chats list: $it")
            if (it.taskStatus == TaskStatus.STARTED) {

//                this.binding.viewBlocker.parentLayout.visibility = View.VISIBLE

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

//                this.binding.viewBlocker.parentLayout.visibility = View.GONE

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