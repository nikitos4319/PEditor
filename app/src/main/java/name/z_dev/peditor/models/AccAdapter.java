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
 * Created by Home on 05.08.2015.
 */
public class AccAdapter
        extends RecyclerView.Adapter<AccAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private ArrayList<String> mNames = new ArrayList<String>();
    private ArrayList<String> mURLs = new ArrayList<String>();
    long j = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;

        public final View mView;
        // public final ImageView mImageView;
        public final TextView mName;
        public final TextView mURL;
        public final TextView mLitare;
        public final CircleImageView mImage;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.nameA);
            mURL = (TextView) view.findViewById(R.id.urlA);
            mLitare = (TextView) view.findViewById(R.id.litareA);
            mImage = (CircleImageView) view.findViewById(R.id.litareColorA);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText();
        }
    }

    public AccAdapter(Context context, ArrayList<Account> accs) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        for (int i = 0; i < accs.size(); i++) {
            mNames.add(accs.get(i).name);
            mURLs.add(accs.get(i).url);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mBoundString = mNames.get(position);
        holder.mURL.setText(mURLs.get(position));
        holder.mName.setText(mNames.get(position));
        holder.mImage.setImageDrawable(new ColorDrawable(RandomMaterialColor.getNotRandomMColor(position)));
        if (mNames.get(position).length() > 0) {
            holder.mLitare.setText((mNames.get(position).charAt(0) + "").toUpperCase());}
        holder.mView.setTag(j);
        j++;

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
        return mNames.size();
    }
}