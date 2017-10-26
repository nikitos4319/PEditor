package name.z_dev.peditor.loader;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import name.z_dev.peditor.PostsFragment;
import name.z_dev.peditor.models.Account;
import name.z_dev.peditor.models.Post;

/**
 * Created by Home on 23.07.2015.
 */
public class Loader extends AsyncTask<Void, Void, Void> {

    Loader h;
    String jst;
    String url;
    Post postName;
    ArrayList<Post> postNames = new ArrayList<Post>();
    WeakReference<ListenerWeakReferenceL> listenerWeakReference;
    String login,pasw;
    boolean useAuth = false;
    boolean error = false;
    String errorMes = "";


    public Loader(ListenerWeakReferenceL li, Account account, boolean auth) {
        listenerWeakReference = new WeakReference<ListenerWeakReferenceL>(li);
        url = account.getUrl();
        login = account.getLogin();
        pasw = account.getPassword();
        useAuth = auth;
    }




    @Override
    protected Void doInBackground(Void... params) {
     //   jst = getJSON(url, 10000);
        jst = getHTPPRequest(url);
//        int ed = jst.indexOf("[");
//        jst ="\"posts\":" + jst.substring(ed);
//        Log.e("my",jst);
        try {
            if(error){
                if(jst!=null) {
                    JSONArray res = new JSONArray(jst);
                    errorMes = res.getJSONObject(0).getString("message");
                }else {
                    JSONArray res = new JSONArray(errorMes);
                    errorMes = res.getJSONObject(0).getString("message");
                }
            }else {
                // JSONObject jsonRootObject = new JSONObject(jst);
                JSONArray jsonArray = new JSONArray(jst);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Post postName = new Post();
                    postName.setId(jsonObject.optLong("id"));
                    postName.setCrDate(jsonObject.optString("date"));
                    postName.setModified(jsonObject.optString("modified"));
                    postName.setSlug(URLDecoder.decode(jsonObject.optString("slug"), "UTF-8"));
                    postName.setType(jsonObject.optString("type"));
                    postName.setLink(jsonObject.optString("link"));
                    postName.setAuthor(jsonObject.optLong("author"));
                    postName.setFormat(jsonObject.optString("format"));
                    JSONObject jsoj = jsonObject.optJSONObject("title");
                    postName.setTitle(URLDecoder.decode(jsoj.optString("rendered"),"UTF-8"));
                    jsoj = jsonObject.optJSONObject("content");
                    postName.setContent(URLDecoder.decode(jsoj.optString("rendered"),"UTF-8"));
                    jsoj = jsonObject.optJSONObject("excerpt");
                    postName.setExcerpt(URLDecoder.decode(jsoj.optString("rendered"),"UTF-8"));
                    //     jsonObject = jsonObject.getJSONObject("_links");
//                jsoj =  jsonObject.optJSONObject("author");
//                if (jsoj.optString("embeddable")=="true"){
//                postName.setAuthorLink(jsoj.optString("href"));}

                    String s = (jsonObject.optString("comment_status").toString().toLowerCase());
                    if (s.equals("open")) {
                        postName.setComment_status(true);
                    } else {
                        postName.setComment_status(false);
                    }
                    s = (jsonObject.optString("ping_status"));
                    if (s.equals("open")) {
                        postName.setPing_status(true);
                    } else {
                        postName.setPing_status(false);
                    }
                    s = (jsonObject.optString("sticky"));
                    if (s.equals("true")) {
                        postName.setSticky(true);
                    } else {
                        postName.setSticky(false);
                    }


                    postNames.add(postName);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if ((listenerWeakReference != null) && (listenerWeakReference.get() != null)) {
            listenerWeakReference.get().OnWorkComplite(postNames,errorMes);
        }
        super.onPostExecute(aVoid);
    }

    public String getHTPPRequest(String URL) {

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
        HttpGet httppost = new HttpGet(URL);
        httppost.setHeader("Content-Type", "application/json");
        //   httppost.setHeader("Accept", "application/json");

        httppost.addHeader("Authorization", ("Basic " + Base64.encodeToString((
                login + ":" + pasw).getBytes(), Base64.NO_WRAP)));
        //   httppost.setHeader("Authorization",("administrator" + ":" + "ighIOGyuTFuGYGgigIUH05"));

        try {
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

    public String getJSON(String url, JSONObject jsobj) {
        try {
            //constants
            URL nurl = new URL(url);
            String message = jsobj.toString();

            HttpURLConnection conn = (HttpURLConnection) nurl.openConnection();
            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout(20000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(message.getBytes().length);

            //make some HTTP header nicety
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            //open
            conn.connect();

            //setup send
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(message.getBytes());
            //clean up
            os.flush();

            //do somehting with response
            InputStream is = conn.getInputStream();
            String contentAsString = convertStreamToString(is);
            os.close();
            is.close();
            conn.disconnect();
            return contentAsString;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //clean up

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

//    public String getJSON(String url, int timeout) {
//        HttpURLConnection c = null;
//        try {
//            URL u = new URL(url);
//            c = (HttpURLConnection) u.openConnection();
//            c.setRequestMethod("GET");
//            //if(useAuth){
//            c.setRequestProperty("Authorization", ("Basic " + Base64.encodeToString((
//                    login + ":" + pasw).getBytes(), Base64.NO_WRAP)));
//            //}
//            c.setRequestProperty("Content-length", "0");
//            c.setRequestProperty("Count","0");
//            c.setUseCaches(false);
//            c.setAllowUserInteraction(false);
//            c.setConnectTimeout(timeout);
//            c.setReadTimeout(timeout);
//            c.connect();
//            int status = c.getResponseCode();
//
//            switch (status) {
//                case 200:
//                case 201:
//                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
//                    StringBuilder sb = new StringBuilder();
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        sb.append(line + "\n");
//                    }
//                    br.close();
//                    return sb.toString();
////                default:
////                    error = true;
////                    errorMes = "[{\"code\":\"\",\"message\":\"Connaction error.\",\"data\":{\"status\":404}}]";
//            }
//
//        } catch (MalformedURLException ex) {
//            error = true;
//            errorMes = "[{\"code\":\"\",\"message\":\""+"Connaction error"+"\",\"data\":{\"status\":404}}]";
//          //  Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            error = true;
//            errorMes = "[{\"code\":\"\",\"message\":\""+"Connaction error"+"\",\"data\":{\"status\":404}}]";
//          //  Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (c != null) {
//                try {
//                    c.disconnect();
//                } catch (Exception ex) {
//                    error = true;
//                    errorMes = "[{\"code\":\"\",\"message\":\""+"Connaction error"+"\",\"data\":{\"status\":404}}]";
//                 //   Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//        return null;
//    }
//public String makePOSTRequest(String url, List<NameValuePair> nameValuePairs) {
//    String response = "";
//
//    try {
//        HttpClient httpClient = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost(url);
//        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//        HttpResponse httpResponse = httpClient.execute(httpPost);
//        HttpEntity httpEntity = httpResponse.getEntity();
//        response = EntityUtils.toString(httpEntity);
//    } catch (UnsupportedEncodingException e) {
//        e.printStackTrace();
//    } catch (ClientProtocolException e) {
//        e.printStackTrace();
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//    Log.d("upload", "POST Response >>> " + response);
//    return response;
//
//}

}