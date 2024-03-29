package com.renata.view.fragment.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.renata.R
import com.renata.data.user.login.LoginPreferences
import com.renata.data.user.login.LoginResult
import com.renata.databinding.FragmentAccountBinding
import com.renata.view.activity.profile.ProfileActivity
import com.renata.view.activity.profile.ProfileViewModel
import com.renata.view.activity.setting.SettingActivity
import com.renata.view.activity.splash.SplashScreenActivity

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val accountBinding get() = _binding!!
    private var accountViewModel: ProfileViewModel = ProfileViewModel()
    private lateinit var loginPreference: LoginPreferences
    private lateinit var loginResult: LoginResult
    private val profileActivityRequestCode = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return accountBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginPreference = LoginPreferences(requireContext())
        loginResult = loginPreference.getUser()
        accountViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[ProfileViewModel::class.java]
        val token = loginResult.token
        getData(token)
        goToSetting()
        changeProfile(token)
        logout()
    }


    private fun getData(token: String?) {
        val getToken = "Bearer $token"
        if (getToken != null) {
            accountViewModel.userProfile(getToken)
            accountViewModel.getUserProfile().observe(viewLifecycleOwner) { response ->
                showLoading(false)
                if (response != null && response.success) {
                    val data = response.data
                    accountBinding.tvProfileEmail.text = data.email
                    val image = data.avatarLink
                    val name = data.fullName
                    if (image == "" || name == "") {
                        accountBinding.tvProfileName.text = ""
                        accountBinding.profileImage.setImageResource(R.drawable.image_placeholder)
                    } else {
                        accountBinding.tvProfileName.text = data.fullName
                        Glide.with(this@AccountFragment)
                            .load(data.avatarLink)
                            .into(accountBinding.profileImage)
                    }
                }
            }
        }
    }

    private fun changeProfile(token: String?) {
        accountBinding.editProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            intent.putExtra("token", token)
            startActivityForResult(intent, profileActivityRequestCode)
            requireActivity().overridePendingTransition(
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        showLoading(false)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == profileActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val tes = loginResult.token
            val tokenNow = "Bearer $tes"
            accountViewModel.userProfile(tokenNow)
            accountViewModel.getUserProfile().observe(viewLifecycleOwner) {
                val data = it.data
                val image = data.avatarLink
                if (image == "") {
                    accountBinding.profileImage.setImageResource(R.drawable.image_placeholder)
                } else {
                    Glide.with(requireActivity())
                        .load(image)
                        .into(accountBinding.profileImage)
                }
            }
            val name = data?.getStringExtra("name")
            val email = data?.getStringExtra("email")
            accountBinding.tvProfileName.text = name
            accountBinding.tvProfileEmail.text = email
        }
    }

    private fun goToSetting() {
        accountBinding.settingButton.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom
            )
        }
    }

    private fun logout() {
        accountBinding.logoutButton.setOnClickListener {
            loginPreference.removeUser()
            val intentToSplash = Intent(requireContext(), SplashScreenActivity::class.java)
            startActivity(intentToSplash)
            activity?.finishAffinity()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        accountBinding.progressBar2?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}