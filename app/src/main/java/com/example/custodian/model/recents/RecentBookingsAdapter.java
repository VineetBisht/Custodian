package com.example.custodian.model.recents;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custodian.R;
import com.example.custodian.model.caretaker.Caretaker;
import com.example.custodian.model.caretaker.Hired;

public class RecentBookingsAdapter extends RecyclerView.Adapter<RecentBookingsAdapter.MyViewHolder> {

    Hired hired;
    public Caretaker temp_C;
    public String temp_d;

    public RecentBookingsAdapter(Hired hired) {
        this.hired = hired;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_item, parent
                        , false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int pos = position + 1;
        holder.position.setText(String.valueOf(pos));
        holder.date.setText(hired.getDate_of_hire().get(position));
        holder.email.setText(hired.getCaretaker().get(position).getMail());
        holder.name.setText(hired.getCaretaker().get(position).getFullname());
    }

    @Override
    public int getItemCount() {
        if (hired != null) {
            return hired.getCaretaker().size();
        } else
            return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView position, name, date, email;

        public MyViewHolder(View view) {
            super(view);
            position = view.findViewById(R.id.position);
            name = view.findViewById(R.id.showname);
            date = view.findViewById(R.id.showdate);
            email = view.findViewById(R.id.showemail);
        }
    }

    public Hired removeItem(int position) {
        temp_C = hired.getCaretaker().get(position);
        temp_d = hired.getDate_of_hire().get(position);
        hired.getCaretaker().remove(position);
        hired.getDate_of_hire().remove(position);
        notifyItemRemoved(position);
        return hired;
    }

    public Hired restoreItem(int position) {
        hired.getCaretaker().add(position, temp_C);
        hired.getDate_of_hire().add(position, temp_d);
        notifyItemInserted(position);
        return hired;
    }
}
