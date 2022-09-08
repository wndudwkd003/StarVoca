package com.zynar.starvoca;


import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBAsyncTask extends Application {
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    public class WordsInsert {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CORES);
    }

}
