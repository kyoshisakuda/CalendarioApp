package com.example.kyosh.pruebacalendari;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements CalendarPickerView.CellClickInterceptor{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        Date today = new Date();
        calendar.init(today, nextYear.getTime())
                .withSelectedDate(today);

        calendar.setCellClickInterceptor(this);
    }

    @Override
    public boolean onCellClicked(Date date) {

        //Agregar a calendario de android
        Calendar cal = Calendar.getInstance();
        final ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.CALENDAR_ID, 1);

        event.put(CalendarContract.Events.TITLE, "evento prueba");
        event.put(CalendarContract.Events.DESCRIPTION, "esto es una prueba");
        event.put(CalendarContract.Events.EVENT_LOCATION, "mi casa");

        event.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis() + 11*60*1000);
        event.put(CalendarContract.Events.DTEND, cal.getTimeInMillis()+60*60*1000);
        event.put(CalendarContract.Events.ALL_DAY, 0);   // 0 for false, 1 for true
        event.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true

        String timeZone = TimeZone.getDefault().getID();
        event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);

        Uri baseUri;
        if (Build.VERSION.SDK_INT >= 8) {
            baseUri = Uri.parse("content://com.android.calendar/events");
        } else {
            baseUri = Uri.parse("content://calendar/events");
        }

        getContentResolver().insert(baseUri, event);

        //Enviar Notificacion

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.calendar_bg_selector);
        mBuilder.setContentTitle("Notification Alert, Click Me!");
        mBuilder.setContentText("Hi, This is Android Notification Detail!");
        Intent intent = new Intent(this, SecondActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(SecondActivity.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NM.notify(0, mBuilder.build());
        }

        //Toast fecha seleccionada

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = df.format(date);
        Toast.makeText(this, fecha, Toast.LENGTH_SHORT).show();
        return true;
    }

    private String getCalendarUriBase(Activity act) {

        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            managedCursor = act.managedQuery(calendars, null, null, null, null);
        } catch (Exception e) {
        }
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                managedCursor = act.managedQuery(calendars, null, null, null, null);
            } catch (Exception e) {
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }
        return calendarUriBase;
    }
}
