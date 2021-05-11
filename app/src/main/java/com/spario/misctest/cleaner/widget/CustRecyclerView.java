package com.spario.misctest.cleaner.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CustRecyclerView extends RecyclerView {
    private View mEmptyView;

    private final CustRecyclerView.AdapterDataObserver observer =
            new CustRecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    checkIfEmpty();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    checkIfEmpty();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    checkIfEmpty();
                }
            };

    public CustRecyclerView(Context context) {
        super(context);
    }

    public CustRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void checkIfEmpty() {
        if (mEmptyView != null && getAdapter() != null) {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;

            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);

            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(CustRecyclerView.Adapter adapter) {
        final CustRecyclerView.Adapter oldAdapter = getAdapter();

        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }

        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;

        checkIfEmpty();
    }
}
