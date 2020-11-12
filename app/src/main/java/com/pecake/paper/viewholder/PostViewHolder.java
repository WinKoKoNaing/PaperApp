package com.pecake.paper.viewholder;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pecake.paper.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    public TextView tvTitle, tvContent, tvUserName, tvViewer, tvDate,tvPostCategory,tvStarCount;
    public ImageView ivPhoto, ivUserLogo, ivBookMark,ivStar;
    public LinearLayout header;
    public ProgressBar pgImage;
    public RelativeLayout userInfo;
    public CardView cardView;
    public PostViewHolder(View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.card_view);
        tvDate = itemView.findViewById(R.id.tvDate);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvContent = itemView.findViewById(R.id.tvContent);
        tvUserName = itemView.findViewById(R.id.tvUserName);
        tvViewer = itemView.findViewById(R.id.tvUserViewer);
        ivPhoto = itemView.findViewById(R.id.ivPhoto);
        ivUserLogo = itemView.findViewById(R.id.ivUserLogo);
        ivBookMark = itemView.findViewById(R.id.ivUserBookMark);
        header = itemView.findViewById(R.id.header_layout);
        pgImage = itemView.findViewById(R.id.imageProgress);
        userInfo = itemView.findViewById(R.id.userInfo);
        tvPostCategory = itemView.findViewById(R.id.tvPostCategory);
        tvStarCount = itemView.findViewById(R.id.tvPostStartCount);
        ivStar = itemView.findViewById(R.id.ivStar);
    }

}
