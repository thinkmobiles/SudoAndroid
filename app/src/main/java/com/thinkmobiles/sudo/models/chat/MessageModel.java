package com.thinkmobiles.sudo.models.chat;

import com.thinkmobiles.sudo.models.chat.CompanionModel;

import java.io.Serializable;

/**
 * Created by njakawaii on 07.05.2015.
 */
public class MessageModel implements Serializable{
    private String body;
    private String postedDate;
    private CompanionModel companion;
    private CompanionModel owner;
    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }



    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public CompanionModel getCompanion() {
        return companion;
    }

    public void setCompanion(CompanionModel companion) {
        this.companion = companion;
    }

    public CompanionModel getOwner() {
        return owner;
    }

    public void setOwner(CompanionModel owner) {
        this.owner = owner;
    }
}
