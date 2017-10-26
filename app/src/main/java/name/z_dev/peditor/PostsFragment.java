package name.z_dev.peditor;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import name.z_dev.peditor.loader.ListenerWeakReferenceL;
import name.z_dev.peditor.loader.Loader;
import name.z_dev.peditor.models.Account;
import name.z_dev.peditor.models.Post;
import name.z_dev.peditor.models.PostAdapter;
import name.z_dev.peditor.models.RecyclerItemClickListener;

import static name.z_dev.peditor.models.Constants.KEY_EDIT_POST;
import static name.z_dev.peditor.models.Constants.KEY_NEW_POST;

/**
 * Created by Nikita on 11.03.2017.
 */

public class PostsFragment extends Fragment implements ListenerWeakReferenceL {

    boolean isPost, isUseAuth;
    Account account;
    Dialog dialog;
    Loader loader;
    RecyclerView recyclerView;
    Toolbar toolbar;
    FloatingActionButton addPBut;
    PostingFragment postingFragment;
    ArrayList<Post> posts = new ArrayList<Post>();
    ArrayList<Post> postSearch = new ArrayList<Post>();
    PostAdapter adapter;
    SearchView searchView;

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public PostsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_posts, container, false);
        toolbar = (Toolbar) myFragmentView.findViewById(R.id.toolbarP);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.posts));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        getActivity().setTitle(getString(R.string.post_editor));
        postingFragment = new PostingFragment();
        addPBut = (FloatingActionButton) myFragmentView.findViewById(R.id.fabPA);
        addPBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = new Post();
                postingFragment.setPost(post, account, KEY_NEW_POST, isPost);
                getFragmentManager().beginTransaction().replace(R.id.conteiner, postingFragment).addToBackStack(null).commit();
            }
        });
        recyclerView = (RecyclerView) myFragmentView.findViewById(R.id.recyclerview);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(searchView.isShown())
                    postingFragment.setPost(postSearch.get(position), account, KEY_EDIT_POST, isPost);
                else
                    postingFragment.setPost(posts.get(position), account, KEY_EDIT_POST, isPost);
                getFragmentManager().beginTransaction().replace(R.id.conteiner, postingFragment).addToBackStack(null).commit();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        adapter = new PostAdapter(getActivity(), postSearch);
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
        setHasOptionsMenu(true);
        return myFragmentView;
    }

    public void setAccount(Account account, boolean post, boolean useAuth){
        this.account = account;
        this.isPost = post;
        this.isUseAuth = useAuth;
    }

    void initDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.progress_dialog, null);
        ((ProgressBar) view.findViewById(R.id.progressBar))
                .getIndeterminateDrawable()
                .setColorFilter(getResources().getColor(R.color.ColorPrimary), PorterDuff.Mode.SRC_IN);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loader != null && loader.isCancelled() != true) {
                    loader.cancel(false);
                }
                Snackbar.make(recyclerView, getString(R.string.canceled_connaction), Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }

    void refreshPosts() {
        if (isNetworkConnected() == false) {
            Snackbar.make(recyclerView, getString(R.string.check_network), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.refresh_button), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshPosts();
                }
            }).show();
        } else {
                if (account != null) {
//                    String urls = account.getUrl();
//                    if(urls.toLowerCase().equals("/posts") != true && urls.toLowerCase().equals("/pages") != true) {
//                        if (isPost) {
//                            if (urls.charAt(urls.length() - 1) == '/') urls += "/";
//                            urls += "posts";
//                        } else {
//                            if (urls.charAt(urls.length() - 1) != '/') urls += "/";
//                            urls += "pages";
//                        }
//                    }
//                    account.setUrl(urls);
                    initDialog();
                    dialog.show();
                    loader = new Loader(PostsFragment.this, account, false);
                    loader.execute();
                } else {
                    getFragmentManager().popBackStack();
                }
        }
    }

    void searchPosts(String query) {
        postSearch.clear();
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            if ((post.getId() + "").toLowerCase().indexOf(query.toLowerCase()) != -1) {
                postSearch.add(post);
            } else {
                if ((post.getTitle() + "").toLowerCase().indexOf(query.toLowerCase()) != -1) {
                    postSearch.add(post);
                } else {
                    if ((post.getCrDate() + "").toLowerCase().indexOf(query.toLowerCase()) != -1) {
                        postSearch.add(post);
                    }
                }
            }
        }
        adapter = new PostAdapter(getActivity(), postSearch);
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void OnWorkComplite(ArrayList<Post> posts, String errorMes) {
        if (errorMes.length() > 0) {
            if (errorMes.equals("Connaction error")) {
                errorMes = getString(R.string.connaction_error);
            }
            Snackbar.make(recyclerView, errorMes, Snackbar.LENGTH_INDEFINITE).show();
        } else {
            this.posts = posts;
            if (searchView.isShown()) {
                searchPosts(searchView.getQuery().toString());
            } else {
                adapter = new PostAdapter(getActivity(), posts);
                recyclerView.setAdapter(null);
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                recyclerView.setAdapter(adapter);
            }
        }
        dialog.hide();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_posts, menu);
        MenuItem menuItem = menu.findItem(R.id.searchPosts);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search));
        ImageView mSearchButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        ImageView mCloseButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        mSearchButton.setImageResource(R.drawable.ic_search_white_24dp);
        mCloseButton.setImageResource(R.drawable.ic_clear_white_24dp);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter = new PostAdapter(getActivity(), posts);
                recyclerView.setAdapter(null);
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                recyclerView.setAdapter(adapter);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchPosts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refreshPs) {
            refreshPosts();
        }
        return super.onOptionsItemSelected(item);
    }
}
