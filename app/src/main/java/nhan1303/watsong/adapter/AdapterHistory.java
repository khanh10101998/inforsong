package nhan1303.watsong.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import nhan1303.watsong.R;
import nhan1303.watsong.model.InfoSong;

/**
 * Created by NHAN on 13/11/2017.
 */

public class AdapterHistory extends ArrayAdapter {
    Context context;
    int resource;
    List<InfoSong> objects;
    public AdapterHistory(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_history_list_view, null);
            viewHolder = new ViewHolder();
            viewHolder.img_item_history = convertView.findViewById(R.id.img_item_history);
            viewHolder.tv_title_item_history = convertView.findViewById(R.id.tv_title_item_history);
            viewHolder.tv_artist_item_history = convertView.findViewById(R.id.tv_artist_item_history);
            viewHolder.tv_date_time_item_history = convertView.findViewById(R.id.tv_date_time_item_history);
            viewHolder.tv_divider = convertView.findViewById(R.id.tv_divider);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        InfoSong info = objects.get(position);
        viewHolder.tv_title_item_history.setText(info.getTitle_track());
        viewHolder.tv_artist_item_history.setText(info.getArtist_name());
        viewHolder.tv_date_time_item_history.setText(info.getCurrentTime());
        if(info.getUrlImage().equals("")){
            Picasso.with(context).load(R.drawable.ic_place_holder).into(viewHolder.img_item_history);
        }else{
            Picasso.with(context).load(info.getUrlImage()).into(viewHolder.img_item_history);
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView img_item_history;
        TextView tv_title_item_history;
        TextView tv_artist_item_history;
        TextView tv_date_time_item_history;
        TextView tv_divider;
    }
}
