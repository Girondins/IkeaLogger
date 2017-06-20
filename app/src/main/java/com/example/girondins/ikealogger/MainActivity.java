package com.example.girondins.ikealogger;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private TextView dateText,taskText,fullText;
    private Button dateBtn,taskBtn,startBtn,stopBtn;
    private boolean started = false;
    private String date,time,selectedTask,checkDate,startTime;
    private String[] ikeaKod = {"Kassa -> 6180", "Sälj/X-Kassa -> 3300", "Obokad/X-Kassa -> 6122"};
    private AlertDialog.Builder alertDialogBuilder, datePicker;
    private int kass,sälj,obok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiateComp();
        getDate();
        getTime();
        dateText.setText(date);
        taskText.setText(ikeaKod[0]);
        stopBtn.setClickable(started);
        startBtn.setClickable(!started);
        setupAlert();
        setupDatePicker();

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupDatePicker();
                datePicker.create().show();
            }
        });

        taskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupAlert();
                alertDialogBuilder.create().show();
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                started = true;
                startBtn.setClickable(!started);
                stopBtn.setClickable(started);
                getTime();
                startTime = time;
                fullText.setText(fullText.getText() + "" + taskText.getText() + " \nStart: " + time);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                started = false;
                startBtn.setClickable(!started);
                stopBtn.setClickable(started);
                getTime();
                fullText.setText(fullText.getText() + " End: " + time + "\n" + "\n");
            }
        });

    }

    public void initiateComp(){
        dateText = (TextView) findViewById(R.id.dateID);
        taskText = (TextView) findViewById(R.id.taskViewID);
        fullText = (TextView) findViewById(R.id.collDatID);
        dateBtn = (Button) findViewById(R.id.dateBtnID);
        taskBtn = (Button) findViewById(R.id.taskBtnID);
        startBtn = (Button)findViewById(R.id.startBtnID);
        stopBtn = (Button) findViewById(R.id.stopBtnID);
        fullText.setMovementMethod(new ScrollingMovementMethod());

    }

    public void getDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        date = df.format(c.getTime());
        checkDate = df.format(c.getTime());
    }

    public void getTime(){
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getDefault());
        time = df.format(c.getTime());
        Log.d("Time is ", " " + TimeZone.getDefault());
    }

    public void setupAlert(){
        alertDialogBuilder = new AlertDialog.Builder(
                this);
    //    alertDialogBuilder.setCustomTitle("Välj pos");
        alertDialogBuilder.setItems(ikeaKod, null);
        // alertDialogBuilder.setTitle("SELECT FREQUENCY");


        alertDialogBuilder.setSingleChoiceItems(ikeaKod, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                selectedTask = ikeaKod[i];
            }
        });

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                taskText.setText(selectedTask);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
    }

    public void setupDatePicker(){
        datePicker = new AlertDialog.Builder(this);
        final DatePicker picker = new DatePicker(this);

        datePicker.setView(picker);
        datePicker.setNegativeButton("Cancel", null);
        datePicker.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Calendar cal = Calendar.getInstance();
                cal.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
                CharSequence output = DateFormat.format("yyyy-MM-dd", cal);
                checkDate = output.toString();
                dateText.setText(checkDate);

                if(!checkDate.equals(date)){
                    startBtn.setClickable(false);
                    stopBtn.setClickable(false);
                }else{
                    startBtn.setClickable(!started);
                    stopBtn.setClickable(started);
                }

            }
        });
    }

}
