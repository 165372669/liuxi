package com.android.lucy.treasure.bean;

import android.os.Parcel;
import android.os.Parcelable;

/*
章节目录的ID和章节名
 */
public class ChapterIDAndName implements Parcelable{
    private int chapterId;
    private String chapterName;


    public ChapterIDAndName(int chapterId, String chapterName) {
        this.chapterId = chapterId;
        this.chapterName = chapterName;
    }

    protected ChapterIDAndName(Parcel in) {
        chapterId = in.readInt();
        chapterName = in.readString();
    }

    public static final Creator<ChapterIDAndName> CREATOR = new Creator<ChapterIDAndName>() {
        @Override
        public ChapterIDAndName createFromParcel(Parcel in) {
            return new ChapterIDAndName(in);
        }

        @Override
        public ChapterIDAndName[] newArray(int size) {
            return new ChapterIDAndName[size];
        }
    };

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(chapterId);
        parcel.writeString(chapterName);
    }
}
