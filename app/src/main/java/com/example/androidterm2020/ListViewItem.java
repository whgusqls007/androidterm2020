package com.example.androidterm2020;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private int type;

    private String titleStr;

    private Drawable iconDrawable;
    private String nameStr;

    public void setType(int type) {
        this.type = type;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setIcon(Drawable icon) {
        iconDrawable = icon;
    }

    public void setName(String name) {
        nameStr = name;
    }

    public int getType() {
        return this.type;
    }

    public Drawable getIcon() {
        return this.iconDrawable;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getName() { return this.nameStr; }
}

