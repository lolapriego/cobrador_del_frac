package com.lolapau.cobradordelfrac.parser.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.lolapau.cobradordelfrac.types.Debt;


/**
 * @date April 27, 2013
 * @author Lola Priego (md.priego@gmail.com)
 *
 */
public class DebtParser {

    public Debt parse(JSONObject json) throws JSONException {
        
        Debt obj = new Debt();
        if (json.has("user_debtor_id")) {
            obj.setDebtorId(json.getString("user_debtor_id"));
        } 
        if (json.has("user_creditor_id")) {
            obj.setCreditorId(json.getString("user_creditor_id"));
        } 
        if (json.has("quantity")) {
            obj.setQuantity(json.getDouble("quantity"));
        } 
        if (json.has("comments")) {
            obj.setComments(json.getString("comments"));
        } 
        
        return obj;
    }
}