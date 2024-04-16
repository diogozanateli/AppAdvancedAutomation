package com.example.advancedautomation;

public class RestrictedRegiao extends Regiao{
    private Regiao mainRegion;
    private boolean restricted;

    public RestrictedRegiao(String name, double latitude, double longitude, int user, Regiao mainRegion,boolean restricted) {
        super(name, latitude, longitude, user);
        this.mainRegion = mainRegion;
        this.restricted = restricted;
    }

    public Regiao getMainRegion() {
        return mainRegion;
    }

    public void setMainRegion(Regiao mainRegion) {
        this.mainRegion = mainRegion;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    @Override
    public String toString() {
        return "RestrictedRegion{" +
                "mainRegion=" + mainRegion.getName() +
                ", name='" + getName() + '\'' +
                ", latitude=" + getLatitude() +
                ", longitude=" + getLongitude() +
                ", user=" + getUser() +
                ", timestamp=" + getTimestamp() +
                ", restricted=" + restricted +
                '}';
    }
}
