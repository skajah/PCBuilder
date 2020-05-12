package com.company.PCBuilder.adapters;

import android.widget.BaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.PCBuilder.R;


public class AccountTabListAdapter extends BaseAdapter {
    Context context;
    String[] functions;
    int[] functionsPic;

    public AccountTabListAdapter(Context context, String[] functions, int[] functionsPic) {
        this.context = context;
        this.functions = functions;
        this.functionsPic = functionsPic;
    }

    @Override
    public int getCount() {
        return functions.length;
    }

    @Override
    public Object getItem(int position) {
        return functions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.account_tab_list_inflator, null);
        ImageView functionImageView = row.findViewById(R.id.accountRowImageView);
        TextView functionTextView = row.findViewById(R.id.accountRowTextView);

        String str = functions[position];
        functionTextView.setText(str);
        functionImageView.setImageResource(functionsPic[position]);

        return row;
    }

}