package com.clr.cityfixer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PostsList extends ArrayAdapter<Post> {

    private Activity context;
    private List<Post> postsList;

    public PostsList(Activity context, List<Post> postsList){
        super(context, R.layout.list_layout, postsList);
        this.context = context;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);
        TextView textViewTitle = (TextView)listViewItem.findViewById(R.id.textViewTitle);
        TextView textViewDescription = (TextView)listViewItem.findViewById(R.id.textViewDescription);

        Post post = postsList.get(position);

        textViewTitle.setText(post.getTitle());
        textViewDescription.setText(post.getDescription());

        return listViewItem;
    }
}
