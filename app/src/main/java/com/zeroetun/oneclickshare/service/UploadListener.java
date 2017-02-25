package com.zeroetun.oneclickshare.service;

import android.net.Uri;

/**
 * Created by françois on 25/02/2017.
 */

public interface UploadListener {

    void onSuccess(Uri uri);
    void onFailure(Exception exception);
}
