package com.pecake.paper.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pecake.paper.R;
import com.pecake.paper.activities.ActivityUserCategory;
import com.pecake.paper.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    List<Category> categoryList;
    Context context;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_category_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {
        holder.title.setText(categoryList.get(position).title);
        Glide.with(context).load(categoryList.get(position).photoUri).apply(RequestOptions.circleCropTransform()).into(holder.ivCategoryImage);
        holder.ReligiousCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Category",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, ActivityUserCategory.class);
                i.putExtra("title",categoryList.get(position).title);
                i.putExtra("user_id",categoryList.get(position).userId);
                i.putExtra("category_id",categoryList.get(position).categoryId);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
     }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryImage;
        TextView title;
        LinearLayout ReligiousCard;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvCategoryTitleAdapter);
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImageAdapter);
            ReligiousCard = itemView.findViewById(R.id.ReligiousCard);
        }
    }
}
