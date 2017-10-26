package name.z_dev.peditor.sender;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import name.z_dev.peditor.models.Account;
import name.z_dev.peditor.models.Post;

/**
 * Created by D.Krylov on 09.08.2015.
 */
public class Deleter extends AsyncTask<Void, Void, Void> {

    Sender h;
    String jst;
    String url;
    Post postName;
    ArrayList<Post> postNames = new ArrayList<Post>();
    WeakReference<ListenerWeakReferenceS> listenerWeakReference;
    String response, login,pasw;
    boolean error = false;
    String errorMes = "";


    public Deleter(ListenerWeakReferenceS li, Account account) {
        listenerWeakReference = new WeakReference<ListenerWeakReferenceS>(li);
        url = account.getUrl();
        login = account.getLogin();
        pasw = account.getPassword();
    }


    @Override
    protected Void doInBackground(Void... params) {
        try {
            response = deleteJSON(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
     //   response = delJSON(url,10000);
        try {
            if (error) {
                JSONArray res = new JSONArray(response);
                errorMes = res.getJSONObject(0).getString("message");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if ((listenerWeakReference != null) && (listenerWeakReference.get() != null)) {
            listenerWeakReference.get().OnWorkComplite(response,"",errorMes);
        }
        super.onPostExecute(aVoid);
    }

    public String deleteJSON(String URL) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        ResponseHandler<String> resonseHandler = new BasicResponseHandler();
        HttpDelete postMethod = new HttpDelete(url);
        postMethod.setHeader("Content-Type", "application/json");
        postMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(login, pasw), "UTF-8", false));
        try {
            HttpResponse httpResponse = null;
            httpResponse = httpClient.execute(postMethod);
            int code = httpResponse.getStatusLine().getStatusCode();
            if (code != 200 && code != 201) {
                error = true;
            }
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                InputStream input = null;
                input = entity.getContent();
                String res = convertStreamToString(input);
                Log.e("my", "response :" + res);
                Log.e("my", "URL :" + url);
                return res;
            }
        } catch (Exception e) {
            error = true;
            return  "[{\"code\":\"\",\"message\":\""+"Connaction error"+"\",\"data\":{\"status\":404}}]";
            // System.out.print(e.toString());
        }

        return null;
    }

//    public String delJSON(String url, int timeout) {
//        HttpURLConnection c = null;
//        try {
//            URL u = new URL(url);
//            c = (HttpURLConnection) u.openConnection();
//            c.setRequestMethod("DELETE");
//                c.setRequestProperty("Authorization", ("Basic " + Base64.encodeToString((
//                        login + ":" + pasw).getBytes(), Base64.NO_WRAP)));
//            c.setRequestProperty("Content-length", "0");
//            c.setUseCaches(false);
//            c.setAllowUserInteraction(false);
//            c.setConnectTimeout(timeout);
//            c.setReadTimeout(timeout);
//            c.connect();
//            int status = c.getResponseCode();
//            if (status != 200 || status != 201) {
//                error = true;
//            }
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
//            }
//
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (c != null) {
//                try {
//                    c.disconnect();
//                } catch (Exception ex) {
//                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//        return null;
//    }

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

    private void copyStream(InputStream is, OutputStream os)
    {
        final int buffer_size = 1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }


}
