package com.kimwash.defaultappbye


import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder
import java.util.ArrayList


class MainFragment(): Fragment() {
    private val TAG = "MainFragment"
    private var connected = false

    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val bundle = msg.data
            val connected = bundle.getBoolean("connected")
            if (connected){
                val ipAddress = bundle.getString("ipAddress")
                val ipLabel = view!!.findViewById<TextView>(R.id.clientIpAddress)
                ipLabel.setText("${getString(R.string.pIP)}: ${ipAddress}")
            }
            else if(!connected){
                view!!.findViewById<TextView>(R.id.connText).text = getString(R.string.connect)
                Toast.makeText(context!!, getString(R.string.disconnected), Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun wifiIpAddress(): String? {
        val wifiManager = context!!.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        var ipAddress = wifiManager.connectionInfo.ipAddress

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            ipAddress = Integer.reverseBytes(ipAddress)
        }
        val ipByteArray: ByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
        val ipAddressString: String?
        ipAddressString = try {
            InetAddress.getByAddress(ipByteArray).hostAddress
        } catch (ex: UnknownHostException) {
            Toast.makeText(context!!, getString(R.string.noWIfi), Toast.LENGTH_SHORT).show()
            null
        }
        return ipAddressString
    }


    override fun onResume() {
        super.onResume()
        if(Settings.Secure.getInt(context!!.getContentResolver(), Settings.Global.ADB_ENABLED, 0) != 1) {
            //;debugging does not enabled
            Util.dispDialog(context!!, getString(R.string.tOnDebugging), {
                val intent = Intent(ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                startActivity(intent)
            }, getString(R.string.info), null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val connectButton = view.findViewById<LinearLayout>(R.id.connect)
        val ipLabel = view.findViewById<TextView>(R.id.serverIpAddress)

        val pager = view.findViewById<ViewPager>(R.id.viewPager)
        pager.setPageTransformer(true, ZoomOutPageTransformer())

        var guide: ArrayList<Guide> = arrayListOf<Guide>()
        guide.add(Guide("https://yoon-lab.xyz/connect.png", getString(R.string.guide_1)))
        guide.add(Guide("https://yoon-lab.xyz/program.png", getString(R.string.guide_2)))
        guide.add(Guide("https://yoon-lab.xyz/click.png", getString(R.string.guide_3)))
        guide.add(Guide("https://yoon-lab.xyz/delete.png", getString(R.string.guide_4)))
        guide.add(Guide("https://yoon-lab.xyz/check.png", getString(R.string.guide_5)))
        val adapter: PagerAdapter = ViewPagerAdapter(context!!, guide)

        pager.adapter = adapter




        connectButton.setOnClickListener {
            if (ServerThread(context!!, handler).isLoop == true){
                ServerThread(context!!, handler).interrupt()
            }

            if (connected){
                connected = false
                ServerThread(context!!, handler).closeConnect()
                view.findViewById<TextView>(R.id.connText).text = getString(R.string.connect)
                Toast.makeText(context!!, getString(R.string.disconnected), Toast.LENGTH_SHORT).show()
            }
            else{
                connected = true
                ServerThread(context!!, handler).start()
                view.findViewById<TextView>(R.id.connText).text = getString(R.string.disConnect)
                Toast.makeText(context!!, getString(R.string.goodToGo), Toast.LENGTH_SHORT).show()
            }

        }
        val ip = wifiIpAddress()

        ipLabel.text = "${getString(R.string.aIP)} $ip"

        Log.e(TAG, "Launched")
    }

    override fun onDestroy() {
        super.onDestroy()
        ServerThread(context!!, handler).setIsLoop(false)
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }
}