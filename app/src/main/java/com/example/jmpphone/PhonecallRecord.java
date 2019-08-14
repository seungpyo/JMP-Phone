package com.example.jmpphone;

public class PhonecallRecord {

    String myNumber;
    String number;
    String name;
    long start, end;
    boolean isIncoming, isMissed;

    public PhonecallRecord(){}

    public void setName(String name) {
        this.name = name;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setIncoming(boolean incoming) {
        isIncoming = incoming;
    }

    public void setMissed(boolean missed) {
        isMissed = missed;
    }

    public void setMyNumber(String myNumber) {
        this.myNumber = myNumber;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setStart(long start) {
        this.start = start;
    }

    @Override
    public String toString() {
        String repr = "?";
        repr += "name=" + name + "&";
        repr += "start=" + start + "&";
        repr += "end=" + end + "&";
        repr += "number=" + number + "&";
        repr += "mynumber=" + myNumber + "&";
        repr += "ismissed=" + isMissed + "&";
        repr += "isincoming=" + isIncoming;
        return repr;
    }

}
