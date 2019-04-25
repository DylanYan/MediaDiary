package com.dylan.mediadiary;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DiaryFragment extends Fragment {
    private static final String ARG_DIARY_ID = "diary_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    public static final int REQUEST_VOICE = 1;
    public static final int REQUEST_PHOTO = 2;

    private Diary diary;
    private File photoFile;
    private EditText titleField;
    private EditText contentField;
    private Button dateButton;
    private Button voiceButton;
    private ImageButton photoButton;
    private ImageView photoView;

    public static DiaryFragment newInstance (UUID diaryId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DIARY_ID, diaryId);
        DiaryFragment fragment = new DiaryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID diaryId = (UUID) getArguments().getSerializable(ARG_DIARY_ID);
        diary = DiaryLab.get(getActivity()).getDiary(diaryId);
        photoFile = DiaryLab.get(getActivity()).getPhotoFile(diary);

        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        DiaryLab.get(getActivity()).updateDiary(diary);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        titleField = (EditText) view.findViewById(R.id.diary_title);
        titleField.setText(diary.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                diary.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        contentField = (EditText) view.findViewById(R.id.diary_content);
        contentField.setText(diary.getContent());
        contentField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                diary.setContent(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dateButton = (Button) view.findViewById(R.id.diary_date);
        updateDate();
        //dateButton.setEnabled(false);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(diary.getDate());
                dialog.setTargetFragment(DiaryFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        voiceButton = (Button) view.findViewById(R.id.add_voice_diary);
        voiceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RecogActivity.class);
                startActivityForResult(intent, REQUEST_VOICE);
            }
        });
        PackageManager packageManager = getActivity().getPackageManager();
        photoButton = (ImageButton) view.findViewById(R.id.diary_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = photoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        photoButton.setEnabled(canTakePhoto);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.dylan.mediadiary.fileprovider", photoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity: cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        photoView = (ImageView) view.findViewById(R.id.diary_photo);
        updatePhotoView();

/*        if (packageManager.resolveActivity(captureImage,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            photoButton.setEnabled(false);
        }*/

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            diary.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_VOICE) {
            String returnedData = data.getStringExtra("data_return");
            if (!returnedData.isEmpty())
                returnedData = returnedData.substring(5, returnedData.length());
            contentField.setText(returnedData);
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.dylan.mediadiary.fileprovider", photoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_diary, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_diary:
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setMessage("确定要删除吗?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogPreference,int position){
                                DiaryLab.get(getActivity()).removeDiary(diary);
                                getActivity().finish();
                            }
                        }).setNegativeButton("取消",null)
                        .create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDate() {
        String dateStr = (String) DateFormat.format("yyyy年MM月dd日",diary.getDate());
        dateButton.setText(dateStr);
    }

    private void updatePhotoView() {
        if (photoFile == null || !photoFile.exists()) {
            photoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
            photoView.setImageBitmap(bitmap);
        }
    }
}
