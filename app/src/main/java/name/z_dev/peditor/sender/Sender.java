package name.z_dev.peditor.sender;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import name.z_dev.peditor.sender.ListenerWeakReferenceS;
import name.z_dev.peditor.models.Post;

/**
 * Created by D.Krylov on 09.08.2015.
 */
public class Sender extends AsyncTask<Void, Void, Void> {

    Sender h;
    String jst;
    String url;
    Post postName;
    ArrayList<Post> postNames = new ArrayList<Post>();
    WeakReference<ListenerWeakReferenceS> listenerWeakReference;
    String response, login, pasw;
    boolean isnew;
    boolean error = false;
    String errorMes = "", publishing = "pubish",dateF;


    public Sender(ListenerWeakReferenceS li, String urlC, String user, String pwd, boolean newP, Post post, String publish,String date) {
        listenerWeakReference = new WeakReference<ListenerWeakReferenceS>(li);
        url = urlC;
        login = user;
        pasw = pwd;
        isnew = newP;
        postName = post;
        publishing = publish;
        dateF = date;
    }


    @Override
    protected Void doInBackground(Void... params) {
        try {

            JSONObject jsonObject = new JSONObject();
            //    jsonObject.put("author", login);
            //  jsonObject.put("date", postName.getCrDate());
            //  jsonObject.put("id", postName.getId());
            //  jsonObject.put("modified", postName.getModified());
            //    jsonObject.put("title", new JSONObject().put("rendered",postName.getTitle()));
            jsonObject.put("title", postName.getTitle());
            jsonObject.put("slug", postName.getSlug());
            if(publishing.equals("auto")==false) {
                jsonObject.put("status", publishing);
                if (dateF.length()>0) {
                    jsonObject.put("date", dateF);
                }
            }
            //   jsonObject.put("slug", postName.getSlug());
            //   jsonObject.put("type", postName.getType());
            //   jsonObject.put("format", postName.getFormat());
            //   jsonObject.put("content", new JSONObject().put("rendered", postName.getTitle()));
            //   jsonObject.put("excerpt", new JSONObject().put("rendered", postName.getTitle()));
            jsonObject.put("content", postName.getContent());
            jsonObject.put("excerpt", postName.getExcerpt());
            if (postName.isComment_status()) {
                jsonObject.put("comment_open", "true");
            } else {
                jsonObject.put("comment_open", "false");
            }
            if (postName.isPing_status()) {
                jsonObject.put("ping_open", "true");
            } else {
                jsonObject.put("ping_open", "false");
            }
            if (postName.isSticky()) {
                jsonObject.put("sticky", "true");
            } else {
                jsonObject.put("sticky", "false");
            }

            response = postHTPPRequest(url, jsonObject.toString());
            //response = setJSON(url, 10000, jsonObject.toString());
//            Log.e("my",response);
            if(error){
                JSONArray res = new JSONArray(response);
                errorMes=res.getJSONObject(0).getString("message");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public String postHTPPRequest(String URL, String paramenter) throws IOException {
//        HttpClient httpClient = new DefaultHttpClient();
//        ResponseHandler<String> resonseHandler = new BasicResponseHandler();
//        HttpPost postMethod = new HttpPost(url);
//        postMethod.setHeader("Content-Type", "application/json");
//        postMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(login, pasw), "UTF-8", false));
//        postMethod.setEntity(new StringEntity(paramenter, "UTF-8"));
//        HttpResponse httpResponse = null;
//        httpResponse = httpClient.execute(postMethod);
//        HttpEntity entity = httpResponse.getEntity();
//        if (entity != null) {
//            InputStream input = null;
//            input = entity.getContent();
//            String res = convertStreamToString(input);
//            Log.e("my", "response :" + res);
//            return res;
//        }
//
//        return null;
//    }


//    public String setJSON(String urlc, int timeout, String date) throws MalformedURLException {
//        URL url = new URL(urlc);
//        HttpURLConnection conn = null;
//        try {
//            conn = (HttpURLConnection) url.openConnection();
//            try {
//                conn.setRequestMethod("POST"); //use post method
//                conn.setDoOutput(true); //we will send stuff
//                conn.setDoInput(true); //we want feedback
//                conn.setUseCaches(false); //no caches
//                conn.setAllowUserInteraction(false);
//                conn.setRequestProperty("Content-Type", "application/json");
////                conn.setRequestProperty("Authorization", ("Basic " + Base64.encodeToString((
////                        "administrator" + ":" + "ighIOGyuTFuGYGgigIUH05").getBytes(), Base64.NO_WRAP)));
//            } catch (ProtocolException e) {
//            }
//
//            // Open a stream which can write to the URL******************************************
//            OutputStream out = conn.getOutputStream();
//            try {
//                OutputStreamWriter wr = new OutputStreamWriter(out);
//                wr.write(date); //ezm is my JSON object containing the api commands
//                wr.flush();
//                wr.close();
//            } catch (IOException e) {
//            } finally { //in this case, we are ensured to close the output stream
//                if (out != null)
//                    out.close();
//            }
//
//            int status = conn.getResponseCode();
//            switch (status) {
//                case 200:
//                case 201:
//                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    StringBuilder sb = new StringBuilder();
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        sb.append(line + "\n");
//                    }
//                    br.close();
//                    return sb.toString();
//            }
//
//            // Open a stream which can read the server response*********************************
//            InputStream in = conn.getInputStream();
//            if (conn.getResponseCode() != 200 || conn.getResponseCode() != 201) {
//                error = true;
//            }
//        } catch (IOException e) {
//        } finally {  //in this case, we are ensured to close the connection itself
//            if (conn != null)
//                conn.disconnect();
//        }
//        return null;
//    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if ((listenerWeakReference != null) && (listenerWeakReference.get() != null)) {
            listenerWeakReference.get().OnWorkComplite(response, "", errorMes);
        }
        super.onPostExecute(aVoid);
    }

    public String postHTPPRequest(String URL, String paramenter) {

        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = 10*1000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 10*1000;

        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpPost httppost = new HttpPost(URL);
        httppost.setHeader("Content-Type", "application/json");
     //   httppost.setHeader("Accept", "application/json");

        httppost.addHeader("Authorization", ("Basic " + Base64.encodeToString((
                login + ":" + pasw).getBytes(), Base64.NO_WRAP)));
       //   httppost.setHeader("Authorization",("administrator" + ":" + "ighIOGyuTFuGYGgigIUH05"));

        try {
            if (paramenter != null) {
                StringEntity tmp = null;
                tmp = new StringEntity(paramenter, "UTF-8");
                httppost.setEntity(tmp);
            }
            HttpResponse httpResponse = null;
            httpResponse = httpclient.execute(httppost);
            int code = httpResponse.getStatusLine().getStatusCode();
            if(code!=200&&code!=201){
                error = true;
              //  Log.e("my",code+"");
            }
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                InputStream input = null;
                input = entity.getContent();
                String res = convertStreamToString(input);
                return res;
            }
        }
        catch (Exception e) {
            error = true;
            return  "[{\"code\":\"\",\"message\":\""+"Connaction error"+"\",\"data\":{\"status\":404}}]";
          // System.out.print(e.toString());
        }
        return null;
    }

    private String convertStreamToString(InputStream is) {
        ByteArrayOutputStream oas = new ByteArrayOutputStream();
        copyStream(is, oas);
        String t = oas.toString();
        try {
            oas.close();
            oas = null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return t;
    }

    private void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

}
