package com.example.finalthesis.Menu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.SeminarViewHolder> {

    private Context mContext;
    private List<ReportData> reportDataList;

    public ReportAdapter(Context context, List<ReportData> reportList) {
        mContext = context;
        reportDataList = reportList;
    }

    @NonNull
    @Override
    public ReportAdapter.SeminarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_report, parent, false);
        return new SeminarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.SeminarViewHolder holder, int position) {

        ReportData reportData = reportDataList.get(position);
        String formattedTime = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(new Date(reportData.getTimestamp()));
        holder.date.setText(formattedTime);
        holder.status.setText(reportData.getIsResolved());
        holder.subjectreport.setText(reportData.getSubject());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ReportDetails.class);
                intent.putExtra("isResolved", reportData.getIsResolved());
                intent.putExtra("message", reportData.getMessage());
                intent.putExtra("senderName", reportData.getSenderName());
                intent.putExtra("subject", reportData.getSubject());
                intent.putExtra("timestamp", reportData.getTimestamp());
                intent.putExtra("Key", reportData.getKey());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportDataList.size();
    }

    public class SeminarViewHolder extends RecyclerView.ViewHolder {

        TextView date, status, subjectreport;

        public SeminarViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
            subjectreport = itemView.findViewById(R.id.subjectReport);
        }
    }
}
