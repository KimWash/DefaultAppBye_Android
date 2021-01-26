package com.kimwash.defaultappbye

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject


class ViewPagerAdapter(context: Context, data:JSONArray) :
    PagerAdapter() {
    private val mContext: Context
    private val mData: JSONArray
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.adapterpage, container, false)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val explainText = view.findViewById<TextView>(R.id.explain)
        Glide.with(mContext)
            .load(mData.getJSONObject(position).getString("url"))
            .into(imageView)
        explainText.text = mData.getJSONObject(position).getString("explain")

        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return mData.length()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o as View
    }

    init {
        mContext = context
        this.mData = data
    }
}