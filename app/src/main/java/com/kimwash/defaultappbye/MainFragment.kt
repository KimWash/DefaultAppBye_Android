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


class MainFragment(): Fragment() {
    private val TAG = "MainFragment"
    private var connected = false

    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val bundle = msg.data
            val connected = bundle.getBoolean("connected")
            val ipAddress = bundle.getString("ipAddress")
            val ipLabel = view!!.findViewById<TextView>(R.id.clientIpAddress)
            ipLabel.setText("PC IP: ${ipAddress}")
        }
    }

    fun wifiIpAddress(): String? {
        val wifiManager = context!!.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        var ipAddress = wifiManager.connectionInfo.ipAddress

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress)
        }
        val ipByteArray: ByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
        val ipAddressString: String?
        ipAddressString = try {
            InetAddress.getByAddress(ipByteArray).getHostAddress()
        } catch (ex: UnknownHostException) {
            Toast.makeText(context!!, "와이파이에 연결되어 있지 않은 것 같습니다. 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
            null
        }
        return ipAddressString
    }


    override fun onResume() {
        super.onResume()
        if(Settings.Secure.getInt(context!!.getContentResolver(), Settings.Global.ADB_ENABLED, 0) != 1) {
            //;debugging does not enabled
            Util.dispDialog(context!!, "USB 디버깅을 활성화해야 정상적으로 이용하실 수 있습니다.", {
                val intent = Intent(ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                startActivity(intent)
            }, "안내", null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val connectButton = view.findViewById<LinearLayout>(R.id.connect)
        val ipLabel = view.findViewById<TextView>(R.id.serverIpAddress)

        val pager = view.findViewById<ViewPager>(R.id.viewPager)
        pager.setPageTransformer(true, ZoomOutPageTransformer())
        val data = JSONArray()
        val data1 = JSONObject()
        val data2 = JSONObject()
        data1.put("url", "https://yoon-lab.xyz/computer.png")
        data1.put("explain", "1. 스마트폰을 PC에 연결합니다.")
        data2.put("url", "https://yoon-lab.xyz/exe-file.png")
        data2.put("explain", "2. PC 프로그램을 실행합니다.")
        data.put(data1)
        data.put(data2)
        val adapter: PagerAdapter = ViewPagerAdapter(context!!, data)

        pager.adapter = adapter




        connectButton.setOnClickListener {
            if (ServerThread(context!!, handler).isLoop == true){
                ServerThread(context!!, handler).interrupt()
            }

            if (connected){
                connected = false
                ServerThread(context!!, handler).closeConnect()
                view.findViewById<TextView>(R.id.connText).text = "연결"
                Toast.makeText(context!!, "연결이 해제되었습니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                connected = true
                ServerThread(context!!, handler).start()
                view.findViewById<TextView>(R.id.connText).text = "연결 해제"
                Toast.makeText(context!!, "이제 PC에서 앱 삭제 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show()
            }

        }
        val ip = wifiIpAddress()

        ipLabel.text = "Your IP : $ip"

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