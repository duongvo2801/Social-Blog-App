package com.example.client.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Product implements Parcelable {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    //new category
    @SerializedName("category")
    private Category category;
    @SerializedName("price")
    private Object price;

    @SerializedName("image")
    private Image image;

    @SerializedName("description")
    private String description;

//    @SerializedName("imageBase64")
//    private String imageBase64;
//
//
//    public String getImageBase64() {
//        return imageBase64;
//    }

//    public void setImageBase64(String imageBase64) {
//        this.imageBase64 = imageBase64;
//    }

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Object getPrice() {
        return price;
    }

    public void setPrice(Object price) {
        this.price = price;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected Product(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        // Assuming you will change the Object type for price and image to appropriate types in the future.
        // For now, I'm using readString for simplicity. Adjust as needed.
        price = in.readString();
//        image = in.readString();
//        image = in.createByteArray();
        image = in.readParcelable(Image.class.getClassLoader());
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
//        dest.writeString(id);
        dest.writeString(String.valueOf(id));
        dest.writeString(name);
        dest.writeString(description);

//        dest.writeString((String) price);
        dest.writeString(String.valueOf(price));
//        dest.writeString((String) image);
//        dest.writeByteArray(image);
        dest.writeParcelable(image, i);
    }
    public static class Image implements Parcelable {
        private String type;
        @SerializedName("data")
        private List<Integer> data;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Integer> getData() {
            return data;
        }

        public void setData(List<Integer> data) {
            this.data = data;
        }

        public byte[] getDataAsByteArray() {
            byte[] byteArray = new byte[data.size()];
            for (int i = 0; i < data.size(); i++) {
                byteArray[i] = data.get(i).byteValue();
            }
            return byteArray;
        }
        public Image() {
            // Empty constructor
        }

        public Image(Parcel in) {
            type = in.readString();
            data = new ArrayList<>();
            in.readList(data, Integer.class.getClassLoader());
        }
        public void setDataAsByteArray(byte[] imageData) {
            this.data = new ArrayList<>();
            for (byte b : imageData) {
                // Convert each byte to an integer (considering byte as unsigned) and add to the list
                this.data.add(b & 0xFF);
            }
        }


        public static final Creator<Image> CREATOR = new Creator<Image>() {
            @Override
            public Image createFromParcel(Parcel in) {
                return new Image(in);
            }

            @Override
            public Image[] newArray(int size) {
                return new Image[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(type);
            dest.writeList(data);
        }
    }
}
