package tw.com.intersense.signalrchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import tw.com.intersense.signalrchat.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavi()
    }

    private fun setupNavi() {
        val navView = binding.navView
        val f = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        NavigationUI.setupWithNavController(navView, f.findNavController())
    }
}