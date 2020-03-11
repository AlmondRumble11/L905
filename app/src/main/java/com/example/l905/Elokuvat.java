package com.example.l905;

public class Elokuvat{
    private String teatteri;
    private String elokuva;
    private String aika;
    public  Elokuvat(String t, String e, String aika){
        this.teatteri=t;
        this.elokuva=e;
        this.aika=aika;
    }

    public String getAika() {
        return aika;
    }

    public String getTeatteri() {
        return teatteri;
    }

    public String getElokuva() {
        return elokuva;
    }
}
