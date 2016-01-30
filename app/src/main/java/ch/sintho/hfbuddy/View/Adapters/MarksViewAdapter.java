package ch.sintho.hfbuddy.View.Adapters;

/**
 * Created by Sintho on 10.01.2016.
 */

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.sintho.hfbuddy.Data.DbContext;
import ch.sintho.hfbuddy.Model.Mark;
import ch.sintho.hfbuddy.Model.PartialMarkType;
import ch.sintho.hfbuddy.R;

public class MarksViewAdapter extends RecyclerView
        .Adapter<MarksViewAdapter
        .DataObjectHolder> {

    private static String LOG_TAG = "MarksViewAdapter";
    private ArrayList<Mark> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnLongClickListener {


        TextView mark;
        TextView teilnote;
        TextView date;


        public DataObjectHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.txtDate);
            teilnote = (TextView) itemView.findViewById(R.id.txtTeilnote);
            mark = (TextView) itemView.findViewById(R.id.txtMark);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
            return false;
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MarksViewAdapter(ArrayList<Mark> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marks_cardviewrow, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);

        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        holder.mark.setText(mDataset.get(position).getMarkFormatted());
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Date date = mDataset.get(position).getDate();
        holder.date.setText(df.format(date));
        PartialMarkType teilnote = mDataset.get(position).getPartialmarktype();

        if (teilnote != null)
            holder.teilnote.setText(teilnote.getName());
        else
            holder.teilnote.setText("");

    }

    public void addItem(Mark dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        DbContext.getInstance().executeQuery("DELETE FROM " + DbContext.TABLE_MARKS + " WHERE " + DbContext.COLUMN_ID + " = " + mDataset.get(index).getId(), Mark.class);
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        if (mDataset == null)
            return 0;

        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}