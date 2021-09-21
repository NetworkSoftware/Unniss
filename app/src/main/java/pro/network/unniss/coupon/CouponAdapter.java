package pro.network.unniss.coupon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pro.network.unniss.R;


public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.MyViewHolder> {


    private OnCoupon onCoupon;
    private ArrayList<Coupon> couponList;

    public CouponAdapter(ArrayList<Coupon> coupons, OnCoupon onCoupon) {
        this.couponList = coupons;
        this.onCoupon = onCoupon;
    }

    public void notifyData(ArrayList<Coupon> myList) {
        this.couponList = myList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coupon_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Coupon bean = couponList.get(position);
        holder.title.setText(bean.coupon);
        holder.description.setText(bean.description);
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCoupon.onCouponSelected(position);
            }
        });
        holder.linear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onCoupon.onCouponLongClick(position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        RelativeLayout linear;

        public MyViewHolder(View view) {
            super(view);
            description = view.findViewById(R.id.description);
            title = view.findViewById(R.id.title);
            linear = view.findViewById(R.id.linear);

        }
    }
}
