package tw.com.intersense.signalrchat.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import tw.com.intersense.signalrchat.EventObserver
import tw.com.intersense.signalrchat.MySharedPreferences
import tw.com.intersense.signalrchat.databinding.FragmentLoginBinding
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val TAG = "LoginFragment"

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var mySharedPreferences: MySharedPreferences

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.login.setOnClickListener {
            var userName = binding.username.text.toString()
            var pwd = binding.password.text.toString()
            if (userName.isNullOrEmpty() || pwd.isNullOrEmpty()) {
                return@setOnClickListener
            }
            binding.loading.visibility = View.VISIBLE
            viewModel.login(userName, pwd);
        }

        viewModel.action.observe(viewLifecycleOwner, EventObserver {
            binding.loading.visibility = View.GONE
            if (it.respose == null) {
                Toast.makeText(context, "登入失敗", Toast.LENGTH_SHORT).show()
                return@EventObserver
            }
            it.respose!!.let { r ->
                if (r.resultCode != 0) {
                    Toast.makeText(context, r.message, Toast.LENGTH_SHORT).show()
                    return@EventObserver
                }
                mySharedPreferences.isLogin(true)
                mySharedPreferences.setToken(r.accessToken)
                mySharedPreferences.setUserName(r.userName)
                mySharedPreferences.setUserId(r.userId)
                val navController = NavHostFragment.findNavController(this)
                val action = LoginFragmentDirections.actionLoginFragmentToMainFragment()
                navController.navigate(action)
            }
        })
    }
}