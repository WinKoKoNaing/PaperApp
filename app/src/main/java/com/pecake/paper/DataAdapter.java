package com.pecake.paper;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private ArrayList<Faq> faqs;

    public DataAdapter(ArrayList<Faq> faqs) {
        this.faqs = faqs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvQ.setText(faqs.get(position).getQ());
        holder.tvAns.setText(faqs.get(position).getAns());
    }

    @Override
    public int getItemCount() {
        return faqs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvQ;
        private TextView tvAns;

        public ViewHolder(View itemView) {
            super(itemView);

            tvQ = (TextView)itemView.findViewById(R.id.tv_Q);
            tvAns = (TextView)itemView.findViewById(R.id.tv_Ans);

        }
    }
}
