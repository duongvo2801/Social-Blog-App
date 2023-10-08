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
    @SerializedName("price")
    private Object price;

    @SerializedName("image")
    private Image image;

    @SerializedName("description")
    private String description;

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
        price = in.readString();
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
        dest.writeString(String.valueOf(id));
        dest.writeString(name);
        dest.writeString(description);

        dest.writeString(String.valueOf(price));
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
