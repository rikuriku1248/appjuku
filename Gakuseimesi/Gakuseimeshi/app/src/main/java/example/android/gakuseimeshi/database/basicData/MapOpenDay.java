package example.android.gakuseimeshi.database.basicData;

/**
 * Created by Tomu on 2017/12/25.
 */

public class MapOpenDay {
    private String name;
    private String opentime1;
    private String opentime2;
    private String opentime3;
    private String holiday;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getOpentime1(){
        return opentime1;
    }

    public void setOpentime1(String opentime1){
        this.opentime1 = opentime1;
    }

    public String getOpentime2(){
        return opentime2;
    }

    public void setOpentime2(String opentime2){
        this.opentime2 = opentime2;
    }

    public String getOpentime3(){
        return opentime3;
    }

    public void setOpentime3(String opentime3){
        this.opentime3 = opentime3;
    }

    public String getHoliday(){
        return holiday;
    }

    public void setHoliday(String holiday){
        this.holiday =  holiday;
    }
}
