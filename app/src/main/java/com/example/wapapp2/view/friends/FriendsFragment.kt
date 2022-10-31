package com.example.wapapp2.view.friends


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentFriendsBinding
import com.example.wapapp2.databinding.FragmentMyprofileBinding
import com.example.wapapp2.main.MainActivity
import com.example.wapapp2.view.login.ProfileAdapter
import com.example.wapapp2.view.login.Profiles
import com.example.wapapp2.view.myprofile.MyprofileFragment
import org.w3c.dom.Text

class FriendsFragment : Fragment() {
    private lateinit var viewBinding: FragmentFriendsBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentFriendsBinding.inflate(inflater)

        val mactivity = activity as MainActivity
        val myprofile = Profiles(R.drawable.man, "김성윤", "ksu8063@naver.com")

        val profileList = arrayListOf(
                Profiles(R.drawable.man, "김성윤", "ksu8063@naver.com"),
                Profiles(R.drawable.man, "박준성", "ksu8063@naver.com"),
                Profiles(R.drawable.girl, "김진우", "nbmlon99@naver.com")
        )

        viewBinding.rvMyprofile.setOnClickListener {
            mactivity.gotoMyprofile()
        }

        viewBinding.rvMyprofile.findViewById<ImageView>(R.id.iv_profile).setImageResource(myprofile.gender)
        viewBinding.rvMyprofile.findViewById<TextView>(R.id.user_name1).text = myprofile.name
        viewBinding.rvMyprofile.findViewById<TextView>(R.id.user_id1).text = myprofile.userid

        viewBinding.rvProfile.setHasFixedSize(true)
        viewBinding.rvProfile.adapter = ProfileAdapter(profileList)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}