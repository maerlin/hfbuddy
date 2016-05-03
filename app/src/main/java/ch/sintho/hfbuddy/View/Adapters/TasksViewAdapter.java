package ch.sintho.hfbuddy.View.Adapters;

/**
 * Created by Sintho on 10.01.2016.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ch.sintho.hfbuddy.Data.DbContext;
import ch.sintho.hfbuddy.Helpers.DateHelper;
import ch.sintho.hfbuddy.Helpers.MediaHelper;
import ch.sintho.hfbuddy.Model.Mark;
import ch.sintho.hfbuddy.Model.PartialMarkType;
import ch.sintho.hfbuddy.Model.Subject;
import ch.sintho.hfbuddy.Model.Task;
import ch.sintho.hfbuddy.R;

public class TasksViewAdapter extends RecyclerView
        .Adapter<TasksViewAdapter
        .DataObjectHolder> {

    private static String LOG_TAG = "TasksViewAdapter";
    private static ArrayList<Task> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener, View.OnLongClickListener {

        TextView title;
        TextView subject;
        TextView date;

        public DataObjectHolder(View itemView) {
            super(itemView);

            subject = (TextView) itemView.findViewById(R.id.txtSubjectOnTaskViewRow);
            title = (TextView) itemView.findViewById(R.id.txtTitleOnTaskViewRow);
            date = (TextView) itemView.findViewById(R.id.txtDatumOnTaskViewRow);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(TasksViewAdapter.mDataset.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            myClickListener.onItemLongClick(TasksViewAdapter.mDataset.get(getAdapterPosition()));
            return true;
        }
    }
    public void setOnItemClickListener(MyClickListener myClickListener) {
        TasksViewAdapter.myClickListener = myClickListener;
    }

    public TasksViewAdapter(ArrayList<Task> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tasks_taskviewrow, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        Task task = mDataset.get(position);

        holder.subject.setText(task.getSubject() != null ? task.getSubject().getName() : "");
        holder.title.setText(task.getTitle());

        Date date = task.getDate();

        if (Calendar.getInstance().getTime().after(date)) {
            holder.title.setTextColor(0xFFc40000);
        }
        else{
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 3);
            if (cal.getTime().after(date))
            {
                holder.title.setTextColor(0xFFF06D2F);
            }
            else
            {
                holder.title.setTextColor(0xFF249e00);
            }
        }

        holder.date.setText(DateHelper.getReadableDatestring(date));
    }

    public void addItem(Task dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(Task task) {
        int index = mDataset.indexOf(task);

        String picpath = DbContext.getInstance().getStringColumnEntry("SELECT " + DbContext.COLUMN_IMAGE + " FROM " + DbContext.TABLE_TASKS +" WHERE " + DbContext.COLUMN_ID + " = " + task.getId());
        String thumbpath = DbContext.getInstance().getStringColumnEntry("SELECT " + DbContext.COLUMN_THUMBNAIL + " FROM " + DbContext.TABLE_TASKS +" WHERE " + DbContext.COLUMN_ID + " = " + task.getId());

        MediaHelper.deleteFile(picpath);
        MediaHelper.deleteFile(thumbpath);

        DbContext.getInstance().executeQuery("DELETE FROM " + DbContext.TABLE_TASKS + " WHERE " + DbContext.COLUMN_ID + " = " + task.getId(), Task.class);
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
        void onItemClick(Task task);
        void onItemLongClick(Task task);
    }
}