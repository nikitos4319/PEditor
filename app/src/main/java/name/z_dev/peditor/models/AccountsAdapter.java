package name.z_dev.peditor.models;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import name.z_dev.peditor.R;
import name.z_dev.peditor.RandomMaterialColor;

/**
 * Created by D.Krylov on 03.08.2015.
 */
public class AccountsAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<Account> accs;

    // кол-во элементов
    @Override
    public int getCount() {
        return accs.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return accs.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }


    public AccountsAdapter(Context context, ArrayList<Account> accs) {
        this.context = context;
        this.accs = accs;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_account, parent, false);
        }

        Account p = accs.get(position);

        ((TextView) view.findViewById(R.id.nameA)).setText(p.name);
        ((TextView) view.findViewById(R.id.urlA)).setText(p.url);
        ((TextView) view.findViewById(R.id.litareA)).setText((p.name.charAt(0)+"").toUpperCase());
        ((CircleImageView) view.findViewById(R.id.litareColorA))
                .setImageDrawable(new ColorDrawable(RandomMaterialColor.getNotRandomMColor(position)));

        return view;
    }

}
