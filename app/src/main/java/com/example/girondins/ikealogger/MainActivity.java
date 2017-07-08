package com.example.girondins.ikealogger;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends Activity {
    private TextView dateText,taskText,fullText,timeText;
    private Button dateBtn,taskBtn,startBtn,stopBtn;
    private boolean started = false;
    private String date,time,selectedTask,checkDate,startTime,startedTask;
    private String[] ikeaKod = {"Kassa -> 6180", "Sälj/X-Kassa -> 3300", "Obokad/X-Kassa -> 6122"};
    private AlertDialog.Builder alertDialogBuilder, datePicker;
    private int kass=0,sälj=0,obok=0;
    private boolean resumeHasRun = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiateComp();
        getDate();
        getTime();
        showWorkedTime();
        selectedTask = ikeaKod[0];
        startedTask = selectedTask;
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
                startedTask = selectedTask;
                System.out.println(startedTask);
                fullText.setText(fullText.getText() + "" + taskText.getText() + " \nStart: " + time);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                int minutes = 0;
                started = false;
                startBtn.setClickable(!started);
                stopBtn.setClickable(started);
                getTime();
                try {
                    Date start = format.parse(startTime);
                    Date end = format.parse(time);
                    long diff = end.getTime() - start.getTime();
                    minutes = (int) diff/(60*1000);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                fullText.setText(fullText.getText() + " End: " + time + "\n" + "\n");

                switch(startedTask){
                    case "Kassa -> 6180":
                        kass = kass + minutes;
                        break;
                    case "Sälj/X-Kassa -> 3300":
                        sälj = sälj + minutes;
                        break;
                    case "Obokad/X-Kassa -> 6122":
                        obok = obok + minutes;
                        break;
                }
                showWorkedTime();
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
        timeText = (TextView) findViewById(R.id.toTimeID);
        fullText.setMovementMethod(new ScrollingMovementMethod());

    }

    public void showWorkedTime(){
        timeText.setText("Kassa: " + kass +"\n"
                        + "Sälj: " + sälj +"\n"
                        + "Obok: " + obok);
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

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sp = getApplication().getSharedPreferences("SaveState", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putString("selectedTask",selectedTask);
        editor.putString("startedTask",startedTask);
        System.out.println(selectedTask);
        editor.putString("startTime",startTime);
        editor.putBoolean("started",started);
        editor.putString("fullText",fullText.getText().toString());
        editor.putInt("kass",kass);
        editor.putInt("sälj",sälj);
        editor.putInt("obok",obok);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onPostResume();

        if (!resumeHasRun) {
            resumeHasRun = true;
            System.out.println("Wallah");
            return;
        }
        SharedPreferences sp = getApplication().getSharedPreferences("SaveState", Activity.MODE_PRIVATE);
            fullText.setText(sp.getString("fullText",null));
        System.out.println("The Text is: " + sp.getString("fullText",null));
            selectedTask = sp.getString("selectedTask",null);
            startedTask = sp.getString("startedTask",null);
            startTime = sp.getString("startTime",null);
            started = sp.getBoolean("started",false);
            kass = sp.getInt("kass",0);
            sälj = sp.getInt("sälj",0);
            obok = sp.getInt("obok",0);
        System.out.println("Resse");
    }




}
