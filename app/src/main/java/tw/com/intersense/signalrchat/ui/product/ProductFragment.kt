package tw.com.intersense.signalrchat.ui.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tw.com.intersense.signalrchat.EventObserver
import tw.com.intersense.signalrchat.databinding.FragmentProductBinding

@AndroidEntryPoint
class ProductFragment : Fragment() {
    private val TAG = "MainFragment"


    private var _binding : FragmentProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()
    lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupObserve()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateProduct()
    }

    private fun setupList() {
        adapter = ProductAdapter(){
            //item click
            item ->
            val navController = NavHostFragment.findNavController(this)
            val action = ProductFragmentDirections.actionProductFragmentToChatFragment(
                productId = item.id, productName = item.name,
                productUserId = item.userId, productUserName = item.userName)
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

        viewModel.action.observe(viewLifecycleOwner, EventObserver {
            when(it.actionType) {
                ProductActionType.UpdateProductResponse -> {
                    it.exceptionMessage?.let {
                        Toast.makeText(context, "更新失敗", Toast.LENGTH_LONG).show()
                    }
                    it.ProductsResponse?.let {
                        respose ->
                        if(respose.resultCode != 0){
                            Toast.makeText(context, "更新失敗", Toast.LENGTH_LONG).show()
                        }
                        else{
                            adapter.submitList(respose.listProduct)
                        }
                    }
                }
            }
        })
    }

}