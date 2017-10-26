package name.z_dev.peditor;

import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import name.z_dev.peditor.models.AccAdapter;
import name.z_dev.peditor.models.Account;
import name.z_dev.peditor.models.RecyclerItemClickListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static name.z_dev.peditor.models.Constants.KEY_LOAD_ACC;
import static name.z_dev.peditor.models.Constants.KEY_NEW_ACC;


/**
 * A placeholder fragment containing a simple view.
 */
public class AccountsFragment extends Fragment {

    private FloatingActionButton addBut;
    private RecyclerView accsList;
    private ArrayList<Account> accounts = new ArrayList<Account>();
    private AccAdapter adapter;
    private Toolbar toolbar;
    private DBHelper dbHelper;

    AccountingFragment accountFragment;


    public AccountsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_accounts, container, false);
        accsList = (RecyclerView) myFragmentView.findViewById(R.id.accsList);
        addBut = (FloatingActionButton) myFragmentView.findViewById(R.id.fabAdd);
        dbHelper = new DBHelper(getActivity());
        toolbar = (Toolbar) myFragmentView.findViewById(R.id.toolbarM);
        toolbar.setTitle(getString(R.string.accounts));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_exit_to_app_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        getActivity().setTitle("Post Editor");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        accountFragment = new AccountingFragment();
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountFragment.setAccount(new Account("","","","",""), KEY_NEW_ACC);
                getFragmentManager().beginTransaction().replace(R.id.conteiner, accountFragment).addToBackStack(null).commit();
                //getFragmentManager().beginTransaction().hide(AccountsFragment.this).commit();
            }
        });
        accsList = (RecyclerView) myFragmentView.findViewById(R.id.accsList);
        accsList.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), accsList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Account account = accounts.get(position);
                accountFragment.setAccount(account, KEY_LOAD_ACC);
                getFragmentManager().beginTransaction().replace(R.id.conteiner, accountFragment).addToBackStack(null).commit();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        return myFragmentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            readBD();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    void readBD () throws UnsupportedEncodingException {
        accounts.clear();
        accsList.setLayoutManager(new LinearLayoutManager(accsList.getContext()));
        accsList.setAdapter(null);
        //  ContentValues cv = new ContentValues();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("accountstable", null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int urlColIndex = c.getColumnIndex("url");
            int loginColIndex = c.getColumnIndex("login");
            int passwordColIndex = c.getColumnIndex("password");
            int apikeyColIndex = c.getColumnIndex("apikey");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Account account = new Account();
                account.setId(c.getInt(idColIndex));
                account.setName(c.getString(nameColIndex));
                account.setUrl(c.getString(urlColIndex));
                account.setLogin(c.getString(loginColIndex));
                account.setPassword(new String(Base64.decode(c.getBlob(passwordColIndex),Base64.DEFAULT),"UTF-8"));
                account.setApikey(c.getString(apikeyColIndex));
                accounts.add(account);


                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false -
                // выходим из цикла
            } while (c.moveToNext());
            adapter = new AccAdapter(getActivity(), accounts);
            accsList.setLayoutManager(new LinearLayoutManager(accsList.getContext()));
            accsList.setAdapter(adapter);
        } else
            Snackbar.make(accsList,"No accounts saved",Snackbar.LENGTH_INDEFINITE).setAction("Create", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBut.callOnClick();}}).show();
        c.close();
    }
}
