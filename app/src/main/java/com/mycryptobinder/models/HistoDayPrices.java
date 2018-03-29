/*
 * Copyright (c) 2018 by Yann39.
 *
 * This file is part of MyCryptoBinder.
 *
 * MyCryptoBinder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCryptoBinder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCryptoBinder. If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycryptobinder.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoDayPrices {

    @SerializedName("Response")
    private String response;

    @SerializedName("Message")
    private String message;

    @SerializedName("Type")
    private String type;

    @SerializedName("Aggregated")
    private String aggregated;

    @SerializedName("Data")
    private List<HistoDayPrice> data;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAggregated() {
        return aggregated;
    }

    public void setAggregated(String aggregated) {
        this.aggregated = aggregated;
    }

    public List<HistoDayPrice> getData() {
        return data;
    }

    public void setData(List<HistoDayPrice> data) {
        this.data = data;
    }
}
