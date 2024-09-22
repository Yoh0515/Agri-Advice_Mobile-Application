package com.example.finalthesis.Menu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.PlantingGuidance.PDFViewActivity;
import com.example.finalthesis.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SeminarAdapter extends RecyclerView.Adapter<SeminarAdapter.SeminarViewHolder> {

    private Context mContext;
    private List<SeminarData> mSeminarList;

    public SeminarAdapter(Context context, List<SeminarData> seminarList) {
        mContext = context;
        mSeminarList = seminarList;
    }

    @NonNull
    @Override
    public SeminarAdapter.SeminarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_seminar, parent, false);
        return new SeminarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeminarAdapter.SeminarViewHolder holder, int position) {

        SeminarData seminarData = mSeminarList.get(position);
        String formattedTime = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(new Date(seminarData.getTimestamp()));
        holder.date.setText(formattedTime);
        holder.status.setText(seminarData.getRequestStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PDFViewActivity.class);
                intent.putExtra("pdfUrl", seminarData.getPdfUrl());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSeminarList.size();
    }

    public class SeminarViewHolder extends RecyclerView.ViewHolder {

        TextView date, status;

        public SeminarViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
        }
    }
}
