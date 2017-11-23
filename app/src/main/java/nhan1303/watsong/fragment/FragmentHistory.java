package nhan1303.watsong.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import nhan1303.watsong.R;
import nhan1303.watsong.activity.InfoSongActivity;
import nhan1303.watsong.adapter.AdapterHistory;
import nhan1303.watsong.model.InfoSong;

/**
 * Created by NHAN on 13/11/2017.
 */

@SuppressLint("ValidFragment")
public class FragmentHistory extends Fragment {
    Context context;
    private ListView lvHistory;
    ArrayList<InfoSong> listSongSaved;
    AdapterHistory adapterHistory;

    @SuppressLint("ValidFragment")
    public FragmentHistory(Context context) {
        this.context = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initDisplay();
        initEvent();
    }

    private void initData() {
        listSongSaved = new ArrayList<>();
        adapterHistory = new AdapterHistory(context, R.layout.item_history_list_view, listSongSaved);
    }

    private void initControl(View view) {
        lvHistory = view.findViewById(R.id.lvHistory);
    }

    private void initDisplay() {
        adapterHistory.notifyDataSetChanged();
        lvHistory.setAdapter(adapterHistory);
    }

    private void initEvent() {
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(context, InfoSongActivity.class);
                intent.putExtra("SONG", listSongSaved.get(position));
                startActivity(intent);
            }
        });

        lvHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + listSongSaved.get(position).getTitle_track());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listSongSaved.remove(positionToRemove);
                        adapterHistory.notifyDataSetChanged();
                    }});
                adb.show();
                return false;
            }
        });
    }

    public void updateFragment(ArrayList<InfoSong> infoSongs) {
        listSongSaved = infoSongs;
        adapterHistory = new AdapterHistory(context, R.layout.item_history_list_view, listSongSaved);
        lvHistory.setAdapter(adapterHistory);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        initControl(view);
        return view;
    }
}
