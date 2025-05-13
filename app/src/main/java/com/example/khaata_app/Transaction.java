package com.example.khaata_app;

public class Transaction {
    int Vid;
    int Cid;
    int Tid;
    String Name;
    String Date;
    String Time;
    int Amount;
    int Send;
    int Receive;

    public  Transaction(){

    }
    public Transaction(int vid, int cid, int tid, String name, String date, String time, int amount,int send,int receive) {
        Vid = vid;
        Cid = cid;
        Tid = tid;
        Name = name;
        Date = date;
        Time = time;
        Amount = amount;
        Receive=receive;
        Send=send;
    }

    public int getVid() {
        return Vid;
    }

    public void setVid(int vid) {
        Vid = vid;
    }

    public int getCid() {
        return Cid;
    }

    public void setCid(int cid) {
        Cid = cid;
    }

    public int getTid() {
        return Tid;
    }

    public void setTid(int tid) {
        Tid = tid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public int getSend() {
        return Send;
    }

    public void setSend(int send) {
        Send = send;
    }

    public int getReceive() {
        return Receive;
    }

    public void setReceive(int receive) {
        Receive = receive;
    }
}
