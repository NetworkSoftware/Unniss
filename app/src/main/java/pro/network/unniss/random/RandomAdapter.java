package pro.network.unniss.random;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Random;

import pro.network.unniss.R;
import pro.network.unniss.app.AppConfig;

public class RandomAdapter extends RecyclerView.Adapter<RandomAdapter.MyViewHolder> {


    private final Context context;
    private final OnRandom onBlock;
    private ArrayList<RandomBeen> blogArrayList;
    private String selectedPosition;

    public RandomAdapter(Context context, ArrayList<RandomBeen> blogArrayList, OnRandom onBlock, String selectedPosition) {
        this.blogArrayList = blogArrayList;
        this.context = context;
        this.onBlock = onBlock;
        this.selectedPosition = selectedPosition;
    }

    public void notifyData(ArrayList<RandomBeen> myList) {
        this.blogArrayList = myList;
        notifyDataSetChanged();
    }

    public void notifyData(String selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public RandomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_aa, parent, false);
        return new RandomAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RandomAdapter.MyViewHolder holder, final int position) {
        final RandomBeen bean = blogArrayList.get(position);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBlock.onItemClick(bean.text);
            }
        });
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions().override(50, 50))
                .load(AppConfig.getResizedImage(bean.image, true))
                .into(holder.image);
        holder.title.setText(bean.text);

/*        java.util.Random rnd = new java.util.Random();
        int currentColor = Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
        holder.card.setBackgroundColor(currentColor);*/

     /*   int color_arr[]=  {R.color.colorpdf,R.color.randomone,R.color.randomtwo,R.color.randomthree,R.color.randomfour,R.color.randomfive,};
        int rnd = new Random().nextInt(color_arr.length);
        holder.card.setBackgroundResource(color_arr[rnd]);*/

    }

    @Override
    public int getItemCount() {
        return blogArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        RelativeLayout card;

        public MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            title = view.findViewById(R.id.text);

        }
    }
}

