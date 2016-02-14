package com.visa.vts.certificationapp.model;

/**
 * Created by Manjit.Kaur on 12/8/2015.
 */
public class TransactionData {

        private String transactionname;
        private String tranactionplace;
        private String tranactiontime;
        private String tranactionbill;

        public void TransactionData(){

        }

        public void setName(String transactionname) {
            this.transactionname = transactionname;
        }

        public String getName() {
            return transactionname;
        }

        public void setPlace(String tranactionplace) {
            this.tranactionplace = tranactionplace;
        }

        public String getPlace() {
            return tranactionplace;
        }

        public void setTime(String tranactiontime) {
            this.tranactiontime = tranactiontime;
        }

        public String getTime() {
            return tranactiontime;
        }

        public void setBill(String tranactionbill) {
        this.tranactionbill = tranactionbill;
    }

        public String getBill() {
        return tranactionbill;
    }
    }




