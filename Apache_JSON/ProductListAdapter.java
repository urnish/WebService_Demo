package com.nationalhandloomcorp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nationalhandloomcorp.ImageSlider.SmallBang;
import com.nationalhandloomcorp.R;
import com.nationalhandloomcorp.dbhandler.DBHandler;
import com.nationalhandloomcorp.model.ProductlistModel;
import com.nationalhandloomcorp.util.Constant;
import com.nationalhandloomcorp.util.MyUpdateWishList;

import java.util.ArrayList;


public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    Context context;
    Activity main;
    ArrayList<ProductlistModel> listData;
    LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;
    DBHandler mDBHandler;
    MyUpdateWishList updateWishList;
    SharedPreferences mprefs;

    public ProductListAdapter(Activity activity,Context context, ArrayList<ProductlistModel> bean) {

        this.main = activity;

        this.listData = bean;
        this.context = context;
        this.inflater = (LayoutInflater.from(context));
        mDBHandler = new DBHandler(context);
        mprefs = context.getSharedPreferences(Constant.PREFS_NAME,context.MODE_PRIVATE);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener)
    {
        this.mOnItemClickListener = mItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView title;
        TextView price;
        TextView cutprice;
        TextView discount;
        TextView ratingtext;
        ImageView fav1;
        RelativeLayout relWishlike;
        LinearLayout lidiscount;
        SmallBang mSmallBang;
        public LinearLayout lyt_parent;
        LinearLayout productitems;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.produclistImage);
            title = (TextView) v.findViewById(R.id.prodcuListtitle);
            price = (TextView) v.findViewById(R.id.productListPrice);
            discount = (TextView) v.findViewById(R.id.produclistdiscount);
            cutprice = (TextView) v.findViewById(R.id.productListmrp);
            lidiscount = (LinearLayout) v.findViewById(R.id.lidiscount);
            ratingtext = (TextView) v.findViewById(R.id.ratingtext);
            fav1 = (ImageView) v.findViewById(R.id.fav1);
            lyt_parent = (LinearLayout) v.findViewById(R.id.productitems);
            productitems= (LinearLayout) v.findViewById(R.id.productitems);
            relWishlike  = (RelativeLayout) v.findViewById(R.id.relWishlike);

        }

    }

    @Override
    public ProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_productlist, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ProductListAdapter.ViewHolder holder, final int position) {
        final ProductlistModel bean = listData.get(position);
        holder.title.setText(bean.getName());

        holder.price.setText(context.getResources().getString(R.string.Rs)+" "+bean.getPrice());
        Glide.with(context)
                .load(bean.getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.default_icon)
                .into(holder.image);

        if(bean.getMrp().length()==0)
        {
            holder.cutprice.setVisibility(View.GONE);
        }
        else {
            holder.cutprice.setText(context.getResources().getString(R.string.Rs)+" "+bean.getMrp());
            holder.cutprice.setPaintFlags(holder.cutprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if(bean.getDiscount().length()==0)
        {
            holder.lidiscount.setVisibility(View.GONE);
        }
        else {
            holder.discount.setText(""+bean.getDiscount()+"%");
        }

        holder.mSmallBang = SmallBang.attach2Window(main);

        if (mDBHandler.ifExists(listData.get(position).getProductID()))
        {
            holder.fav1.setImageResource(R.drawable.ic_selected);
        }
        else {
            holder.fav1.setImageResource(R.drawable.ic_unselected);
        }

        holder.relWishlike.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnItemClickListener != null)
                {
                    if (mDBHandler.ifExists(listData.get(position).getProductID()))
                    {
                        mDBHandler.Delete_Contact(listData.get(position).getProductID());
                        holder.fav1.setImageResource(R.drawable.ic_unselected);
                        mprefs.edit().putInt(Constant.USER_WISHLIST ,mDBHandler.getAllProduct().size()).apply();

                    }
                    else
                    {
                        mDBHandler.addProduct(listData.get(position).getProductID());
                        mOnItemClickListener.onItemClick(position,v,1);
                        holder.fav1.setImageResource(R.drawable.ic_selected);
                        // For animation on click of button
                        holder.mSmallBang.bang(v);
                        mprefs.edit().putInt(Constant.USER_WISHLIST ,mDBHandler.getAllProduct().size()).apply();
                      }
                }
            }
        });

        holder.productitems.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mOnItemClickListener != null)
                {
                    mOnItemClickListener.onItemClick(position,view,0);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

}







