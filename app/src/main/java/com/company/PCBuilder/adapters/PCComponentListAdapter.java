package com.company.PCBuilder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.company.PCBuilder.PCComponent;
import com.company.PCBuilder.R;
import static com.company.PCBuilder.dialogues.DetailDialogue.getImage;

import java.util.ArrayList;

public class PCComponentListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PCComponent> components;
    private boolean isSearchList = false;
    private boolean[] checkedArray;

    public PCComponentListAdapter(Context context, ArrayList<PCComponent> components, boolean isSearchList){
        this.context = context;
        this.components = components;
        this.isSearchList = isSearchList;
        this.checkedArray = new boolean[components.size()];
    }

    @Override

    public int getCount() {
        return components.size();
    }

    @Override
    public Object getItem(int position) {
        return components.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = convertView;
        ViewHolder holder = null;

        if(row==null){
            if (isSearchList)
                row = inflater.inflate(R.layout.search_list_row_layout, null);
            else
                row = inflater.inflate(R.layout.list_row_layout, null);

            holder = new ViewHolder();

            holder.checkBox = row.findViewById(R.id.checkbox);
            holder.imageView = row.findViewById(R.id.imageView);
            holder.nameTextView = row.findViewById(R.id.textView1);
            holder.priceTextView = row.findViewById(R.id.textView2);

            row.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

            if (holder.checkBox != null)
                holder.checkBox.setOnCheckedChangeListener(null);

        }

        if (holder.checkBox != null) {
            holder.checkBox.setFocusable(false);
            holder.checkBox.setChecked(checkedArray[position]);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            int positionFinal = position;
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                        checkedArray[positionFinal] = true;
                    else
                        checkedArray[positionFinal] = false;
                }
            });
        }

        PCComponent component = components.get(position);
        holder.nameTextView.setText(component.getName());
        holder.priceTextView.setText(String.valueOf(component.getPrice()));
        holder.imageView.setImageResource(getImage(component.getCategory()));

        return row;
    }

    static class ViewHolder {
        CheckBox checkBox;
        ImageView imageView;
        TextView nameTextView, priceTextView;
    }
}
