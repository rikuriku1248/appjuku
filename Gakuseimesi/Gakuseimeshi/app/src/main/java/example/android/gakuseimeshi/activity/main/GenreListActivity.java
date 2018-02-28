package example.android.gakuseimeshi.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import example.android.gakuseimeshi.R;


public class GenreListActivity extends AppCompatActivity {
    private Intent intent;
    private static final String genreListItems[] = {"未選択", "和食", "中華", "フレンチ", "イタリアン", "麺", "ファストフード", "洋食", "喫茶店", "スイーツ", "居酒屋", "揚げ物", "肉", "丼物", "魚", "パン", "定食", "その他"};
    private static final String childItem[][] = {
            {},
            {"おでん", "懐石料理", "天ぷら","和食で検索"},
            {"餃子", "麻婆豆腐", "中華で検索"},
            {"フレンチで検索"},
            {"パスタ", "ピザ", "イタリアンで検索"},
            {"うどん", "そば", "ラーメン", "冷麺"},
            {"ハンバーガー", "ファミリーレストラン","ファストフードで検索"},
            {"オムライス", "オムレツ","ステーキ", "ハンバーガー", "ハンバーグ","洋食で検索"},
            {"カフェ", "喫茶店で検索"},
            {"クレープ", "ケーキ"},
            {"おでん", "串カツ", "バー", "居酒屋で検索"},
            {"串カツ", "とんかつ", "揚げ物で検索"},
            {"しゃぶしゃぶ", "ハンバーガー", "ハンバーグ", "焼き鳥", "焼肉", },
            {"牛丼", "天丼"},
            {"シーフード", "寿司"},
            {},
            {},
            {"台湾料理", "郷土料理", "サムギョプサル", "ベトナム料理", "スペインバル", "創作料理", "おばんざい"}};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_list);
        ExpandableListView genreList = (ExpandableListView)findViewById(R.id.genre_list);

        List<Map<String, String>> parentList = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> allChildList = new ArrayList<List<Map<String, String>>>();
        int i = 0;

        //親
        for(String str : genreListItems) {
            Map<String, String> parentData = new HashMap<String, String>();
            parentData.put("genre", str);
            parentList.add(parentData);
            i++;
        }

        i = 0;

        //子
        for(String str[] : childItem){
            List<Map<String, String>> childList = new ArrayList<Map<String, String>>();
            for(String item : str){
                Map<String, String> childData = new HashMap<String, String>();
                childData.put("GENRE", item);
                childList.add(childData);
            }
            allChildList.add(childList);
            i++;
        }

        i = 0;

        //アダプター
        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this, parentList, android.R.layout.simple_expandable_list_item_1,
                new String []{"genre"}, new int[] {android.R.id.text1},
                allChildList, android.R.layout.simple_expandable_list_item_2,
                new String []{"GENRE"}, new int [] {android.R.id.text1});

        genreList.setAdapter(adapter);

        //Log.d("onCreate", "inf:");

        genreList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view,
                                        int groupPosition, int childPosition, long id){
                ExpandableListAdapter adapter = parent.getExpandableListAdapter();
                Map<String, String> item = (Map<String, String>) adapter.getChild(groupPosition, childPosition);
                //Toast.makeText(getApplicationContext(), "child clicked " + item.get("GENRE"), Toast.LENGTH_LONG).show();
                intent = new Intent(GenreListActivity.this, MainActivity.class);
                intent.putExtra("GenreItem", item.get("GENRE"));
                intent.putExtra("GENRE", true);
                setResult(RESULT_OK, intent);
                finish();
                return false;
            }
        });

        genreList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id){
                ExpandableListAdapter adapter = parent.getExpandableListAdapter();
                Map<String, String> item = (Map<String,String>) adapter.getGroup(groupPosition);
                if(adapter.getChildrenCount(groupPosition) == 0){
                    intent = new Intent(GenreListActivity.this, MainActivity.class);

                    if(item.get("genre").equals("未選択")) intent.putExtra("GenreItem", "");
                    else intent.putExtra("GenreItem", item.get("genre"));

                    intent.putExtra("GENRE", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                //Toast.makeText(getApplicationContext(), "parent clicked " + item.get("genre"), Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }
}