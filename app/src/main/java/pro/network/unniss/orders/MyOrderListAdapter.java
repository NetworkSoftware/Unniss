package pro.network.unniss.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pro.network.unniss.R;
import pro.network.unniss.app.AppConfig;


public class MyOrderListAdapter extends RecyclerView.Adapter<MyOrderListAdapter.MyViewHolder> {

    ReturnOnClick returnOnClick;
    private final Context mainActivityUser;
    private ArrayList<MyorderBean> myorderBean;

    public MyOrderListAdapter(Context mainActivityUser,
                              ArrayList<MyorderBean> myorderBean, ReturnOnClick returnOnClick) {
        this.mainActivityUser = mainActivityUser;
        this.myorderBean = myorderBean;
        this.returnOnClick = returnOnClick;
    }

    public void notifyData(ArrayList<MyorderBean> myorderBean) {
        this.myorderBean = myorderBean;
        notifyDataSetChanged();
    }

    public MyOrderListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myorders_list, parent, false);

        return new MyOrderListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final MyorderBean been = myorderBean.get(position);


        holder.amount.setText(been.getAmount());
        holder.quantity.setText(been.getQuantity());
        holder.status.setText(been.getStatus());
        holder.createdon.setText(AppConfig.convertTimeToLocal(been.getCreatedon()));
        holder.reason.setText(been.getReson());
        holder.orderid.setText(been.getId());
        holder.trackId.setText(been.getTrackId());
        holder.track_lay.setVisibility(View.VISIBLE);
        if(been.getTrackId().equalsIgnoreCase("0")){
            holder.track_lay.setVisibility(View.GONE);
        }
     /*   if(been.getWalletAmount().equalsIgnoreCase("")){
            holder.cashback.setVisibility(View.GONE);
            holder.cashbackTxt.setVisibility(View.GONE);
        }else {
            holder.cashback.setText(been.getWalletAmount());
        }*/
        holder.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnOnClick.onCartClick(been);
            }
        });


        if (been.getStatus().equalsIgnoreCase("Delivered")
                && !AppConfig.checkReturnExpired(been.createdon)) {

        } else {

        }

        MyOrderListProAdapter myOrderListAdapter = new MyOrderListProAdapter(mainActivityUser, been.getProductBeans());
        final LinearLayoutManager addManager1 = new LinearLayoutManager(mainActivityUser, LinearLayoutManager.HORIZONTAL, false);
        holder.cart_sub_list.setLayoutManager(addManager1);
        holder.cart_sub_list.setAdapter(myOrderListAdapter);


    }

    public int getItemCount() {
        return myorderBean.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderid;
        RecyclerView cart_sub_list;
        LinearLayout track_lay;
        CardView cart;
        private final TextView name;
        private final TextView quantity;
        private final TextView amount;
        private final TextView status;
        private final TextView createdon;
        private final TextView reason,trackId;

        public MyViewHolder(View view) {
            super((view));
            name = view.findViewById(R.id.name);
            quantity = view.findViewById(R.id.quantity);
            amount = view.findViewById(R.id.amount);
            status = view.findViewById(R.id.status);
            createdon = view.findViewById(R.id.createdon);
            cart_sub_list = view.findViewById(R.id.cart_sub_list);
            reason = view.findViewById(R.id.reason);
            orderid = view.findViewById(R.id.orderid);
            cart = view.findViewById(R.id.cart);
            trackId = view.findViewById(R.id.trackId);
            track_lay = view.findViewById(R.id.track_lay);


        }
    }

}


