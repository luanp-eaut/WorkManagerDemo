package eaut.it.mobileappdev.workmanagerdemo;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CountWorker extends Worker {
    public static final String DATA_SENT = "Sent";
    public static final String COUNT_LIMIT = "key_count";

    public CountWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Getting Data from InputData
        Data data = getInputData();
        int countLimit = data.getInt(COUNT_LIMIT, 0);

        for (int i = 0; i < countLimit; i++) {
            Log.i("COUNT", i + "");
        }

        // Sending Data and Done info
        Data dataToSend = new Data.Builder().putString(DATA_SENT,"Task Done Successfully").build();
        return Result.success(dataToSend);
    }
}
