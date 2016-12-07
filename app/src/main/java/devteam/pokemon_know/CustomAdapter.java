package devteam.pokemon_know;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ekach on 4/12/2559.
 */

public class CustomAdapter extends ArrayAdapter<String> {

    private Activity mContext;
    private String[] strName;
    private int[] resId;
    private View row;

    public CustomAdapter(Activity mContext, String[] strName, int[] resId) {
        super(mContext,R.layout.favorite_list,strName);
        this.mContext = mContext;
        this.strName = strName;
        this.resId = resId;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater = mContext.getLayoutInflater();

        row = mInflater.inflate(R.layout.favorite_list,null,true);

        TextView textView = (TextView)row.findViewById(R.id.list_row_text);
        textView.setText(strName[position]);

        ImageView imageView = (ImageView)row.findViewById(R.id.list_row_image);
        imageView.setImageResource(resId[position]);

        return row;
    }
}
