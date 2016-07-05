package com.example.chienhua.chatroom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by chienhua on 2016/7/5.
 */
public class ChatRoomAdapter extends ArrayAdapter<DataStruct> {
    private Context context;
    private ArrayList<DataStruct> data;
    private ViewHolder holder;

    static class ViewHolder {
        ImageView userPhoto, messagePhoto;
        TextView name, content;
    }

    private void ComponentInit(View view, ViewGroup group) {
        holder.userPhoto = (ImageView) view.findViewById(R.id.imageView3);
        holder.messagePhoto = (ImageView) view.findViewById(R.id.imageView2);
        holder.name = (TextView) view.findViewById(R.id.textView4);
        holder.content = (TextView) view.findViewById(R.id.textView3);
    }

    private void setMessagePhoto(int position) {
        new AsyncTask<String, Void, Bitmap>() {


            @Override
            protected Bitmap doInBackground(String... params) {
                URL url = null;
                Bitmap bitmap = null;
                try {
                    url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                holder.messagePhoto.setImageBitmap(bitmap);
                notifyDataSetChanged();
                super.onPostExecute(bitmap);
            }
        }.execute(data.get(position).messagePhoto);
    }

    public ChatRoomAdapter(Context context, ArrayList<DataStruct> data) {
        super(context, R.layout.list, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list, parent, false);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        ComponentInit(convertView, parent);
        holder.name.setText(data.get(position).name);
        holder.content.setText(data.get(position).message);
        if (!data.get(position).messagePhoto.equals("")) {
            holder.messagePhoto.setVisibility(View.VISIBLE);
            AsyncImageLoader imageLoader = new AsyncImageLoader(context);
            Bitmap bitmap = imageLoader.loadImage(holder.messagePhoto, data.get(position).messagePhoto);
            if (bitmap != null) {
                holder.messagePhoto.setImageBitmap(bitmap);
            }
//            setMessagePhoto(position);
        } else {
            holder.messagePhoto.setVisibility(View.GONE);
        }

        return convertView;
    }
}
