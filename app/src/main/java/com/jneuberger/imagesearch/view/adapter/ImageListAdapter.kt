package com.jneuberger.imagesearch.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jneuberger.imagesearch.R
import com.jneuberger.imagesearch.entity.Image
import kotlinx.android.synthetic.main.image_grid_item.view.*


class ImageListAdapter(private val mContext: Context) : RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {
    private val mImageList = ArrayList<Image>()
    private lateinit var mHolder: ImageListAdapter.ViewHolder
    private lateinit var mOnClickListener: View.OnClickListener


    override fun getItemCount(): Int {
        return mImageList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.image_grid_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mHolder = holder
        mHolder.bindItems(mImageList[position])
        mHolder.itemView.setOnClickListener { view -> mOnClickListener.onClick(view) }
    }

    fun setOnClickListener(onClickListener: View.OnClickListener) {
        mOnClickListener = onClickListener
    }

    fun updateImageList(imageList: List<Image>) {
        mImageList.clear()
        mImageList.addAll(imageList)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItems(image: Image) {
            itemView.imageView.setImageBitmap(image.smallImage)
            itemView.description.text = image.description
            itemView.userFullName.text = image.user
        }
    }
}