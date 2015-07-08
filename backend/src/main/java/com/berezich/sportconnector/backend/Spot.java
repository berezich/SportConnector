package com.berezich.sportconnector.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by berezkin on 25.06.2015.
 */
@Entity
public class Spot {
    @Id Long id;
    @Index Long regionId;
    String name;
    String address;
    Coordinates coords;
    String price;
    String workHours;
    String contact;
    String description;
    List<Long> partnerLst;
    List<Long> coachLst;
    List<Picture> pictureLst;
    public Spot(){
        pictureLst = new ArrayList<Picture>();
        coachLst = new ArrayList<Long>();
        partnerLst = new ArrayList<Long>();
    }

    public Long getId() {
        return id;
    }

    public Long getRegionId() {
        return regionId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Coordinates getCoords() {
        return coords;
    }

    public String getPrice() {
        return price;
    }

    public String getWorkHours() {
        return workHours;
    }

    public String getContact() {
        return contact;
    }

    public String getDescription() {
        return description;
    }

    public List<Long> getPartnerLst() {

        if(partnerLst==null)
            partnerLst = new ArrayList<Long>();
        return partnerLst;
    }

    public List<Long> getCoachLst() {

        if(coachLst==null)
            coachLst = new ArrayList<Long>();
        return coachLst;
    }

    public List<Picture> getPictureLst() {
        if(pictureLst==null)
            pictureLst = new ArrayList<Picture>();
        return pictureLst;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCoords(Coordinates coords) {
        this.coords = coords;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setWorkHours(String workHours) {
        this.workHours = workHours;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setDescription(String _description) {
        this.description = _description;
    }

    public void setPartnerLst(List<Long> _partnerLst) {
        this.partnerLst = _partnerLst;
    }

    public void setCoachLst(List<Long> _couchLst) {
        this.coachLst = _couchLst;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public void setPictureLst(List<Picture> pictureLst) {
        this.pictureLst = pictureLst;
    }

    @Override
    public String toString() {
        return String.format("id:%d name:%s",id,name);
    }
}
