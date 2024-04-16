package com.example.advancedautomation;

public class SubRegiao extends Regiao{
    private Regiao mainRegiao;

    public SubRegiao(String name, double latitude, double longitude, int user,Regiao mainRegiao) {
        super(name, latitude, longitude, user);
        this.mainRegiao = mainRegiao;
    }

    public Regiao getMainRegion() {
        return mainRegiao;
    }

    public void setMainRegion(Regiao mainRegion) {
        this.mainRegiao = mainRegion;
    }

    @Override
    public String toString() {
        return "SubRegion{" +
                "mainRegion=" + mainRegiao.getName() +
                ", name='" + getName() + '\'' +
                ", latitude=" + getLatitude() +
                ", longitude=" + getLongitude() +
                ", user=" + getUser() +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
