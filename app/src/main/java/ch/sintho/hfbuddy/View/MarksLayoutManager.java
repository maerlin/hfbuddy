package ch.sintho.hfbuddy.View;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.EventListener;

/**
 * Created by Sintho on 31.01.2016.
 */
public class MarksLayoutManager extends LinearLayoutManager {

    private ItemRemovedNotificator notificator;

    public MarksLayoutManager(Context context) {
        super(context);
    }

    public void setItemsRemovedListener(ItemRemovedNotificator notîficator) {
        notificator = notîficator;
    }

    @Override
    public void onItemsRemoved(RecyclerView view, int start, int end) {
        super.onItemsRemoved(view, start,end);
        if (notificator != null)
            notificator.onItemsRemoved();
    }
}


