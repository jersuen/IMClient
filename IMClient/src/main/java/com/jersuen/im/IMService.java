package com.jersuen.im;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class IMService extends Service {
    public IMService() {
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
