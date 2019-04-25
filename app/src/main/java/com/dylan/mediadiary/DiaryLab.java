package com.dylan.mediadiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dylan.mediadiary.database.DiaryBaseHelper;
import com.dylan.mediadiary.database.DiaryCursorWrapper;
import com.dylan.mediadiary.database.DiaryDbSchema;
import com.dylan.mediadiary.database.DiaryDbSchema.DiaryTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiaryLab {
    private static DiaryLab diaryLab;
    //private List<Diary> diaries;
    private Context context;
    private SQLiteDatabase database;

    public static DiaryLab get(Context context) {
        if (diaryLab == null) {
            diaryLab = new DiaryLab(context);
        }
        return diaryLab;
    }

    public void addDiary(Diary d) {
        ContentValues values = getContentValues(d);

        database.insert(DiaryTable.NAME, null, values);
    }

    public void removeDiary(Diary d) {

        database.delete(DiaryTable.NAME, DiaryTable.Cols.UUID + " = ?",
                new String[] {d.getId().toString()});
    }

    private DiaryCursorWrapper queryDiaries(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                DiaryTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new DiaryCursorWrapper(cursor);
    }

    public List<Diary> getDiaries() {
        List<Diary> diaries = new ArrayList<>();
        DiaryCursorWrapper cursor = queryDiaries(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                diaries.add(cursor.getDiary());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return diaries;
    }

    public Diary getDiary(UUID id) {
        DiaryCursorWrapper cursor = queryDiaries(DiaryTable.Cols.UUID + " = ?",
                new String[] { id.toString()});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getDiary();
        } finally {
            cursor.close();
        }
    }

    public void updateDiary (Diary diary) {
        String uuidString = diary.getId().toString();
        ContentValues values = getContentValues(diary);
        database.update(DiaryTable.NAME, values, DiaryTable.Cols.UUID + " = ?",
                new String[] { uuidString});
    }

    public File getPhotoFile(Diary diary) {
        File fileDir = context.getFilesDir();
        return new File(fileDir, diary.getPhotoFilename());
    }

    private static ContentValues getContentValues (Diary diary) {
        ContentValues values = new ContentValues();
        values.put(DiaryTable.Cols.UUID, diary.getId().toString());
        values.put(DiaryTable.Cols.TITLE, diary.getTitle());
        values.put(DiaryTable.Cols.DATE, diary.getDate().getTime());
        values.put(DiaryTable.Cols.CONTENT, diary.getContent());
        return values;
    }

    private DiaryLab(Context context) {
        this.context = context.getApplicationContext();
        database = new DiaryBaseHelper(this.context).getWritableDatabase();
        //diaries = new ArrayList<>();
    }


}
