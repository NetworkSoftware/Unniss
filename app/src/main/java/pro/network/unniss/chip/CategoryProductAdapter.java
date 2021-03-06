package pro.network.unniss.chip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import pro.network.unniss.R;
import pro.network.unniss.app.AppConfig;

public class CategoryProductAdapter extends RecyclerView.Adapter<CategoryProductAdapter.MyViewHolder> {


    private final Context context;
    private final OnChip onBlock;
    private ArrayList<ChipBean> blogArrayList;
    private String selectedPosition;


    public CategoryProductAdapter(Context context, ArrayList<ChipBean> blogArrayList, OnChip onBlock, String selectedPosition) {
        this.blogArrayList = blogArrayList;
        this.context = context;
        this.onBlock = onBlock;
        this.selectedPosition = selectedPosition;
    }


    public void notifyData(ArrayList<ChipBean> myList) {
        this.blogArrayList = myList;
        notifyDataSetChanged();
    }

    public void notifyData(String selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public CategoryProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryProductAdapter.MyViewHolder holder, final int position) {
        final ChipBean bean = blogArrayList.get(position);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBlock.onItemClick(bean.chip);
            }
        });
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions().override(50,50))
                .load(AppConfig.getResizedImage(bean.image, true))
                .into(holder.image);
        holder.title.setText(bean.chip);
    }

    @Override
    public int getItemCount() {
        return blogArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        public MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            title = view.findViewById(R.id.title);

        }
    }
}
