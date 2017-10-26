package name.z_dev.peditor.codeEditor;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.lang.ref.WeakReference;

/**
 * Created by Nikita on 20.03.2017.
 */

public class HightlineAsync extends AsyncTask<Void,Void,Void> {

    String code;
    Spannable res;
    int select;
    WeakReference<ListenerHightlineRes> listenerWeakReference;

    HightlineAsync(ListenerHightlineRes listener, String code, int select){
        this.listenerWeakReference = new WeakReference<ListenerHightlineRes>(listener);
        this.code = code;
        this.select = select;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = hightline(code);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if ((listenerWeakReference != null) && (listenerWeakReference.get() != null)) {
            listenerWeakReference.get().onHightline(res, select);
        }
        super.onPostExecute(aVoid);
    }

    private Spannable hightline(String textString) {
        Spannable spanText = Spannable.Factory.getInstance().newSpannable(textString);
        int start = textString.indexOf("<");
        int end = textString.indexOf(">");
        while (start > -1 && end > -1) {
            spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#96129F")), start, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String textSub = textString.substring(start, end + 1);
            int startSub = textSub.indexOf(" ");
            int endSub = textSub.indexOf("=");
            while (startSub > -1 && endSub > -1) {
                spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#D89000")), start + startSub, start + endSub, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanText.setSpan(new StyleSpan(Typeface.BOLD), start + startSub, start + endSub, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startSub = textSub.indexOf(" ", endSub + 1);
                endSub = textSub.indexOf("=", endSub + 1);
            }

            start = textString.indexOf("<", end + 1);
            end = textString.indexOf(">", end + 1);
        }
        start = textString.indexOf("\"");
        end = textString.indexOf("\"", start + 1);
        while (start > -1 && end > -1) {
            spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#1A1AA6")), start, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanText.setSpan(new StyleSpan(Typeface.ITALIC), start + 1, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = textString.indexOf("\"", end + 1);
            end = textString.indexOf("\"", start + 1);
        }
        return spanText;
    }
}
