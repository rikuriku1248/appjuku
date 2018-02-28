package example.android.gakuseimeshi.database.basicData;

/**
 * Created by Tomu on 2017/12/19.
 */

public class LocationInformation {
    private int id;
    private String name;
    private double latitude;
    private double longitude;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
}
