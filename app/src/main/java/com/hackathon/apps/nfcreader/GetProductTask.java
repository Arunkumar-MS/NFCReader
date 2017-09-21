package com.hackathon.apps.nfcreader;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.hackathon.apps.nfcreader.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created by ruthvik on 21/09/2017.
 */

public class GetProductTask extends AsyncTask<Void, Void, Product> {
    private static final String PRODUCT_URL="https://ci-mango.ngbeta.net/";
    private static final String LOG_TAG = "Network Request";
    private static final String query="{\"query\": \"query GetProduct($storeid: ID $tpnc: String){product(tpnc: $tpnc storeid: $storeid){title price {unitPrice unitOfMeasure}}}\",\"variables\":{\"storeid\":6440,\"tpnc\":272043262},\"operationName\": \"GetProduct\"}";

    @Override
    protected Product doInBackground(Void... params) {
        URL url = createUrl(PRODUCT_URL);

        String jsonResponse = "";

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Product product = extractFeatureFromJson(jsonResponse);
        return null;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection = setHeaderContents(urlConnection);
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream(),"UTF-8");
            writer.write(query);
            writer.close();
            urlConnection.connect();

            int code = urlConnection.getResponseCode();
            inputStream = urlConnection.getInputStream();
            if(urlConnection.getResponseCode() == 200) {
                jsonResponse = readFromStream(inputStream);
            }
        } catch (Exception e) {
            // TODO: Handle the exception
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private HttpURLConnection setHeaderContents(HttpURLConnection urlConnection) {
        urlConnection.setRequestProperty("Accept","*/*");
        urlConnection.setRequestProperty("Content-Type","application/json");
        //urlConnection.setRequestProperty("ighs-appkey", "trn:tesco:cid:mweb:uuid:A5EA1E42-1A9D-4262-8370-770B927D12E0");
        urlConnection.setRequestProperty("ighs-language","en-US");
        //urlConnection.setRequestProperty("Cookie","OAuth.AccessToken=6c4ea4de-9a18-401b-adf2-d87308cb0643; OAuth.RefreshToken=962c6628-1923-46f1-bf31-324ee4c50c2b; UUID=a0ab14bf-a9e8-4420-bf15-bf3c14f06bb3");
        //urlConnection.setRequestProperty("ighs-trkid","f8e32840-a132-4a34-ab36-529c496e9f22");
        //urlConnection.setRequestProperty("ighs-hashed-email","'f2aea6074f9a61b01b1ce7fd9cf3ee44317ace70fa9351642303d355b15f3226");
        urlConnection.setRequestProperty("region","UK");
        urlConnection.setRequestProperty("X-Status", "Auth");
        //urlConnection.setRequestProperty("DCO","wdc");
        return urlConnection;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link Product} object by parsing out information
     * about the first earthquake from the input earthquakeJSON string.
     */
    private Product extractFeatureFromJson(String earthquakeJSON) {
        if(TextUtils.isEmpty(earthquakeJSON))
            return null;
        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            JSONArray featureArray = baseJsonResponse.getJSONArray("features");

            // If there are results in the features array
            if (featureArray.length() > 0) {
                // Extract out the first feature (which is an earthquake)
                JSONObject firstFeature = featureArray.getJSONObject(0);
                JSONObject properties = firstFeature.getJSONObject("properties");

                // Extract out the title, time, and tsunami values
                String title = properties.getString("title");
                long time = properties.getLong("time");
                int tsunamiAlert = properties.getInt("tsunami");

                // Create a new {@link Event} object
                //return new Event(title, time, tsunamiAlert);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }
}
