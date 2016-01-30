package ch.sintho.hfbuddy.View.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ch.sintho.hfbuddy.Data.Controller;
import ch.sintho.hfbuddy.Model.Subject;
import ch.sintho.hfbuddy.R;
import ch.sintho.hfbuddy.View.Adapters.MarksViewAdapter;
import ch.sintho.hfbuddy.View.Adapters.SubjectsViewAdapter;
import ch.sintho.hfbuddy.View.Adapters.SubjectsViewAdapter.MyClickListener;

/**
 * Created by Sintho on 09.01.2016.
 */
public class SubjectsViewContentFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static FragmentActivity mActivity;

    public static SubjectsViewContentFragment newInstance(FragmentActivity context){
        mActivity = context;
        return new SubjectsViewContentFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_subjectsview,container,false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.subjectsrecyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SubjectsViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        // Code to Add an item with default animation
        //((MarksViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MarksViewAdapter) mAdapter).deleteItem(index);

        ((SubjectsViewAdapter) mAdapter).setOnItemClickListener(new MyClickListener() {
            @Override
            public void onItemClick(Subject subject) {

                Bundle bundle = new Bundle();
                bundle.putInt("subjectid",subject.getId());
                MarksContentFragment fragment = new MarksContentFragment();
                fragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("SubjectsViewContentFragment").commit();
            }
        });

        ((SubjectsViewAdapter) mAdapter).setOnItemClickListener(new MyClickListener() {
            @Override
            public void onItemClick(Subject sub) {
                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Note löschen")
                        .setMessage("Wollen Sie diese Note wirklich löschen?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //((MarksViewAdapter) mAdapter).deleteItem(position);
                                Snackbar.make(getView(), "Note erfolgreich gelöscht!", Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });


        return v;
    }

    private ArrayList<Subject> getDataSet() {
        return Controller.GetInstance().getSubjectsFromDb(getActivity());
    }
}
