package com.example.kmapp.TriviaCategoriesManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.kmapp.R;

import java.util.ArrayList;


public class CategoriesAdapter extends ArrayAdapter<Categories> {

    private final ArrayList<Categories> list;
    private final Context context;

    public CategoriesAdapter(Context context, ArrayList<Categories> list) {
        super(context, R.layout.row, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.row, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.textviewCB);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setChecked(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.textviewCB, viewHolder.text);
            convertView.setTag(R.id.checkbox, viewHolder.checkbox);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.checkbox.setTag(position); // This line is important.

        viewHolder.text.setText(list.get(position).getText());
        viewHolder.checkbox.setChecked(list.get(position).isChecked);

        return convertView;
    }
}