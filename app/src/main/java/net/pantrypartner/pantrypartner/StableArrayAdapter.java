/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.pantrypartner.pantrypartner;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

//TODO Changed everything from String to E
public class StableArrayAdapter<E> extends ArrayAdapter<E> {

    final int INVALID_ID = -1;

    HashMap<E, Integer> mIdMap = new HashMap<E, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId, List<E> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        E item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private class ViewHolder {
        //ImageView picture;
        TextView title;
        TextView exp;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;
        GroceryItem item = (GroceryItem) getItem(position);

        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.groceryTitle);
            holder.exp = (TextView) convertView.findViewById(R.id.groceryExp);
            //holder.picture = (ImageView) convertView.findViewById(R.id.groceryImage);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.title.setText(item.get_title());
        holder.exp.setText(item.expToString());
        //holder.picture.setImageDrawable(item.get_appIcon());

        return convertView;
    }



}
