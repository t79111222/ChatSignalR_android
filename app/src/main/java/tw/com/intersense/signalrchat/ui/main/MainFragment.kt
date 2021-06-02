package tw.com.intersense.signalrchat.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import tw.com.intersense.signalrchat.EventObserver
import tw.com.intersense.signalrchat.MySharedPreferences
import tw.com.intersense.signalrchat.databinding.FragmentMainBinding
import tw.com.intersense.signalrchat.ui.chat.ChatFragmentArgs
import tw.com.intersense.signalrchat.ui.login.LoginFragmentDirections
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val TAG = "MainFragment"

    @Inject
    lateinit var mySharedPreferences : MySharedPreferences

    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    lateinit var adapter: MainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupObserve()

    }
    override fun onResume() {
        super.onResume()
        if(!mySharedPreferences.isLogin()){
            gotoLogin()
            return
        }
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun setupList() {
        adapter = MainAdapter(){
            //item click
            chatId ->
            val navController = NavHostFragment.findNavController(this)
            val action = MainFragmentDirections.actionMainFragmentToChatFragment(chatId = chatId)
            navController.navigate(action)
        }
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (binding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = adapter
    }

    private fun setupObserve() {

        viewModel.listChat.observe(viewLifecycleOwner,
            {
                Timber.d(TAG, "ListChat onChanged: ")
                adapter.submitList(it)
            })

        viewModel.action.observe(viewLifecycleOwner, EventObserver {
            when(it.actionType) {
                MainActionType.TokenFail -> gotoLogin()
                MainActionType.Connected -> binding.disconnected.visibility = View.GONE
                MainActionType.Disconnected -> binding.disconnected.visibility = View.VISIBLE
            }
        })
    }

    private fun gotoLogin() {
        mySharedPreferences.isLogin(false)
        mySharedPreferences.setToken("")
        val navController = NavHostFragment.findNavController(this)
        val action = MainFragmentDirections.actionMainFragmentToLoginFragment()
        navController.navigate(action)
    }
}