package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sam_chordas.android.stockhawk.service.StockIntentService;

/**
 *
 * Created by Zeeshan Khan on 8/21/2016.
 */
public class StockWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String strAction = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(strAction)) {
            Log.d("wigetupdate", "Widget update");

        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent=new Intent(context, StockIntentService.class);
        intent.putExtra("tag", "init");
        context.startService(intent);

//        for (int appWidgetId : appWidgetIds) {
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
//            Intent intent = new Intent(context, MyStocksActivity.class);
//            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
//            views.setOnClickPendingIntent(R.id.text, pIntent);
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//        }

    }

}
