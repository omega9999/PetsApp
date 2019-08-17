package com.example.android.petsapp.db;

import com.example.android.petsapp.db.PetContract.PetEntry;
import com.example.android.petsapp.db.PetContract.PetEntry.Gender;

public class Pet {

    public Pet() {
    }

    public static Pet getDummyInstance(){
        final Pet pet = new Pet();
        pet.setName("Toto").setBreed("Terrier").setGender(PetEntry.GENDER_MALE).setWeight(7);
        return pet;
    }

    public long getId() {
        return mId;
    }

    public Pet setId(long id) {
        this.mId = id;
        return this;
    }

    public String getName() {
        return mName;
    }

    public Pet setName(String name) {
        this.mName = name;
        return this;
    }

    public String getBreed() {
        return mBreed;
    }

    public Pet setBreed(String breed) {
        this.mBreed = breed;
        return this;
    }

    @Gender
    public int getGender() {
        return mGender;
    }


    public Pet setGender(@Gender int gender) {
        this.mGender = gender;
        return this;
    }

    public int getWeight() {
        return mWeight;
    }

    public Pet setWeight(int weight) {
        this.mWeight = weight;
        return this;
    }

    private long mId;
    private String mName;
    private String mBreed;
    private int mGender;
    private int mWeight;
}
