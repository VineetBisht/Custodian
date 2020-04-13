package com.example.custodian.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.custodian.R;

import java.util.List;

public class HomePagerAdapter extends PagerAdapter {
    private List<String> options;
    Context context;
    OnPagerListener onPagerListener;

    HomePagerAdapter(List<String> options,  Context context, OnPagerListener onPagerListener) {
        this.options = options;
        this.context = context;
        this.onPagerListener=onPagerListener;
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.main_options, container
                        , false);

        TextView card = itemView.findViewById(R.id.name);
        final int positionInside=position;
        ImageView image = itemView.findViewById(R.id.option_image);

        itemView.findViewById(R.id.cardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPagerListener.onPagerClick(positionInside);
            }
        });

        card.setText(options.get(position));
        image.setImageResource(R.drawable.ic_tag_faces_black_24dp);
//        switch (options.get(position)) {
//            case "Quick Fixes":
//                image.setImageResource(R.mipmap.fix);
//                break;
//            case "Cost Reduction":
//                image.setImageResource(R.mipmap.money);
//                break;
//            case "New Booking":
//                image.setImageResource(R.mipmap.calendar);
//                break;
//
//            default:
//                Log.e(HomePagerAdapter.class.getName(), "Image Resource Error");
//                break;
//        }
        container.addView(itemView, 0);
        return itemView;
    }

    public interface OnPagerListener{
        void onPagerClick(int position);
    }

    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((CardView)object);
    }
}

