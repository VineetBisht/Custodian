package com.example.custodian.model.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.custodian.R;
import com.example.custodian.model.caretaker.Caretaker;
import com.example.custodian.model.caretaker.CaretakerManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomePagerAdapter extends PagerAdapter {

    private List<Caretaker> caretakers;
    Context context;
    OnPagerListener onPagerListener;

    public HomePagerAdapter(Context context, OnPagerListener onPagerListener) {
        this.context = context;
        this.onPagerListener = onPagerListener;
        caretakers = CaretakerManager.getCaretakers();
    }

    @Override
    public int getCount() {
        return caretakers.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.caretaker_item, container
                        , false);

        TextView card = itemView.findViewById(R.id.name);
        final int positionInside = position;
        de.hdodenhof.circleimageview.CircleImageView image = itemView.findViewById(R.id.option_image);

        itemView.findViewById(R.id.cardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPagerListener.onPagerClick(positionInside);
            }
        });

        card.setText(caretakers.get(position).getFullname());

        Picasso.get().load(caretakers.get(position).getImage()).into(image);

        container.addView(itemView, 0);
        return itemView;
    }

    public interface OnPagerListener {
        void onPagerClick(int position);
    }

    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((CardView) object);
    }
}

