package name.z_dev.peditor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import name.z_dev.peditor.codeEditor.CodeEditorFragment;
import name.z_dev.peditor.codeEditor.ListenerDataChange;
import name.z_dev.peditor.models.Account;
import name.z_dev.peditor.models.Post;
import name.z_dev.peditor.sender.Deleter;
import name.z_dev.peditor.sender.ListenerWeakReferenceS;
import name.z_dev.peditor.sender.Sender;

import static name.z_dev.peditor.models.Constants.KEY_CONTENT;
import static name.z_dev.peditor.models.Constants.KEY_EXCERPT;
import static name.z_dev.peditor.models.Constants.KEY_NEW_POST;

/**
 * Created by Nikita on 12.03.2017.
 */

public class PostingFragment extends Fragment implements ListenerWeakReferenceS, ListenerDataChange {

    Toolbar toolbar;
    Post post;
    Account account;
    EditText titlePosting, slugPosting, contentPosting, excerptPosting;
    TextView contEditButtonPosting, excerptEditButtonPosting, contentCount, excerptCount;
    CheckBox commentsPosting, pingsPosting, stickyPosting;
    FloatingActionButton actionButton;
    Deleter deleter;
    Dialog dialogL;
    Sender sender;
    View scrollview;
    Spinner statusSpinner;
    int key;
    boolean isPost;
    CodeEditorFragment codeEditorFragment;

    public PostingFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_posting, container, false);
        toolbar = (Toolbar) myFragmentView.findViewById(R.id.toolbarPosting);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setCollapsible(false);
        toolbar.setTitle(getString(R.string.posting));
        getActivity().setTitle(getString(R.string.post_editor));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        codeEditorFragment = new CodeEditorFragment();
        scrollview = myFragmentView.findViewById(R.id.scrollViewPing);
        titlePosting = (EditText) myFragmentView.findViewById(R.id.titlePosting);
        slugPosting = (EditText) myFragmentView.findViewById(R.id.slugPosting);
        contentPosting = (EditText) myFragmentView.findViewById(R.id.contentPosting);
        excerptPosting = (EditText) myFragmentView.findViewById(R.id.excerptPosting);
        contEditButtonPosting = (TextView) myFragmentView.findViewById(R.id.contEditButtonPosting);
        excerptEditButtonPosting = (TextView) myFragmentView.findViewById(R.id.excerptEditButtonPosting);
        commentsPosting = (CheckBox) myFragmentView.findViewById(R.id.commentsPosting);
        pingsPosting = (CheckBox) myFragmentView.findViewById(R.id.pingsPosting);
        stickyPosting = (CheckBox) myFragmentView.findViewById(R.id.stickyPosting);
        statusSpinner = (Spinner) myFragmentView.findViewById(R.id.statusSpinner);
        actionButton = (FloatingActionButton) myFragmentView.findViewById(R.id.fabConnectPosting);
        if(post != null) {
            titlePosting.setText(post.getTitle());
            slugPosting.setText(post.getSlug());
            contentPosting.setText(post.getContent());
            excerptPosting.setText(post.getExcerpt());
            commentsPosting.setChecked(post.isComment_status());
            pingsPosting.setChecked(post.isPing_status());
            stickyPosting.setChecked(post.isSticky());
            statusSpinner.setSelection(0);
        }
        setHasOptionsMenu(true);
        dialogL = new Dialog(getActivity());
        dialogL.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflaterD = getActivity().getLayoutInflater();
        View view = inflaterD.inflate(R.layout.progress_dialog, null);
        ((ProgressBar) view.findViewById(R.id.progressBar))
                .getIndeterminateDrawable()
                .setColorFilter(getResources().getColor(R.color.ColorPrimary), PorterDuff.Mode.SRC_IN);
        dialogL.setContentView(view);
        dialogL.setCancelable(true);
        dialogL.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (sender != null && sender.isCancelled() != true) {
                    sender.cancel(false);
                }
                if (deleter != null && deleter.isCancelled() != true) {
                    deleter.cancel(false);
                }
                Snackbar.make(scrollview, getString(R.string.canceled_connaction), Snackbar.LENGTH_INDEFINITE).show();
            }
        });
        contentCount = (TextView) myFragmentView.findViewById(R.id.text_countPing);
        excerptCount = (TextView) myFragmentView.findViewById(R.id.excerpt_countPing);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titlePosting.getText().length()>0&&contentPosting.getText().length()>0) {
                    if (isNetworkConnected() == false) {
                        Snackbar.make(scrollview, getString(R.string.check_network), Snackbar.LENGTH_INDEFINITE).setAction("TRY AGAIN", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                actionButton.callOnClick();
                            }
                        }).show();
                    } else {
                        dialogL.show();
                        String url = account.getUrl();
                        if (key != KEY_NEW_POST) {
                                url = "/" + post.getId();
                            }
                            Post post = new Post();
                            post.setTitle(titlePosting.getText().toString());
                            post.setSlug(slugPosting.getText().toString());
                            post.setContent(contentPosting.getText().toString());
                            post.setExcerpt(excerptPosting.getText().toString());
                            post.setComment_status(commentsPosting.isChecked());
                            post.setSticky(stickyPosting.isChecked());
                            post.setPing_status(pingsPosting.isChecked());
                            sender = new Sender(PostingFragment.this, url,
                                    account.getLogin(), account.getPassword(),
                                    key == KEY_NEW_POST, post, statusSpinner.getSelectedItem().toString(), "");
                            sender.execute();
                    }
                }else {
                    Snackbar.make(scrollview,getString(R.string.bad_title_or_content),Snackbar.LENGTH_LONG).show();
                }
            }
        });
        contentPosting.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                post.setContent(contentPosting.getText().toString());
                contentCount.setText(contentPosting.getText().length() + "");
            }
        });
        excerptPosting.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                post.setExcerpt(excerptPosting.getText().toString());
                excerptCount.setText(excerptPosting.getText().length() + "");
            }
        });
        contEditButtonPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeEditorFragment.initCodeEditor(PostingFragment.this,contentPosting.getText().toString(),KEY_CONTENT, post.getTitle());
                getFragmentManager().beginTransaction().replace(R.id.conteiner, codeEditorFragment).addToBackStack(null).commit();
            }
        });
        excerptEditButtonPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeEditorFragment.initCodeEditor(PostingFragment.this,excerptPosting.getText().toString(),KEY_EXCERPT, post.getTitle());
                getFragmentManager().beginTransaction().replace(R.id.conteiner, codeEditorFragment).addToBackStack(null).commit();
            }
        });
        return myFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        contentPosting.setText(post.getContent());
        excerptPosting.setText(post.getExcerpt());
    }

    public void setPost(Post post, Account account, int key, boolean isPost){
        this.post = post;
        this.key = key;
        this.account = account;
        this.isPost = isPost;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_posting, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pingOpen && key != KEY_NEW_POST) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.getLink()));
            startActivity(browserIntent);
        }
        if (item.getItemId() == R.id.pingDelete) {
            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
            deleteDialog.setTitle(getString(R.string.delete_dialog_title));
            deleteDialog.setMessage(getString(R.string.delete_dialog_message));
            deleteDialog.setPositiveButton(getString(R.string.yes_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (isNetworkConnected() == false) {
                        Snackbar.make(scrollview, getString(R.string.check_network), Snackbar.LENGTH_INDEFINITE).show();
                    } else {
                            if (key == KEY_NEW_POST) {
                                getFragmentManager().popBackStack();
                            } else {
                                dialogL.show();
                            }
                            deleter = new Deleter(PostingFragment.this, account);
                            deleter.execute();
                    }

                }
            });
            deleteDialog.setNegativeButton(getString(R.string.no_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            deleteDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        } else
            return true;
    }

    @Override
    public void OnWorkComplite(String posts, String url, String error) {
        if (error.length() > 0) {
            if (error.equals("Connaction error")) {
                error = getString(R.string.connaction_error);
            }
            Snackbar.make(scrollview, error, Snackbar.LENGTH_INDEFINITE).show();
        } else {
            getFragmentManager().popBackStack();
        }
        dialogL.hide();
    }

    @Override
    public void OnDataChanged(String newData, int key) {
        if (key == KEY_CONTENT){
            post.setContent(newData);
            contentPosting.setText(newData);
        }
        if(key == KEY_EXCERPT){
            post.setExcerpt(newData);
            excerptPosting.setText(newData);
        }
    }
}
