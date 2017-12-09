package com.exoncloud.mobi.distributeapp.model;

/**
 * Created by brad on 2017/02/11.
 */

public class Voucher {

    private String invoiceId;
    private String invoiceDate;
    private String invoiceTotal;
    private String invoicePaid;
    private String invoiceDue;
    private String invoiceAge;
    private String invoiceCustomer;
    private int invoiceIdInt;

    public Voucher(){};

    public Voucher(String invoiceId, String invoiceDate, String invoiceTotal, String invoicePaid, String invoiceDue, String invoiceAge, String invoiceCustomer, int invoiceIdInt) {
        this.invoiceId = invoiceId;
        this.invoiceDate = invoiceDate;
        this.invoiceTotal = invoiceTotal;
        this.invoicePaid = invoicePaid;
        this.invoiceDue = invoiceDue;
        this.invoiceAge = invoiceAge;
        this.invoiceCustomer = invoiceCustomer;
        this.invoiceIdInt = invoiceIdInt;
    }

    public int getInvoiceIdInt() {
        return invoiceIdInt;
    }

    public void setInvoiceIdInt(int invoiceIdInt) {
        this.invoiceIdInt = invoiceIdInt;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(String invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    public String getInvoicePaid() {
        return invoicePaid;
    }

    public void setInvoicePaid(String invoicePaid) {
        this.invoicePaid = invoicePaid;
    }

    public String getInvoiceDue() {
        return invoiceDue;
    }

    public void setInvoiceDue(String invoiceDue) {
        this.invoiceDue = invoiceDue;
    }

    public String getInvoiceAge() {
        return invoiceAge;
    }

    public void setInvoiceAge(String invoiceAge) {
        this.invoiceAge = invoiceAge;
    }

    public String getInvoiceCustomer() {
        return invoiceCustomer;
    }

    public void setInvoiceCustomer(String invoiceCustomer) {
        this.invoiceCustomer = invoiceCustomer;
    }
}
