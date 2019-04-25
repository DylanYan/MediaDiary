package com.dylan.mediadiary.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.dylan.mediadiary.Diary;
import com.dylan.mediadiary.database.DiaryDbSchema.DiaryTable;

import java.util.Date;
import java.util.UUID;

public class DiaryCursorWrapper extends CursorWrapper {
    public DiaryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Diary getDiary() {
        String uuidString = getString(getColumnIndex(DiaryTable.Cols.UUID));
        String title = getString(getColumnIndex(DiaryTable.Cols.TITLE));
        long date = getLong(getColumnIndex(DiaryTable.Cols.DATE));
        String content = getString(getColumnIndex(DiaryTable.Cols.CONTENT));
        Diary diary = new Diary(UUID.fromString(uuidString));
        diary.setTitle(title);
        diary.setDate(new Date(date));
        diary.setContent(content);
        return diary;
    }
}
