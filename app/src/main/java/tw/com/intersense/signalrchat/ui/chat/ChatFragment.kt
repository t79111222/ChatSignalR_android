package tw.com.intersense.signalrchat.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import tw.com.intersense.signalrchat.EventObserver
import tw.com.intersense.signalrchat.MySharedPreferences
import tw.com.intersense.signalrchat.databinding.FragmentChatBinding
import javax.inject.Inject
import android.widget.Toast
import androidx.core.widget.addTextChangedListener


@AndroidEntryPoint
class ChatFragment : Fragment() {
    private val TAG = "ChatFragment"

    @Inject
    lateinit var mySharedPreferences : MySharedPreferences

    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private val args: ChatFragmentArgs by navArgs()
    private lateinit var mAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setChatValue(args.chatId, args.productId)
        setupList()
        setSendEditText()
        setupObserve()

    }
    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun setupList() {
        val phpneDensity = requireContext().resources.displayMetrics.density

        mAdapter = MessageAdapter(mySharedPreferences.getUserId()?:"", phpneDensity)
        mAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (binding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })
        binding.recyclerView.apply {
            itemAnimator = null
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
                reverseLayout = false
            }
        }
    }

    private fun setSendEditText(){
        binding.send.isEnabled = false
        binding.etMessage.addTextChangedListener(onTextChanged = {
                text, start, before, count ->
            binding.send.isEnabled = !text.isNullOrEmpty()
        })
        binding.send.setOnClickListener{
            it.isEnabled = false
            var text = binding.etMessage.text.toString()
            if(!viewModel.onSend(text)){
                //發送失敗
                Toast.makeText(context, "訊息發送失敗", Toast.LENGTH_SHORT).show()
            }else{
                binding.etMessage.text.clear()
            }
            it.isEnabled = true
        }
    }

    private fun setTitleInfo(){
        var myUserId = mySharedPreferences.getUserId()
        if (viewModel.chat == null && args.productId != 0){
            binding.tvRequest.text = if(myUserId != args.productUserId) "向 ${args.productUserName} 提問:" else ""
            binding.tvProduct.text = args.productName
        } else if(viewModel.chat != null){
            var chat = viewModel.chat!!
            if(viewModel.productUser == null && viewModel.reqestUser != null){
                //不知道產品是誰的，先當成自己的產品
                viewModel.reqestUser?.name.let { binding.tvRequest.text = "$it 向你提問:" }
            } else  if(viewModel.productUser != null && viewModel.reqestUser != null){
                if(viewModel.productUser!!.id == myUserId){
                    viewModel.reqestUser?.name.let { binding.tvRequest.text = "$it 向你提問:" }
                }
                else{
                    viewModel.productUser?.name.let {  binding.tvRequest.text =  "向 $it 提問:" }
                }
            }
            binding.tvProduct.text = chat.productName
        }
    }

    private fun setListMessageObserve(){
        viewModel.listMessage.observe(viewLifecycleOwner,
            {
                Timber.d(TAG, "ListChat onChanged: ")
                mAdapter.submitList(it)
            })
    }

    private fun setupObserve() {

        viewModel.action.observe(viewLifecycleOwner, EventObserver {
            when(it.actionType) {
                ChatActionType.TokenFail -> gotoLogin()
                ChatActionType.Connected -> binding.disconnected.visibility = View.GONE
                ChatActionType.Disconnected -> binding.disconnected.visibility = View.VISIBLE
                ChatActionType.OnChatChange ->{
                    setTitleInfo()
                    setListMessageObserve()
                    mAdapter.submitList(viewModel.listMessage.value)
                }
            }
        })
    }

    private fun gotoLogin() {
        mySharedPreferences.isLogin(false)
        mySharedPreferences.setToken("")
        //TODO goto login
//        val navController = NavHostFragment.findNavController(this)
//        val action = ChatFragmentDirections.actionMainFragmentToLoginFragment()
//        navController.navigate(action)
    }
}