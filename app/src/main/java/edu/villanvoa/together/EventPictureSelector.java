package edu.villanvoa.together;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by aodell on 3/16/15.
 */
public class EventPictureSelector {
    private ArrayList<Integer> imgOptions;
    private Random randomGen;
    private int img;

    public EventPictureSelector() {
        imgOptions = new ArrayList<Integer>();
        randomGen = new Random();

        imgOptions.add(R.drawable.bar);
        imgOptions.add(R.drawable.basketball);
        imgOptions.add(R.drawable.bowl);
        imgOptions.add(R.drawable.club);
        imgOptions.add(R.drawable.concert);
        imgOptions.add(R.drawable.dinner);
        imgOptions.add(R.drawable.movie);
        imgOptions.add(R.drawable.picnic);
        imgOptions.add(R.drawable.roadtrip);
        imgOptions.add(R.drawable.shop);
        imgOptions.add(R.drawable.ski);
        imgOptions.add(R.drawable.smores);

        chooseRandom();
    }

    private void chooseRandom() {
        img = imgOptions.get(randomGen.nextInt(imgOptions.size()));
    }

    public int getImgResource() {
        return img;
    }
}
