package myApp.lwttlzh.Taiwan_explore.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import myApp.lwttlzh.Taiwan_explore.R;

/**
 * Created by yiyiy on 2016/6/10.
 */
public class NoteFragment extends Fragment {

    public EditText inputText;
    public ListView listInput;
    public NoteDBHelper helper;
    public Cursor cursor;
    public SimpleCursorAdapter cursorAdapter;
    private List<String> option;
    private boolean isScrollFoot = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note,container,false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initDB();
        initView();
    }

    private void initDB(){
        helper = new NoteDBHelper(getActivity().getApplicationContext(),"note_database");
        cursor = helper.select();
        listInput = (ListView)getActivity().findViewById(R.id.listInputText);
        listInput.setDivider(null);
        cursorAdapter = new SimpleCursorAdapter(getActivity().getApplication(),
                R.layout.adapter, cursor,
                new String[]{"item_text"},
                new int[]{R.id.text},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
    }

    private void initView(){
        option = new ArrayList<>();
        option.add(getActivity().getApplicationContext().getString(R.string.modify));
        option.add(getString(R.string.delete));
        inputText = (EditText)getActivity().findViewById(R.id.inputText);
        listInput = (ListView)getActivity().findViewById(R.id.listInputText);
        listInput.setAdapter(cursorAdapter);

        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(!inputText.getText().toString().equals("")){
                    helper.insert(inputText.getText().toString());
                    cursor.requery();
                    cursorAdapter.notifyDataSetChanged();
                    inputText.setText("");
                }
                return true;
            }
        });

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
                                                        helper.update(cursor.getInt(0), editText.getText().toString());
                                                        cursor.requery();
                                                        cursorAdapter.notifyDataSetChanged();
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
                                                        helper.delete(cursor.getInt(0));

                                                        cursor.requery();
                                                        cursorAdapter.notifyDataSetChanged();
                                                    }
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

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "新增");
        menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "離開程式");
        return super.onCreateOptionsMenu(menu);
    }*/

 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removeall://add new item
                if(!inputText.getText().toString().equals("")){
                    helper.insert(inputText.getText().toString());
                    cursor.requery();
                    cursorAdapter.notifyDataSetChanged();
                    inputText.setText("");
                }
                break;
            case R.id.exit://exit app
                new AlertDialog.Builder(getActivity())
                        .setTitle("離開此程式")
                        .setMessage("你確定要離開？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/
}


