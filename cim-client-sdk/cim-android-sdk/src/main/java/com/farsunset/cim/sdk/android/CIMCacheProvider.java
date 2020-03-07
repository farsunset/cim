/*
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.cim.sdk.android;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class CIMCacheProvider extends ContentProvider {
    static final String MODEL_KEY = "PRIVATE_CIM_CONFIG";

    @Override
    public int delete(Uri arg0, String key, String[] arg2) {
        getContext().getSharedPreferences(MODEL_KEY, Context.MODE_PRIVATE).edit().remove(key).apply();
        return 0;
    }

    @Override
    public String getType(Uri arg0) {
        return null;
    }

    @Override
    public Uri insert(Uri arg0, ContentValues values) {
        String key = values.getAsString("key");
        String value = values.getAsString("value");
        getContext().getSharedPreferences(MODEL_KEY, Context.MODE_PRIVATE).edit().putString(key, value).apply();
        return null;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri arg0, String[] arg1, String key, String[] arg3, String arg4) {
        MatrixCursor cursor = new MatrixCursor(new String[]{"value"});
        String value = getContext().getSharedPreferences(MODEL_KEY, Context.MODE_PRIVATE).getString(arg1[0], null);
        cursor.addRow(new Object[]{value});
        return cursor;
    }

    @Override
    public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        return 0;
    }

}
