package com.pecake.paper;

public class Faq {

    private String q;
    private String ans;


    public Faq(String q, String ans) {
        this.q = q;
        this.ans = ans;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }
}
