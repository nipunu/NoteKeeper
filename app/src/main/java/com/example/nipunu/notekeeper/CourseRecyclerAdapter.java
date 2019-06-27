package com.example.nipunu.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nipunu on 24,June,2019
 */
public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {

    private final Context context;
    private final LayoutInflater layoutInflater;
    private final List<CourseInfo> courses;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> courses) {
        this.context = context;
        layoutInflater = LayoutInflater.from(this.context);
        this.courses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = layoutInflater.inflate(R.layout.item_course_list, viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CourseInfo course = courses.get(position);
        viewHolder.textCourse.setText(course.getTitle());
//        viewHolder.textTitle.setText(note.getTitle());
        viewHolder.currentPosition = position;
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView textCourse;
//        public final TextView textTitle;
        public int currentPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse = (TextView) itemView.findViewById(R.id.text_course);
//            textTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v,courses.get(currentPosition).getTitle(),Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
