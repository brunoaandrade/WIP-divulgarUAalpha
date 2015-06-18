package addProject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.List;

import deti.ua.main.R;


/**
 * Created by bruno on 31/05/2015.
 */
public class CheeseDynamicAdapter extends BaseDynamicGridAdapter {
    List<?> description;
    List<Bitmap> pics;
    public CheeseDynamicAdapter(Context context, List<?> items, int columnCount,List<?>description,List<Bitmap>pics) {
        super(context, items, columnCount);
        this.description=description;
        this.pics=pics;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheeseViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_grid, null);
            holder = new CheeseViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CheeseViewHolder) convertView.getTag();
        }
        holder.build(getItem(position).toString(),description.get(position).toString(), pics.get(position));
        return convertView;
    }

    private class CheeseViewHolder {
        private TextView titleText;
        private TextView descriptionText;
        private ImageView image;

        private CheeseViewHolder(View view) {
            titleText = (TextView) view.findViewById(R.id.item_title);
            descriptionText = (TextView) view.findViewById(R.id.item_description);
            image = (ImageView) view.findViewById(R.id.item_img);
        }

        void build(String title,String description,Bitmap bit) {
            descriptionText.setText(description);
            image.setImageBitmap(bit);
            title=title.substring(title.lastIndexOf('/')+1).replace("_edited","");
            titleText.setText(title);
        }

    }

}