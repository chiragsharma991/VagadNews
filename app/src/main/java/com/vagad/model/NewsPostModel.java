package com.vagad.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.vagad.utils.AppUtils;
import com.vagad.utils.DateUtils;

import java.util.Calendar;

// [START blog_user_class]
@IgnoreExtraProperties
public class NewsPostModel {

    public String nameReporter;
    public String newsTitle;
    public String newsDesc;
    public String image;
    public boolean isVisible;
    public long timestamp;
    public String date;
    public String uniqueId;
    public String mobileNo;

    public NewsPostModel() {}

    public NewsPostModel(String nameReporter, String newsDesc, String newsTitle,
                         String image, boolean isVisible, String uniqueId, String mobileNo) {
        this.nameReporter = nameReporter;
        this.newsTitle = newsTitle;
        this.newsDesc = newsDesc;
        this.image = image;
        this.isVisible = isVisible;
        timestamp = Calendar.getInstance().getTimeInMillis();
        date = DateUtils.getDate(Calendar.getInstance().getTimeInMillis());
        this.uniqueId = uniqueId;
        this.mobileNo = mobileNo;
    }

}

