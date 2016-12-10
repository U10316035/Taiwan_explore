package myApp.lwttlzh.Taiwan_explore.Fragments;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import myApp.lwttlzh.Taiwan_explore.R;

public class FlagFragment extends Fragment {

    //private EditText inputText;
    public ListView listInput;
    public NoteDBHelper fhelper;
    public Cursor cursor;
    public SimpleCursorAdapter cursorAdapter;
    private List<String> option;
    private boolean isScrollFoot = true;
    private SQLiteDatabase coord;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flag,container,false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initFlagDB();
        initView();
    }

    private void initFlagDB(){
        fhelper = new NoteDBHelper(getActivity().getApplicationContext(),"flag_database");

        cursor = fhelper.select();
        listInput = (ListView)getActivity().findViewById(R.id.listInputText);
        listInput.setDivider(null);
        cursorAdapter = new SimpleCursorAdapter(getActivity().getApplication(),
                R.layout.flagadapter, cursor,
                new String[]{"item_text"},
                new int[]{R.id.text},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );

       /* cursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                SpannableStringBuilder style = new SpannableStringBuilder("1234567891");

                style.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(new ForegroundColorSpan(Color.RED), 7, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((TextView) view).setText(style);
                return false;
            }
        });*/
    }

    private void initView(){
        option = new ArrayList<>();
        option.add(getActivity().getApplicationContext().getString(R.string.modify));
        option.add(getString(R.string.delete));
       // inputText = (EditText)getActivity().findViewById(R.id.inputText);
        listInput = (ListView)getActivity().findViewById(R.id.listInputText);
        listInput.setAdapter(cursorAdapter);




        listInput .setOnScrollListener( new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    //最底
                    isScrollFoot = true;
                } else {
                    isScrollFoot = false;
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isScrollFoot ) { // 滾動靜止且滾動到最底部
                    //停在最底部
                    Toast. makeText(getActivity(), "繼續探險吧", Toast. LENGTH_SHORT).show();
                } else {

                    // 不是停在最底部
                }
            }
        });

        listInput.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
                //Toast.makeText(getActivity(),position+"", Toast.LENGTH_LONG).show();
                //Toast.makeText(getActivity(),cursor.getPosition()+"", Toast.LENGTH_LONG).show();
                final int pos = position;
                cursor.moveToPosition(1);

                new AlertDialog.Builder(getActivity())
                        .setItems(option.toArray(new String[option.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0://modify
                                        final View item = LayoutInflater.from(getActivity()).inflate(R.layout.item_layout, null);
                                        final EditText editText = (EditText) item.findViewById(R.id.edittext);
                                        editText.setText(cursor.getString(1));
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("修改內容")
                                                .setView(item)
                                                .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        fhelper.update(cursor.getInt(0), editText.getText().toString());
                                                        cursor.requery();
                                                        cursorAdapter.notifyDataSetChanged();

                                                        Cursor Cur = coord.rawQuery("SELECT * FROM table01",null);

                                                            //Toast.makeText(this,i+"", Toast.LENGTH_LONG).show();

                                                        Cur.moveToPosition(pos);
                                                        String[] AfterSplit = editText.getText().toString().split("\n");
                                                        ContentValues values = new ContentValues();
                                                        values.put("tit",AfterSplit[0]);
                                                        values.put("txt",AfterSplit[1]);
                                                        coord.update("table01",values,"_id="+Cur.getInt(0),null);




                                                    }
                                                })
                                                .show();
                                        break;
                                    case 1://delete
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("刪除列")
                                                .setMessage("你確定要刪除？")
                                                .setPositiveButton("是", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        fhelper.delete(cursor.getInt(0));
                                                        cursor.requery();
                                                        cursorAdapter.notifyDataSetChanged();

                                                        Cursor Cur = coord.rawQuery("SELECT * FROM table01",null);

                                                            //Toast.makeText(this,i+"", Toast.LENGTH_LONG).show();

                                                                Cur.moveToPosition(pos);
                                                                //Toast.makeText(getActivity(),i+"", Toast.LENGTH_LONG).show();
                                                               // String p = Integer.toString(pos);
                                                               // Toast.makeText(getActivity(),p, Toast.LENGTH_LONG).show();
                                                                coord.delete("table01","_id="+Cur.getInt(0),null);



                                                        }

                                                      //  Toast.makeText(getActivity(),cursor.getColumnCount(), Toast.LENGTH_LONG).show();


                                                })
                                                .setNegativeButton("否", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .show();
                                        break;
                                }

                            }
                        }).show();
                return false;
            }

        });
    }

    public void setFlagSQL(SQLiteDatabase coord){
        this.coord = coord;
    }

}
