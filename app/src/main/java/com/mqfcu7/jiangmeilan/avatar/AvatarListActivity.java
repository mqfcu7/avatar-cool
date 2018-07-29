package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.mqfcu7.jiangmeilan.avatar.databinding.ActivityAvatarListBinding;

import java.util.LinkedList;
import java.util.List;

public class AvatarListActivity extends AppCompatActivity {
    private static final String EXTRA_AVATAR_TYPE =
            "com.mqfcu7.jiangmeilan.avatar.avatar_type";
    private static final String EXTRA_TITLE =
            "com.mqfcu7.jiangmeilan.avatar.title";

    private Database mDatabase;
    private Handler mHandler = new Handler();

    private ActivityAvatarListBinding mBinding;
    private PtrClassicFrameLayout mFrameLayout;
    private RecyclerView mReyclerView;
    private AvatarAdapter mvatarAdapter;
    private RecyclerAdapterWithHF mAdapter;

    private int mAvatarType;
    private String mTitle;
    List<String[]> mAvatarList = new LinkedList<>();
    private int mMaxAvatarId;

    public static Intent newIntent(Context context, int avatar_type, String title) {
        Intent intent = new Intent(context, AvatarListActivity.class);
        intent.putExtra(EXTRA_AVATAR_TYPE, avatar_type);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_avatar_list);
        Utils.setStatusBarLightMode(this, getWindow(), true);
        mDatabase = new Database(getApplicationContext());

        /*
        mAvatarType = (int)getIntent().getSerializableExtra(EXTRA_AVATAR_TYPE);
        mTitle = (String)getIntent().getSerializableExtra(EXTRA_TITLE);
        mBinding.avatarListTitleText.setText(mTitle);
        */

        mAvatarType = Database.AvatarType.GIRL;

        createAvatarList();
        createFrameLayout();
    }

    private void createAvatarList() {
        mReyclerView = mBinding.avatarListRecycleView;
        mReyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mvatarAdapter = new AvatarAdapter(mAvatarList);
        mAdapter = new RecyclerAdapterWithHF(mvatarAdapter);
        mReyclerView.setAdapter(mAdapter);
        updateAvatarList(1, 30);
    }

    private void createFrameLayout() {
        mFrameLayout = mBinding.avatarListFrameLayout;
        mFrameLayout.setLoadMoreEnable(true);
        mFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                final int num = updateAvatarList(1, 9);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyItemRangeChanged(0, num);
                        mFrameLayout.refreshComplete();
                    }
                }, 1000);
            }
        });
        mFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                int num = updateAvatarList(2, 9);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mFrameLayout.loadMoreComplete(true);
                    }
                }, 1000);
            }
        });
    }

    private int updateAvatarList(int aspect, int num) {
        final int imageNum = AvatarsLayout.IMAGE_NUM;
        List<Avatar> newAvatar = mDatabase.getBatchAvatars(mMaxAvatarId, mAvatarType, num);
        String[] ss = null;
        for (int i = 0; i < newAvatar.size() / imageNum * imageNum; ++ i) {
            mMaxAvatarId = Math.max(mMaxAvatarId, newAvatar.get(i).id);
            if (i % imageNum == 0) {
                ss = new String[imageNum];
            }
            ss[i % imageNum] = newAvatar.get(i).url;
            if ((i + 1) % imageNum == 0) {
                if (aspect == 1) {
                    ((LinkedList) mAvatarList).addFirst(ss);
                } else {
                    ((LinkedList) mAvatarList).addLast(ss);
                }
            }
        }

        return newAvatar.size() / imageNum * imageNum;
    }

    private class AvatarHolder extends RecyclerView.ViewHolder {

        private AvatarsLayout avatarsLayout;

        public AvatarHolder(View v) {
            super(v);

            avatarsLayout = (AvatarsLayout) v.findViewById(R.id.list_item_avatars_layout);
        }

        public void bindAvatar(String[] avatars) {
            avatarsLayout.setAvatars(avatars);
        }
    }

    private class AvatarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<String[]> mAvatarList;

        public AvatarAdapter(List<String[]> avatarList) {
            mAvatarList = avatarList;
        }

        @NonNull
        @Override
        public AvatarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(AvatarListActivity.this);
            View v = inflater.inflate(R.layout.list_item_avatar, parent, false);
            return new AvatarHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            String[] avatar = mAvatarList.get(position);
            ((AvatarHolder)holder).bindAvatar(avatar);
        }

        @Override
        public int getItemCount() {
            return mAvatarList.size();
        }
    }
}
