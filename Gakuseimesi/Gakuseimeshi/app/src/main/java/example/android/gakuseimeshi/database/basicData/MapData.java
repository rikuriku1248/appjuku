package example.android.gakuseimeshi.database.basicData;

import java.io.Serializable;

/**
 * Created by Tomu on 2017/12/13.
 */

public class MapData implements Serializable {
    private int id;
    private String name;
    private String nameKana;
    private String address;
    private String tel;
    private String opentime;
    private String holiday;
    private String image;
    private int favorite;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getNameKana() {
        return nameKana;
    }

    public void setNameKana(String nameKana){
        this.nameKana = nameKana;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel){
        this.tel = tel;
    }

    public String getOpentime(){
        return opentime;
    }

    public void setOpentime(String opentime){
        this.opentime = opentime;
    }

    public String getHoliiday() {
        return holiday;
    }

    public void setHoliday(String holiday){
        this.holiday = holiday;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }

    public int getFavorite(){
        return favorite;
    }

    public void setFavorite(int favorite){
        this.favorite = favorite;
    }
}
