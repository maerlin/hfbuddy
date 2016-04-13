package ch.sintho.hfbuddy.View.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import ch.sintho.hfbuddy.Model.Task;
import ch.sintho.hfbuddy.R;
import ch.sintho.hfbuddy.View.Adapters.TasksViewAdapter;

/**
 * Created by Sintho on 09.01.2016.
 */
public class TasksViewContentFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static FragmentActivity mActivity;

    public static TasksViewContentFragment newInstance(FragmentActivity context){
        mActivity = context;
        return new TasksViewContentFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_tasksview,container,false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.tasksrecyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TasksViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fabOnTasksView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTaskFragment fragment = new NewTaskFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("TasksViewContentFragment");
                fragmentTransaction.commit();
            }
        });

        ((TasksViewAdapter) mAdapter).setOnItemClickListener(new TasksViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(Task task) {
                NewTaskFragment fragment = new NewTaskFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                //Bundle bundle = new Bundle();
                //bundle.putInt("subjectid", mSubjectId);
                //fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("TasksViewContentFragment");
                fragmentTransaction.commit();
            }

            @Override
            public void onItemLongClick(final Task task) {
                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Aufgabe löschen?")
                        .setMessage("Wollen Sie diese Aufgabe wirklich löschen?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((TasksViewAdapter) mAdapter).deleteItem(task);
                                Snackbar.make(getView(), "Aufgabe erfolgreich gelöscht!", Snackbar.LENGTH_SHORT).show();
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

    private ArrayList<Task> getDataSet() {
        return Controller.GetInstance().getTasksFromDb(getActivity());
    }
}
