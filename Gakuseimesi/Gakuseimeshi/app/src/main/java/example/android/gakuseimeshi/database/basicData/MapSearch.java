package example.android.gakuseimeshi.database.basicData;

import java.io.Serializable;

/**
 * Created by Tomu on 2017/12/19.
 */

public class MapSearch implements Serializable{
    private int id;
    private String name;
    private String nameKana;
    private String address;
    private String opentime;
    private String holiday;
    private String categoryName1;
    private String categoryName2;
    private int budget;
    private int studentDiscount;
    private String studentDiscountInfo;
    private int favorite;
    private String searchTime1;
    private String searchTime2;
    private String searchTime3;
    private String searchHolidayDay;
    private String searchHolidayMonth;

    /**
     * コンストラクタ
     */
    public MapSearch(){
    }

    /**
     * contains関数のequals部分のオーバーライド
     * @param object
     * @return
     */
    public boolean equals(Object object) {
        if (object == this) return true;
        if (object.getClass() != this.getClass()) return false;
        MapSearch mapInfo = (MapSearch) object;
        return this.name.equals(mapInfo.getName()) && this.nameKana.equals(mapInfo.getNameKana()) &&
                this.address.equals(mapInfo.getAddress()) && this.opentime.equals(mapInfo.getOpentime()) &&
                this.holiday.equals(mapInfo.getHoliday()) && this.categoryName1.equals(mapInfo.getCategoryName1()) &&
                this.categoryName2.equals(mapInfo.getCategoryName2()) && this.budget == budget &&
                this.studentDiscount == studentDiscount && this.studentDiscountInfo.equals(mapInfo.getStudentDiscountInfo()) &&
                this.favorite == favorite && this.searchTime1.equals(mapInfo.getSearchTime1()) &&
                this.searchTime2.equals(mapInfo.getSearchTime2()) && this.searchTime3.equals(mapInfo.getSearchTime3()) &&
                this.searchHolidayDay.equals(mapInfo.getSearchHolidayDay()) && this.searchHolidayMonth.equals(mapInfo.getSearchHolidayMonth());
    }


    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getNameKana(){
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

    public String getOpentime(){
        return opentime;
    }

    public void setOpentime(String opentime){
        this.opentime = opentime;
    }

    public String getHoliday(){
        return holiday;
    }

    public void setHoliday(String holiday){
        this.holiday = holiday;
    }

    public String getCategoryName1(){
        return categoryName1;
    }

    public void setCategoryName1(String categoryName1){
        this.categoryName1 = categoryName1;
    }

    public String getCategoryName2(){
        return categoryName2;
    }

    public void setCategoryName2(String categoryName2){
        this.categoryName2 = categoryName2;
    }

    public int getBudget(){
        return budget;
    }

    public void setBudget(int budget){
        this.budget = budget;
    }

    public int getStudentDiscount(){
        return studentDiscount;
    }

    public void setStudentDiscount(int studentDiscount){
        this.studentDiscount = studentDiscount;
    }

    public String getStudentDiscountInfo(){
        return studentDiscountInfo;
    }

    public void setStudentDiscountInfo(String studentDiscountInfo){
        this.studentDiscountInfo = studentDiscountInfo;
    }

    public int getFavorite(){
        return favorite;
    }

    public void setFavorite(int favorite){
        this.favorite = favorite;
    }

    public String getSearchTime1(){
        return searchTime1;
    }

    public void setSearchTime1(String searchTime1){
        this.searchTime1 = searchTime1;
    }

    public String getSearchTime2(){
        return searchTime2;
    }

    public void setSearchTime2(String searchTime2){
        this.searchTime2 = searchTime2;
    }

    public String getSearchTime3(){
        return searchTime3;
    }

    public void setSearchTime3(String searchTime3){
        this.searchTime3 = searchTime3;
    }

    public String getSearchHolidayDay(){
        return searchHolidayDay;
    }

    public void setSearchHolidayDay(String searchHolidayDay){
        this.searchHolidayDay = searchHolidayDay;
    }

    public String getSearchHolidayMonth(){
        return searchHolidayMonth;
    }

    public void setSearchHolidayMonth(String searchHolidayMonth){
        this.searchHolidayMonth = searchHolidayMonth;
    }


}
