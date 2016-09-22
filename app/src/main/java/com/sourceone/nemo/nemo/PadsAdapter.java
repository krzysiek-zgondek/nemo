package com.sourceone.nemo.nemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.sourceone.nemo.nemo.sgine.track.Track;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SourceOne on 12.09.2016.
 */
public class PadsAdapter extends BaseAdapter {
    private Context context;
    private OnPadHitListener onPadHitListener;

    public interface OnPadHitListener{
        void onPadHit(int position, Holder holder);
    }

    public PadsAdapter(Context context, OnPadHitListener onPadHitListener){
        this.context = context;
        this.onPadHitListener = onPadHitListener;
    }

    @Override
    public int getCount() {
        return 16;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = newView(position, parent);

            Holder holder = new Holder();
            ButterKnife.bind(holder, convertView);

            convertView.setTag(holder);
        }

        Holder holder = (Holder) convertView.getTag();
        bindView(position, holder);

        return convertView;
    }

    private void bindView(final int position, final Holder holder) {
        holder.pad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    onPadHitListener.onPadHit(position, holder);
                    return true;
                }
                return false;
            }
        });
        holder.name.setText("Pad"+position);
    }

    private View newView(int position, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.pad, parent, false);
    }

    public class Holder {
        @BindView(R.id.pad) View pad;
        @BindView(R.id.name) TextView name;
    }
}
