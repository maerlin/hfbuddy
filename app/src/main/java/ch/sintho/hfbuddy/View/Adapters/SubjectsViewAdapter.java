package ch.sintho.hfbuddy.View.Adapters;

/**
 * Created by Sintho on 10.01.2016.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.sintho.hfbuddy.Data.DbContext;
import ch.sintho.hfbuddy.Model.Mark;
import ch.sintho.hfbuddy.Model.PartialMarkType;
import ch.sintho.hfbuddy.Model.Subject;
import ch.sintho.hfbuddy.R;

public class SubjectsViewAdapter extends RecyclerView
        .Adapter<SubjectsViewAdapter
        .DataObjectHolder> {

    private static String LOG_TAG = "SubjectsViewAdapter";
    private static ArrayList<Subject> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener, View.OnLongClickListener {

        TextView subject;
        TextView durschnitt;
        TextView anzahl;

        public DataObjectHolder(View itemView) {
            super(itemView);

            subject = (TextView) itemView.findViewById(R.id.txtSubjectOnSubjects_CardViewRow);
            anzahl = (TextView) itemView.findViewById(R.id.txtAnzahlNotenOnSubjects_CardViewRow);
            durschnitt = (TextView) itemView.findViewById(R.id.txtDurchschnittOnSubjects_CardViewRow);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(SubjectsViewAdapter.mDataset.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            myClickListener.onItemLongClick(SubjectsViewAdapter.mDataset.get(getAdapterPosition()));
            return true;
        }
    }
    public void setOnItemClickListener(MyClickListener myClickListener) {
        SubjectsViewAdapter.myClickListener = myClickListener;
    }

    public SubjectsViewAdapter(ArrayList<Subject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subjects_cardviewrow, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        Subject sub = mDataset.get(position);

        ArrayList<Mark> marks = DbContext.getInstance().executeQuery("SELECT * FROM " + DbContext.TABLE_MARKS + " WHERE " + DbContext.COLUMN_SUBJECT_FK + " = " + sub.getId(), Mark.class);

        double durchschnitt = 0.00;
        int count = 0;

        if (marks != null && marks.size() > 0)
        {
            HashMap<Integer, List<Double>> map = new HashMap<>();

            for (Mark mark:marks) {
                PartialMarkType type = mark.getPartialmarktype();

                int typeid = 0;
                if (type != null) {
                    typeid = type.getId();
                }

                if (map.containsKey(typeid)) {
                    List<Double> list = map.get(typeid);
                    list.add(mark.getMark()/1000);
                    map.put(typeid, list);
                } else {
                    List<Double> list = new ArrayList<>();
                    list.add(mark.getMark()/1000);
                    map.put(typeid, list);
                }

                count++;
            }

            for (List<Double> lst: map.values()) {
                double internalds = 0.00;
                for (int i = 0; i < lst.size() ; i++) {
                    internalds += lst.get(i);
                }
                durchschnitt += (internalds / lst.size());
            }
            if (map.values().size() != 0) {
                durchschnitt = (durchschnitt / map.values().size());
            }
        }


        holder.subject.setText(sub.getName());
        holder.anzahl.setText(count + " Note(n)");
        holder.durschnitt.setText("ø " + Mark.formatMark(durchschnitt));
    }

    public void addItem(Subject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        DbContext.getInstance().executeQuery("DELETE FROM " + DbContext.TABLE_MARKS + " WHERE " + DbContext.COLUMN_SUBJECT_FK + " = " + mDataset.get(index).getId(), Mark.class);
        DbContext.getInstance().executeQuery("DELETE FROM " + DbContext.TABLE_SUBJECTS + " WHERE " + DbContext.COLUMN_ID + " = " + mDataset.get(index).getId(), Subject.class);
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
        void onItemClick(Subject sub);
        void onItemLongClick(Subject sub);
    }
}