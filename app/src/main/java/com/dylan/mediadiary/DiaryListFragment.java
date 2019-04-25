package com.dylan.mediadiary;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

public class DiaryListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView recyclerView;
    private DiaryAdapter adapter;
    private boolean subtitleVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.diary_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, subtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_diary_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (subtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_diary:
                Diary diary = new Diary();
                DiaryLab.get(getActivity()).addDiary(diary);
                Intent intent = DiaryPagerActivity.newIntent(getActivity(), diary.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                subtitleVisible = !subtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        DiaryLab diaryLab = DiaryLab.get(getActivity());
        int diaryCount = diaryLab.getDiaries().size();
        String subtitle = getString(R.string.subtitle_format, diaryCount);

        if (!subtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        DiaryLab diaryLab = DiaryLab.get(getActivity());
        List<Diary> diaries = diaryLab.getDiaries();
        if (adapter == null) {
            adapter = new DiaryAdapter(diaries);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setDiaries(diaries);
            adapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private class DiaryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView titleTextView;
        private TextView dateTextView;

        private Diary diary;

        public DiaryHolder (LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_diary, parent, false));
            itemView.setOnClickListener(this);
            titleTextView = (TextView) itemView.findViewById(R.id.diary_title);
            dateTextView = (TextView) itemView.findViewById(R.id.diary_date);
        }

        public void bind (Diary diary) {
            this.diary = diary;
            titleTextView.setText(diary.getTitle());
            String date = (String) DateFormat.format("yyyy年MM月dd日",diary.getDate());
            dateTextView.setText(date);
        }

        @Override
        public void onClick(View view) {
            Intent intent = DiaryPagerActivity.newIntent(getActivity(), diary.getId());
            startActivity(intent);
        }
    }

    private class DiaryAdapter extends RecyclerView.Adapter<DiaryHolder> {
        private List<Diary> diaries;
        public DiaryAdapter (List<Diary> diaries) {
            this.diaries = diaries;
        }

        @NonNull
        @Override
        public DiaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new DiaryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DiaryHolder holder, int position) {
            Diary diary = diaries.get(position);
            holder.bind(diary);
        }

        @Override
        public int getItemCount() {
            return diaries.size();
        }

        public void setDiaries(List<Diary> diaries) {
            this.diaries = diaries;
        }
    }
}
