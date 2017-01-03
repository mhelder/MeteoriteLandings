package com.antonio.samir.meteoritelandingsspots.service.server;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;

public interface MeteoriteService {

    void getMeteorites(MeteoriteServiceDelegate delegate, AppCompatActivity mActivity);

    void remove();

    Cursor getMeteoriteById(String meteoriteId);

    @NonNull
    CursorLoader getMeteoriteListCursorLoader();

    String getOrderString();

    String[] getProjection();
}
