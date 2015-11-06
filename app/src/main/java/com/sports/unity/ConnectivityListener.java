package com.sports.unity;

import android.content.Context;

/**
 * Created by madmachines on 16/10/15.
 */
public interface ConnectivityListener {

    public void internetStateChangeEvent(Context context, boolean state);

}
