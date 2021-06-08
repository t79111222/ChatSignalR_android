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
import tw.com.intersense.signalrchat.EventObserver
import tw.com.intersense.signalrchat.MySharedPreferences
import tw.com.intersense.signalrchat.databinding.FragmentChatBinding
import javax.inject.Inject
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import tw.com.intersense.signalrchat.R


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
        viewModel.setChatValue(args.productId, args.askerPhoneId, args.product)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
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

        mAdapter = MessageAdapter(this,mySharedPreferences.getPhoneId()?:"", phpneDensity)
        mAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (binding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })
        binding.recyclerView.apply {
            itemAnimator = null
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context).apply {
                reverseLayout = true
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
        var myPhoneId = mySharedPreferences.getPhoneId()
        if (viewModel.chat != null){
            var chat = viewModel.chat!!
            var urlOther = ""
            if(chat.AskerPhoneId != myPhoneId){
                binding.title.text = chat.AskerName
                urlOther = chat.AskerImageLink?:""
            }else{
                binding.title.text = chat.OwnerName
                urlOther = chat.OwnerImageLink?:""
            }
            var urlProduct = ""
            chat.ProductImagesString?.let {
                val listProductImage =it.split(",").toTypedArray()
                if(listProductImage.isNotEmpty()) urlProduct = listProductImage[0]
            }
            Glide.with(this).load(urlProduct).placeholder(R.drawable.shipping).into(binding.ivProduct)
            binding.tvProduct.text = chat.ProductName
            binding.tvPrice.text = "NT ${chat.Price}"
        }
    }

    private fun setupObserve() {

        viewModel.action.observe(viewLifecycleOwner, EventObserver {
            when(it.actionType) {
                ChatActionType.TokenFail -> gotoLogin()
                ChatActionType.Connected -> binding.disconnected.visibility = View.GONE
                ChatActionType.Disconnected -> binding.disconnected.visibility = View.VISIBLE
                ChatActionType.OnChatChange ->{
                    setTitleInfo()
//                    setListMessageObserve()
//                    mAdapter.submitList(viewModel.listMessage.value)
                }
                ChatActionType.OnMessageUpdate ->{
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