package name.z_dev.peditor.codeEditor;

import android.animation.LayoutTransition;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import name.z_dev.peditor.R;

import static name.z_dev.peditor.models.Constants.KEY_CONTENT;

/**
 * Created by Nikita on 12.03.2017.
 */

public class CodeEditorFragment extends Fragment implements ImageButton.OnClickListener, ListenerHightlineRes{

    EditText editor;
    private String lastV = "";
    private int crPos = -1;
    ImageButton bBut, itBut, undBut, strBut, linkBut, qotBut, formBut, undoBut, redoBut,
            saveBut, browsBut, imgBut, numlistBut, bullistBut, tagBut, videoBut, audioBut, locBut, closeSBar, searchNext, clearSearch;
    TextViewUndoRedo undoRedo;
    Toolbar toolbar;
    RelativeLayout searchBar;
    EditText sEdit;
    int prsearch = 0;
    String code,filename;
    int key;
    ListenerDataChange listenerDataChange;
    SearchView searchB;
    View mainLayout;
    String query;
    Snackbar searchSnack1, searchSnack2;
    HightlineAsync hightlineAsync;

    public CodeEditorFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_codeeditor, container, false);
        mainLayout = myFragmentView.findViewById(R.id.mainLayout);
        toolbar = (Toolbar) myFragmentView.findViewById(R.id.toolbarCE);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setCollapsible(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().setTitle(getString(R.string.code_editor));
        searchBar = (RelativeLayout) myFragmentView.findViewById(R.id.searchingB);
        clearSearch = (ImageButton) myFragmentView.findViewById(R.id.clearSBut);
        searchNext = (ImageButton) myFragmentView.findViewById(R.id.nextSBut);
        closeSBar = (ImageButton) myFragmentView.findViewById(R.id.closeSBut);
        sEdit = (EditText) myFragmentView.findViewById(R.id.sEdit);
        tagBut = (ImageButton) myFragmentView.findViewById(R.id.tagBut);
        bBut = (ImageButton) myFragmentView.findViewById(R.id.boldBut);
        itBut = (ImageButton) myFragmentView.findViewById(R.id.italicBut);
        undBut = (ImageButton) myFragmentView.findViewById(R.id.underlinedBut);
        strBut = (ImageButton) myFragmentView.findViewById(R.id.strikethroughBut);
        linkBut = (ImageButton) myFragmentView.findViewById(R.id.linkBut);
        qotBut = (ImageButton) myFragmentView.findViewById(R.id.quoteBut);
        formBut = (ImageButton) myFragmentView.findViewById(R.id.formatBut);
        undoBut = (ImageButton) myFragmentView.findViewById(R.id.undoBut);
        redoBut = (ImageButton) myFragmentView.findViewById(R.id.redoBut);
        saveBut = (ImageButton) myFragmentView.findViewById(R.id.saveBut);
        browsBut = (ImageButton) myFragmentView.findViewById(R.id.browsBut);
        imgBut = (ImageButton) myFragmentView.findViewById(R.id.imgBut);
        videoBut = (ImageButton) myFragmentView.findViewById(R.id.videoBut);
        audioBut = (ImageButton) myFragmentView.findViewById(R.id.audioBut);
        numlistBut = (ImageButton) myFragmentView.findViewById(R.id.numlistBut);
        bullistBut = (ImageButton) myFragmentView.findViewById(R.id.bullistBut);
        editor = (EditText) myFragmentView.findViewById(R.id.editor);
        if(code != null) editor.setText(hightline(code));
        undoRedo = new TextViewUndoRedo(editor);
        undoRedo.setMaxHistorySize(20);
        hightlineAsync = new HightlineAsync(CodeEditorFragment.this, "", 0);
        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(hightlineAsync != null) {
                    hightlineAsync.cancel(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newValue = editable.toString();
                lastV = newValue;
                crPos = editor.getSelectionEnd() - 1;
                char ch;
                if (crPos > 0) {
                    ch = newValue.charAt(crPos);
                } else {
                    ch = '-';
                }
                if ((ch == '>' || ch == '/' || ch == ' ' || ch == '=')) {
                    hightlineAsync = new HightlineAsync(CodeEditorFragment.this, newValue, crPos+1);
                    hightlineAsync.execute();
                }
            }
        });

        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEdit.setText("");
            }
        });
        closeSBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setVisibility(View.GONE);
            }
        });
        searchNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = sEdit.getText().toString();
                int p = editor.getText().toString().toLowerCase().indexOf(query.toLowerCase(), prsearch + 1);
                if (p > -1) {
                    editor.requestFocus();
                    editor.setSelection(p, p + query.length());
                    prsearch = p;
                }
                if (p < 0 && prsearch > 0) {
                    prsearch = 0;
                    p = editor.getText().toString().toLowerCase().indexOf(query.toLowerCase(), prsearch + 1);
                    if (p > -1) {
                        editor.requestFocus();
                        editor.setSelection(p, p + query.length());
                        prsearch = p;
                    }
                }
            }
        });
        sEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    searchNext.callOnClick();
                }
                return false;
            }
        });
        browsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = writeToFile("open", editor.getText().toString());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse("file://" + location + "/open.htm"), "text/html");
                startActivity(browserIntent);
            }
        });
        undoBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (undoRedo.getCanUndo()) {
                    undoRedo.undo();
                }
            }
        });
        redoBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (undoRedo.getCanRedo()) {
                    undoRedo.redo();
                }
            }
        });
        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: save dialog
                Toast.makeText(getActivity(), writeToFile(filename, editor.getText().toString()), Toast.LENGTH_LONG).show();
            }
        });
        tagBut.setOnClickListener(this);
        bBut.setOnClickListener(this);
        itBut.setOnClickListener(this);
        imgBut.setOnClickListener(this);
        videoBut.setOnClickListener(this);
        audioBut.setOnClickListener(this);
        numlistBut.setOnClickListener(this);
        bullistBut.setOnClickListener(this);
        strBut.setOnClickListener(this);
        linkBut.setOnClickListener(this);
        qotBut.setOnClickListener(this);
        formBut.setOnClickListener(this);
        setHasOptionsMenu(true);
        return myFragmentView;
    }

    public void initCodeEditor(ListenerDataChange listenerDataChange, String code, int key, String filename){
        this.listenerDataChange = listenerDataChange;
        this.code = code;
        this.key = key;
        if(toolbar!=null) {
            if (key == KEY_CONTENT)
                toolbar.setSubtitle("Content");
            else
                toolbar.setSubtitle("Excerpt");
        }
        this.filename = filename;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_codeeditor, menu);
        searchSnack1 = Snackbar.make(editor,"",Snackbar.LENGTH_INDEFINITE);
        searchSnack1.setAction(R.string.next_button, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serchInEditor(query);
                searchSnack2.setText(query);
                searchSnack2.show();
            }
        });
        searchSnack2 = Snackbar.make(editor,"",Snackbar.LENGTH_INDEFINITE);
        searchSnack2.setAction(R.string.next_button, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serchInEditor(query);
                searchSnack1.setText(query);
                searchSnack1.show();
            }
        });
        MenuItem menuItem = menu.findItem(R.id.searchB);
        searchB = (SearchView) menuItem.getActionView();
        searchB.setQueryHint(getString(R.string.search));
        ImageView mSearchButton = (ImageView) searchB.findViewById(android.support.v7.appcompat.R.id.search_button);
        ImageView mCloseButton = (ImageView) searchB.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        mSearchButton.setImageResource(R.drawable.ic_search_white_24dp);
        mCloseButton.setImageResource(R.drawable.ic_clear_white_24dp);
        searchB.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(searchSnack1!=null)
                    searchSnack1.dismiss();
                if(searchSnack2!=null)
                    searchSnack2.dismiss();
                return false;
            }
        });
        searchB.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                serchInEditor(query);
                CodeEditorFragment.this.query = query;
                searchSnack1.setText(query);
                searchSnack1.show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                prsearch = 0;
                if(searchSnack1!=null)
                    searchSnack1.dismiss();
                if(searchSnack2!=null)
                    searchSnack2.dismiss();
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    void serchInEditor(String query){
        int p = editor.getText().toString().toLowerCase().indexOf(query.toLowerCase(), prsearch + 1);
        if (p > -1) {
            editor.requestFocus();
            editor.setSelection(p, p + query.length());
            prsearch = p;
        }
        if (p < 0 && prsearch > 0) {
            prsearch = 0;
            p = editor.getText().toString().toLowerCase().indexOf(query.toLowerCase(), prsearch + 1);
            if (p > -1) {
                editor.requestFocus();
                editor.setSelection(p, p + query.length());
                prsearch = p;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.searchB) {
//            if (searchBar.getVisibility() == View.GONE) {
//                searchBar.setVisibility(View.VISIBLE);
//            } else {
//                searchBar.setVisibility(View.GONE);
//            }
        }
        if (id == R.id.share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = editor.getText().toString();
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        if (id == R.id.okB) {
            listenerDataChange.OnDataChanged(editor.getText().toString(),key);
            getFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }

    public String writeToFile(String fileName, String body) {
        String curF;
        FileOutputStream fos = null;

        try {
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PEditor");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            curF = dir + "";
            File myFile = new File(dir, fileName + ".htm");

            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            FileOutputStream f = new FileOutputStream(myFile);
            PrintWriter pw = new PrintWriter(f);
            pw.println(body);
            pw.flush();
            pw.close();
            f.close();
//            fos = new FileOutputStream(myFile);
//            fos.write(body.getBytes());
//            fos.close();
            return curF;
        } catch (IOException e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return "error";
        }
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

    @Override
    public void onClick(View view) {
        String tag1 = "", tag2 = "";
        switch (view.getId()){
            case R.id.tagBut:
                tag1 = "<";
                tag2 = ">";
                break;
            case R.id.boldBut:
                tag1 = "<b>";
                tag2 = "</b>";
                break;
            case R.id.italicBut:
                tag1 = "<i>";
                tag2 = "</i>";
                break;
            case R.id.imgBut:
                tag1 = "<img src=\"";
                tag2 = "\" alt=\"\">";
                break;
            case R.id.videoBut:
                tag1 = "<video>\n" +
                        " <source src=\"";
                tag2 = "\">\n" +
                        "</video>";
                break;
            case R.id.audioBut:
                tag1 = "<audio controls>\n" +
                        "  <source src=\"";
                tag2 = "\"> \n" +
                        "</audio>";
                break;
            case R.id.numlistBut:
                tag1 = "<ol>\n" +
                        "   <li>";
                tag2 = "</li>\n" +
                        "</ol>";
                break;
            case R.id.bullistBut:
                tag1 = "<ul>\n" +
                        "   <li>";
                tag2 = "</li>\n" +
                        "</ul>";
                break;
            case R.id.strikethroughBut:
                tag1 = "<del>";
                tag2 = "</del>";
                break;
            case R.id.linkBut:
                tag1 = "<a href=\"\">";
                tag2 = "</a>";
                break;
            case R.id.quoteBut:
                tag1 = "<blockquote>";
                tag2 = "</blockquote>";
                break;
            case R.id.formatBut:
                tag1 = "<font size=\"\" color=\"\" face=\"\">";
                tag2 = "</font>";
                break;
        }
        int p = editor.getSelectionStart();
        String s = editor.getText().toString();
        int st = editor.getSelectionStart();
        int ed = editor.getSelectionEnd();
        if (ed == st) {
            s = s.substring(0, st) + tag1 + tag2 + s.substring(ed, s.length());
        } else {
            s = s.substring(0, st) + tag1 + s.substring(st, ed) + tag2 + s.substring(ed, s.length());
        }
        editor.setText(hightline(s));
        editor.setSelection(p + tag1.length());
    }

    @Override
    public void onHightline(Spannable res, int select) {
        editor.setText(res);
        editor.setSelection(select);
        hightlineAsync = new HightlineAsync(CodeEditorFragment.this, "", 0);
    }
}
