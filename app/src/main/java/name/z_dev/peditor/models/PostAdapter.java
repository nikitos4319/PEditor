package name.z_dev.peditor.models;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import name.z_dev.peditor.R;
import name.z_dev.peditor.RandomMaterialColor;

/**
 * Created by Home on 04.08.2015.
 */
public class PostAdapter
        extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private ArrayList<String> mTitles = new ArrayList<String>();
    private ArrayList<String> mURLs = new ArrayList<String>();
    private ArrayList<String> mIDs = new ArrayList<String>();
    private ArrayList<String> mDates = new ArrayList<String>();
    long j=0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;

        public final View mView;
       // public final ImageView mImageView;
        public final TextView mID;
        public final TextView mURL;
        public final TextView mTitle;
        public final TextView mDate;
        public final CircleImageView mImage;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mID = (TextView) view.findViewById(R.id.idPost);
           // mImageView = (ImageView) view.findViewById(R.id.avatarPost);
            mURL = (TextView) view.findViewById(R.id.urlPost);
            mDate = (TextView) view.findViewById(R.id.datePost);
            mTitle = (TextView) view.findViewById(R.id.titlePost);
            mImage = (CircleImageView) view.findViewById(R.id.avatarPost);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText();
        }
    }

    public PostAdapter(Context context, ArrayList<Post> posts) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        for (int i=0;i<posts.size();i++) {
            mTitles.add(posts.get(i).title);
            mURLs.add(posts.get(i).getLink());
            mIDs.add(posts.get(i).getId()+"");
            mDates.add(posts.get(i).getCrDate());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mBoundString = mTitles.get(position);
        holder.mURL.setText(mURLs.get(position));
        holder.mDate.setText(mDates.get(position));
        holder.mID.setText(mIDs.get(position));
        holder.mTitle.setText(mTitles.get(position));
        holder.mImage.setImageDrawable(new ColorDrawable(RandomMaterialColor.getNotRandomMColor(position)));
        holder.mView.setTag(j); j++;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
     //          Intent intent = new Intent(context, CheeseDetailActivity.class);
     //           intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);
     //           context.startActivity(intent);
            }
        });

//        Glide.with(holder.mImageView.getContext())
//                .load(RandomMaterialColor.getRandomMColor())
//                .fitCenter()
//                .into(holder.mImageView);
    }


    @Override
    public int getItemCount() {
        return mIDs.size();
    }
}