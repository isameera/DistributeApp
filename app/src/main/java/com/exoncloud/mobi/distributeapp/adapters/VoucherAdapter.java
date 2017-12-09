package com.exoncloud.mobi.distributeapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.exoncloud.mobi.distributeapp.R;
import com.exoncloud.mobi.distributeapp.Receipt;
import com.exoncloud.mobi.distributeapp.model.Voucher;

import java.util.ArrayList;

/**
 * Created by Sameera on 2017/09/03.
 */

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherHolder> {

    private ArrayList<Voucher> mData;
    private Activity mACtivity;
    Context mContext;

    public VoucherAdapter(ArrayList<Voucher> data, Activity activity, Context context) {
        this.mData = data;
        this.mACtivity = activity;
        this.mContext = context;
    }

    @Override
    public VoucherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_voucher, parent, false);
        return new VoucherHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(VoucherHolder holder, int position) {
        Voucher voucher = mData.get(position);

        holder.setInvoiceId(voucher.getInvoiceId());
        holder.setInvoiceDate(voucher.getInvoiceDate());
        holder.setInvoiceTotal(voucher.getInvoiceTotal());
        holder.setInvoicePaid(voucher.getInvoicePaid());
        holder.setInvoiceDue(voucher.getInvoiceDue());
        holder.setInvoiceAge(voucher.getInvoiceAge());

        Glide.with(mContext)
                .load(voucher.getInvoiceId());
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    public class VoucherHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        TextView invoiceId;
        TextView invoiceDate;
        TextView InvoiceTotal;
        TextView invoicePaid;
        TextView InvoiceDue;
        TextView InvoiceAge;

        public VoucherHolder(View itemView, final Context context) {
            super(itemView);
            mContext = context;

            invoiceId = (TextView) itemView.findViewById(R.id.invoice_id);
            invoiceDate = (TextView) itemView.findViewById(R.id.invoice_date);
            InvoiceTotal = (TextView) itemView.findViewById(R.id.invoice_total);
            invoicePaid = (TextView) itemView.findViewById(R.id.invoice_paid);
            InvoiceDue = (TextView) itemView.findViewById(R.id.invoice_due);
            InvoiceAge = (TextView) itemView.findViewById(R.id.invoice_age);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Voucher voucher = mData.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, Receipt.class);

                    Receipt.invoice_id_static = Integer.valueOf(voucher.getInvoiceIdInt());
                    intent.putExtra("invoice_id", voucher.getInvoiceId());
                    intent.putExtra("invoice_date", voucher.getInvoiceDate());
                    intent.putExtra("invoice_age", voucher.getInvoiceAge());
                    intent.putExtra("invoice_total", voucher.getInvoiceTotal());
                    intent.putExtra("invoice_paid", voucher.getInvoicePaid());
                    intent.putExtra("invoice_due", voucher.getInvoiceDue());
                    intent.putExtra("invoice_customer", voucher.getInvoiceCustomer());
                    context.startActivity(intent);

//                    Toast.makeText(mContext, "aaaaaaaaaaaaa"+voucher.getInvoiceId(), Toast.LENGTH_SHORT).show();

                }
            });

        }

        public void setInvoiceId(String invoiceId) {
            this.invoiceId.setText(invoiceId);
        }

        public void setInvoiceDate(String invoiceDate) {
            this.invoiceDate.setText(invoiceDate);
        }

        public void setInvoiceTotal(String InvoiceTotal) {
            this.InvoiceTotal.setText(InvoiceTotal);
        }

        public void setInvoicePaid(String invoicePaid) {
            this.invoicePaid.setText(invoicePaid);
        }

        public void setInvoiceDue(String InvoiceDue) {
            this.InvoiceDue.setText(InvoiceDue);
        }

        public void setInvoiceAge(String InvoiceAge) {
            this.InvoiceAge.setText(InvoiceAge);
        }

        @Override
        public void onClick(View view) {



        }
    }
}
