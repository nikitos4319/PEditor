package name.z_dev.peditor;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.NestedScrollingChild;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import name.z_dev.peditor.models.Account;

import static name.z_dev.peditor.models.Constants.KEY_EDIT_ACC;
import static name.z_dev.peditor.models.Constants.KEY_LOAD_ACC;
import static name.z_dev.peditor.models.Constants.KEY_NEW_ACC;


public class AccountingFragment extends Fragment {

    public AccountingFragment() {

    }

    EditText loginEditText, passwordEditText, nameEditText, urlEditText;
    FloatingActionButton fabConnect;
    NestedScrollingChild scrollingChild;
    Account account;
    int key;
    PostsFragment postsFragment;
    RadioButton loadPosts;
    CheckBox useAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_accountint, container, false);
        Toolbar toolbar = (Toolbar) myFragmentView.findViewById(R.id.toolbarA);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setCollapsible(false);
        toolbar.setTitle(getString(R.string.accounting));
        getActivity().setTitle(getString(R.string.post_editor));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        loadPosts = (RadioButton) myFragmentView.findViewById(R.id.loadPosts);
        useAuth = (CheckBox) myFragmentView.findViewById(R.id.useAuth);
        postsFragment = new PostsFragment();
        scrollingChild = (NestedScrollingChild) myFragmentView.findViewById(R.id.scrollViewAccounting);
        loginEditText = (EditText) myFragmentView.findViewById(R.id.loginEditText);
        passwordEditText = (EditText) myFragmentView.findViewById(R.id.passwordEditText);
        nameEditText = (EditText) myFragmentView.findViewById(R.id.nameEditText);
        urlEditText = (EditText) myFragmentView.findViewById(R.id.urlEditText);
        fabConnect = (FloatingActionButton) myFragmentView.findViewById(R.id.fabConnect);
        fabConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (key){
                    case KEY_LOAD_ACC:
                        Account a = new Account();
                        a.setLogin(loginEditText.getText().toString());
                        a.setPassword(passwordEditText.getText().toString());
                        a.setName(nameEditText.getText().toString());
                        if(urlEditText.getText().toString().toCharArray()[urlEditText.length()-1] != '/')
                            urlEditText.setText(urlEditText.getText().toString()+'/');
                        if(loadPosts.isChecked()){
                            a.setUrl(urlEditText.getText().toString()+"posts");
                        }else{
                            a.setUrl(urlEditText.getText().toString()+"pages");
                        }
                        postsFragment.setAccount(a, loadPosts.isChecked(), useAuth.isChecked());
                        getFragmentManager().beginTransaction().replace(R.id.conteiner, postsFragment).addToBackStack(null).commit();
                        break;
                    case KEY_EDIT_ACC:

                        break;
                    case  KEY_NEW_ACC:
//                        if (nameEditText.getText().length() > 0 && urlEditText.getText().length() > 0 &&
//                                (urlEditText.getText().toString().equals("http://") || urlEditText.getText().toString().equals("https://"))) {
                            DBHelper dbHelper = new DBHelper(getActivity());
                            ContentValues cv = new ContentValues();
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            cv.put("name", nameEditText.getText().toString());
                            cv.put("url", urlEditText.getText().toString());
                            cv.put("login", loginEditText.getText().toString());
                            cv.put("password", Base64.encode(passwordEditText.getText().toString().getBytes(), Base64.DEFAULT));
                            //    cv.put("useauth", apikeyAing.getText().toString());
                            long rowID = db.insert("accountstable", null, cv);
//                        } else {
//                            Snackbar.make((View) scrollingChild,getString(R.string.bad_url_or_name), Snackbar.LENGTH_LONG).show();
//                        }
                        break;
                }
            }
        });
        if(account != null) {
            loginEditText.setText(account.getLogin());
            passwordEditText.setText(account.getPassword());
            nameEditText.setText(account.getName());
            urlEditText.setText(account.getUrl());
        }
        setHasOptionsMenu(true);
        return myFragmentView;
    }

    public void setAccount(Account account, int key){
        this.account = account;
        this.key = key;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_accounting, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.aingDelete) {
            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
            deleteDialog.setTitle(getString(R.string.delete_dialog_title));
            deleteDialog.setMessage(getString(R.string.delete_generaly_dialog_message));
            deleteDialog.setPositiveButton(getString(R.string.yes_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DBHelper dbHelper = new DBHelper(getActivity());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    int delCount = db.delete("accountstable", "id = " + account.getId(), null);
                    getFragmentManager().popBackStack();
                }
            });
            deleteDialog.setNegativeButton(getString(R.string.no_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            deleteDialog.show();
        }
        if (item.getItemId() == R.id.aingSave){
            if (key == KEY_NEW_ACC) {
                if (nameEditText.getText().length() > 0 &&
                        urlEditText.getText().length() > 0 &&
                        (urlEditText.getText().toString().equals("http://")||urlEditText.getText().toString().equals("https://"))) {
                    DBHelper dbHelper = new DBHelper(getActivity());
                    ContentValues cv = new ContentValues();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    cv.put("name", nameEditText.getText().toString());
                    cv.put("url", urlEditText.getText().toString());
                    cv.put("login", loginEditText.getText().toString());
                    cv.put("password", Base64.encode(passwordEditText.getText().toString().getBytes(), Base64.DEFAULT));
                    //    cv.put("useauth", apikeyAing.getText().toString());
                    long rowID = db.insert("accountstable", null, cv);
                    getFragmentManager().popBackStack();
                } else {
                    Snackbar.make((View) scrollingChild,getString(R.string.bad_url_or_name), Snackbar.LENGTH_LONG).show();
                }
            }
            if (key == KEY_EDIT_ACC || key == KEY_LOAD_ACC) {
                if (nameEditText.getText().length() > 0 && urlEditText.getText().length() > 0) {
                    DBHelper dbHelper = new DBHelper(getActivity());
                    ContentValues cv = new ContentValues();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    cv.put("name", nameEditText.getText().toString());
                    cv.put("url", urlEditText.getText().toString());
                    cv.put("login", loginEditText.getText().toString());
                    cv.put("password", Base64.encode(passwordEditText.getText().toString().getBytes(), Base64.DEFAULT));
                    //    cv.put("apikey", apikeyAing.getText().toString());
                    long rowID = db.update("accountstable", cv, "id = ?",
                            new String[]{account.getId() + ""});
                    getFragmentManager().popBackStack();
                } else {
                    Snackbar.make((View) scrollingChild,getString(R.string.bad_url_or_name), Snackbar.LENGTH_LONG).show();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
