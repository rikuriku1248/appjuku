package example.android.gakuseimeshi.database.basicData;

/**
 * Created by Tomu on 2018/02/23.
 */

public class ReviewData {
    private int id;
    private String name;
    private String comment;
    private String image;

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String getComment(){
        return comment;
    }

    public void setImage(String image){
        this.image = image;
    }

    public String getImage(){
        return image;
    }
}
