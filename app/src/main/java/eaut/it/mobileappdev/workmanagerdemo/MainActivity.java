package eaut.it.mobileappdev.workmanagerdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class MainActivity extends AppCompatActivity {
    Button btn;
    EditText textLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn = findViewById(R.id.button);
        textLimit = findViewById(R.id.limitText);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioGroup radioGroup = findViewById(R.id.radioGroup);
                int checked = radioGroup.getCheckedRadioButtonId();
                int limit = getLimit();
                if(checked == R.id.count1) countInMainThread(limit);
                else countInWorkerThread(limit);
            }
        });
    }

    public void countInMainThread(int limit) {
        for (int i = 0; i < limit; i++) {
            Log.i("COUNT", i + "");
        }
    }

    private void countInWorkerThread(int limit) {
        //data passed to worker
        Data data = new Data.Builder().putInt(CountWorker.COUNT_LIMIT, limit).build();

        //constraints for worker
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        //build work request
        WorkRequest workRequest = new OneTimeWorkRequest.Builder(CountWorker.class)
                //    .setConstraints(constraints)
                .setInputData(data)
                .build();

        //put work request into queue
        WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);

        //observe worker
        observeWorker(workRequest, limit);
    }

    private int getLimit() {
        try {
            return Integer.parseInt(textLimit.getText().toString());
        } catch (Exception e) {
            return 500;
        }
    }

    private void observeWorker(WorkRequest workRequest, int limit) {
        WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(workRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null) {
                            Toast.makeText(getApplicationContext(),
                                    "Status: " + workInfo.getState().name(),
                                    Toast.LENGTH_SHORT).show();

                            if (workInfo.getState().isFinished()) {
                                Data data1 = workInfo.getOutputData();
                                String msg = data1.getString(CountWorker.DATA_SENT) + ", limit: " + limit;
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}