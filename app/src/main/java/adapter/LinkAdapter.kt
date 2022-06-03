package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.linkapp.R
import jdo.Link

class LinkAdapter(var mContext: Context, var mList: ArrayList<Link>) :
    RecyclerView.Adapter<LinkAdapter.ViewHolder>() {

    val mListCopy:ArrayList<Link> = mList.clone() as ArrayList<Link>


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mName: TextView
        var mLink: TextView
        var mImage:ImageView
        var view: View = itemView

        init {
            mName = view.findViewById(R.id.linkName)
            mLink = view.findViewById(R.id.linkUrl)
            mImage = view.findViewById(R.id.thumbnail)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.list_items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mName.text = mList[position].name
        holder.mLink.text = mList[position].link_url
        Glide.with(mContext).load(mList[position].image_url).into(holder.mImage)
    }

    fun filter(pText: String) {
        val newList = ArrayList<Link>()
        mList.clear()
        if (pText.isEmpty()) {
            mList.addAll(mListCopy as Collection<Link>)
        } else {
            for (link in mListCopy) {
                if (link.name.contains(pText)) {
                    newList.add(link)
                }
            }
            mList.addAll(newList)
        }
    }
}