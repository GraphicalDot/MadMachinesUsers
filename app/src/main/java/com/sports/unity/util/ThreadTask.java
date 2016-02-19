package com.sports.unity.util;

/**
 * Created by amandeep on 26/11/15.
 */
public abstract class ThreadTask extends Thread {

    public Object object = null;

    public ThreadTask(Object object){
        this.object = object;
    }

    abstract public Object process();
    abstract public void postAction(Object object);

    @Override
    public void run() {
        super.run();

        Object returnedObject = process();
        postAction(returnedObject);
    }

}
