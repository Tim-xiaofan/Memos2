package com.example.zhong.memo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhong.memo.Services.ScheduleService;
import com.example.zhong.memo.db.Memo;
import com.example.zhong.memo.db.MemoGroup;
import com.example.zhong.memo.db.MemoManager;
import com.example.zhong.memo.ui.MemoAdapter;
import com.example.zhong.memo.db.GroupManager;
import com.example.zhong.memo.ui.SelectingTitleLayout;

import org.litepal.LitePal;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG="MainActivity";

    private MemoAdapter adapter;

    private DrawerLayout mDrawerLayout;

    private GroupManager groupManager;

    private boolean isUnderDeleting;

    private boolean isAllSelected;

    private Toolbar toolbar;

    private boolean isPressedBefore;

    RecyclerView recyclerView;

    String returnedData;

    private boolean isUnderGroupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MemoActivity.class);
                startActivity(intent);
            }
        });
        //加载主页面RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MemoAdapter();
        recyclerView.setAdapter(adapter);
        isPressedBefore = false;
        //导航栏menu点击事件,分组浏览
        NavigationView navView =  findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //分组加载
                String menuTitle = menuItem.getTitle()+"";
                toolbar.setTitle(menuTitle);
                Toast.makeText(MainActivity.this,"You clicked " +menuTitle,
                        Toast.LENGTH_SHORT).show();
                recyclerView = findViewById(R.id.recycler_view);
                GridLayoutManager layoutManager = new
                        GridLayoutManager(MainActivity.this,1);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new MemoAdapter(menuTitle);
                recyclerView.setAdapter(adapter);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        //加载导航栏菜单
        NavigationView navigationView = findViewById(R.id.nav_view);
        groupManager = new GroupManager(navigationView);
        groupManager.refreshMenu();
        isUnderDeleting = false;
        isAllSelected = false;
        //"取消"的点击事件,退出删除状态
        TextView cancelText = findViewById(R.id.cancel_text);
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingActionButton fab = findViewById(R.id.fab);
                Toolbar toolbar = findViewById(R.id.toolbar);
                SelectingTitleLayout selectingTitleLayout = findViewById(R.id.title_selecting_layout);
                fab.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                selectingTitleLayout.setVisibility(View.GONE);
                recyclerView = findViewById(R.id.recycler_view);
                GridLayoutManager layoutManager = new
                        GridLayoutManager(MainActivity.this,1);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new MemoAdapter();
                adapter.isUnderSelecting(false);
                String title = toolbar.getTitle()+"";
                if(title.equals("便签"))title = "全部";
                //adapter = new MemoAdapter(title);
                recyclerView.setAdapter(adapter);
                isUnderDeleting = false;
            }
        });
        //全选的点击事件
        TextView allText = findViewById(R.id.all_text);
        allText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //全选
                if(!isAllSelected){
                    adapter.isNeededAllSelected(true);
                    adapter.notifyDataSetChanged();
                    isAllSelected = true;
                }
                //全不选
                else{
                    adapter.isNeededAllUnSelected(true);
                    adapter.notifyDataSetChanged();
                    isAllSelected = false;
                }
            }
        });
        //删除的点击事件
        ImageView deleteImage = findViewById(R.id.delete_image);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage("删除"+adapter.getCountSelectedItem()+"条便签");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MemoManager memoManager = new MemoManager(adapter.getSelectedMemoList());
                        memoManager.deleteMemoList(MainActivity.this);
                        FloatingActionButton fab = findViewById(R.id.fab);
                        Toolbar toolbar = findViewById(R.id.toolbar);
                        SelectingTitleLayout selectingTitleLayout = findViewById(R.id.title_selecting_layout);
                        fab.setVisibility(View.VISIBLE);
                        toolbar.setVisibility(View.VISIBLE);
                        selectingTitleLayout.setVisibility(View.GONE);
                        RecyclerView recyclerView = findViewById(R.id.recycler_view);
                        GridLayoutManager layoutManager = new
                                GridLayoutManager(MainActivity.this,1);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new MemoAdapter();
                        adapter.isUnderSelecting(false);
                        recyclerView.setAdapter(adapter);
                        isUnderDeleting = false;
                        NavigationView navigationView = mDrawerLayout.findViewById(R.id.nav_view);
                        GroupManager groupManager = new GroupManager(navigationView);
                        groupManager.refreshMenu();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
    }
    //返还键建重写，关闭导航栏，退出选状态
    @Override
    public void onBackPressed() {
        LinearLayout linearLayout = findViewById(R.id.left_drawer);
        if (linearLayout.getVisibility() == View.VISIBLE)
            mDrawerLayout.closeDrawers();
        else if(isUnderDeleting){
            FloatingActionButton fab = findViewById(R.id.fab);
            Toolbar toolbar = findViewById(R.id.toolbar);
            SelectingTitleLayout selectingTitleLayout = findViewById(R.id.title_selecting_layout);
            fab.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            selectingTitleLayout.setVisibility(View.GONE);
            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            GridLayoutManager layoutManager = new
                    GridLayoutManager(this,1);
            recyclerView.setLayoutManager(layoutManager);
            String title = toolbar.getTitle()+"";
            if(title.equals("便签"))title = "Call";
            //adapter = new MemoAdapter(title);
            adapter.isUnderSelecting(false);
            recyclerView.setAdapter(adapter);
            isUnderDeleting = false;
        } else{
            if(!isPressedBefore){
                Toast.makeText(MainActivity.this,"再按一次退出",Toast.LENGTH_SHORT).show();
                isPressedBefore = true;
            }else {
                super.onBackPressed();
            }
        }
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.delete:{
                FloatingActionButton fab = findViewById(R.id.fab);
                Toolbar toolbar = findViewById(R.id.toolbar);
                SelectingTitleLayout selectingTitleLayout = findViewById(R.id.title_selecting_layout);
                if(!isUnderDeleting){//进入删除状态
                    //切换布局
                    fab.setVisibility(View.GONE);
                    toolbar.setVisibility(View.GONE);
                    selectingTitleLayout.setVisibility(View.VISIBLE);
                    RecyclerView recyclerView = findViewById(R.id.recycler_view);
                    GridLayoutManager layoutManager = new
                            GridLayoutManager(this,1);
                    recyclerView.setLayoutManager(layoutManager);
                    String title = toolbar.getTitle()+"";
                    if(title.equals("便签"))title = "Call";
                    //adapter = new MemoAdapter(title);
                    adapter.isUnderSelecting(true);
                    recyclerView.setAdapter(adapter);
                    isUnderDeleting = true;
                }
            }
                break;
            case R.id.setting:
                Intent startIntent = new Intent(MainActivity.this, ScheduleService.class);
                startService(startIntent);
                break;
            default:break;
        }
        return true;
    }
    @Override
    protected void onStart(){
        super.onStart();
        LitePal.getDatabase();
        Log.d(TAG,"onStart");
        MemoManager memoManager = new MemoManager();
        List<MemoGroup> memoGroupList = memoManager.getGroupList();
        for (MemoGroup memoGroup : memoGroupList){
            Log.d(TAG, "onStart: memoGroup name is "+ memoGroup.getGroupName());
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        //刷新recyclerView
        Log.d(TAG,"onResume");
        adapter = new MemoAdapter();
        recyclerView.setAdapter(adapter);
        //刷新导航栏
        NavigationView navigationView = mDrawerLayout.findViewById(R.id.nav_view);
        groupManager = new GroupManager(navigationView);
        groupManager.refreshMenu();
        if(!TextUtils.isEmpty(returnedData)){
            toolbar.setTitle(returnedData);
            adapter = new MemoAdapter(returnedData);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG,"onRestart");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    returnedData = data.getStringExtra("date_return_type");
                    isUnderGroupView = data.getBooleanExtra("isUnderGroupView ",
                            false);
                }
                break;
            case 2:
                returnedData = "";
                break;
            default:
        }
    }

}
