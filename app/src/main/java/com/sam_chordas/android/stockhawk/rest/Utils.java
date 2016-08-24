package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.util.Log;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

    private static String LOG_TAG = Utils.class.getSimpleName();

    public static boolean showPercent = true;

    public static ArrayList quoteJsonToContentVals(String JSON) {
        Log.d("to quotes", JSON);
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject = null;
        JSONArray resultsArray = null;
        ContentProviderOperation operation;
        try {
            jsonObject = new JSONObject(JSON);
            if (jsonObject != null && jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject("query");
                int count = Integer.parseInt(jsonObject.getString("count"));
                if (count == 1) {
                    jsonObject = jsonObject.getJSONObject("results")
                            .getJSONObject("quote");
                    operation = buildBatchOperation(jsonObject);
                    if (operation != null)
                        batchOperations.add(operation);

                } else {
                    resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            operation = buildBatchOperation(jsonObject);
                            if (operation != null)
                                batchOperations.add(operation);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return batchOperations;
    }

    public static String truncateBidPrice(String bidPrice) {

        try {
            bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
        } catch (NumberFormatException ex) {
            bidPrice = "0.00";
        }
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange) {
        Log.d("truncateChange", change);
//        if(change==null ||change.isEmpty() ||change.equals("null"))
//            return change;
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = 0;
        try {
            round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        } catch (NumberFormatException ex) {
        }
        change = String.format("%.2f", round);
        StringBuffer changeBuffer = new StringBuffer(change);
        changeBuffer.insert(0, weight);

        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        String symbol, bid, changeInPercent, change;

        change = jsonObject.optString("Change");
        if (change == null || change.equals("null"))
            return null;
        symbol = jsonObject.optString("symbol");
        if (symbol == null || symbol.equals("null"))
            return null;
        bid = jsonObject.optString("Bid");
        if (bid == null || bid.equals("null"))
            return null;
        changeInPercent = jsonObject.optString("ChangeinPercent");
        if (changeInPercent == null || changeInPercent.equals("null"))
            return null;
        Log.d("Utils", symbol + bid + changeInPercent + change);
        builder.withValue(QuoteColumns.SYMBOL, symbol);
        builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(bid));
        builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(changeInPercent, true));
        builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
        builder.withValue(QuoteColumns.ISCURRENT, 1);
        if (change.charAt(0) == '-') {
            builder.withValue(QuoteColumns.ISUP, 0);
        } else {
            builder.withValue(QuoteColumns.ISUP, 1);
        }
        return builder.build();
    }
}
