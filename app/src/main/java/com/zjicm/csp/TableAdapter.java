package com.zjicm.csp;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private ArrayList<Table> mTableList;
    private Activity mContext;
    private onPlayerClickListener mListener;

    public TableAdapter(ArrayList<Table> tableList, Activity context) {
        mTableList = tableList;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = View.inflate(mContext, R.layout.table_recycler_item, null);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.layout_p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPlayer1Click(viewHolder.getAdapterPosition(),
                        mTableList.get(viewHolder.getAdapterPosition()).isPlayer1Online());
            }
        });

        viewHolder.layout_p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPlayer2Click(viewHolder.getAdapterPosition(),
                        mTableList.get(viewHolder.getAdapterPosition()).isPlayer2Online());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Table table = mTableList.get(position);
        holder.mTableName.setText(table.getTableName());
        holder.mPlayer1.setImageResource(table.isPlayer1Online() ? R.drawable.online : R.drawable.wait);
        holder.mPlayer2.setImageResource(table.isPlayer2Online() ? R.drawable.online : R.drawable.wait);
        holder.mPlayer1IP.setText(table.getPlayer1IP());
        holder.mPlayer2IP.setText(table.getPlayer2IP());
    }

    @Override
    public int getItemCount() {
        return mTableList == null ? 0 : mTableList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mPlayer1;
        ImageView mPlayer2;
        TextView mPlayer1IP;
        TextView mPlayer2IP;
        TextView mTableName;
        LinearLayout layout_p1;
        LinearLayout layout_p2;
        View view;

        private ViewHolder(View view) {
            super(view);
            this.mPlayer1 = view.findViewById(R.id.player1);
            this.mPlayer2 = view.findViewById(R.id.player2);
            this.mPlayer1IP = view.findViewById(R.id.player1IP);
            this.mPlayer2IP = view.findViewById(R.id.player2IP);
            this.mTableName = view.findViewById(R.id.table_name);
            this.layout_p1 = view.findViewById(R.id.ll_p1);
            this.layout_p2 = view.findViewById(R.id.ll_p2);
            this.view = view;
        }
    }

    public interface onPlayerClickListener {
        void onPlayer1Click(int position, boolean isPositionAvailable);

        void onPlayer2Click(int position, boolean isPositionAvailable);
    }

    public void setOnPlayerClickListener(onPlayerClickListener listener) {
        mListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void update(Table newTable) {
        if (mTableList == null) {
            mTableList = new ArrayList<>();
        }
        if (newTable != null) {
            for (int i = 0; i < mTableList.size(); i++) {
                if (mTableList.get(i).getTableName().equals(newTable.getTableName())) {
                    mTableList.get(i).setTableName(newTable.getTableName());
                    mTableList.get(i).setPlayer1Online(newTable.isPlayer1Online());
                    mTableList.get(i).setPlayer2Online(newTable.isPlayer2Online());
                    mTableList.get(i).setPlayer1IP(newTable.getPlayer1IP());
                    mTableList.get(i).setPlayer2IP(newTable.getPlayer2IP());
                    notifyDataSetChanged();
                }
            }
        }
    }

    public void removeData(int position) {
        mTableList.remove(position);
        notifyItemRemoved(position);
    }

//    public void addData(int position) {
//        mDatas.add(position, "Insert " + position);
//        notifyItemInserted(position);
//    }
}
