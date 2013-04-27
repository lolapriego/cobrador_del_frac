package com.lolapau.cobradordelfrac.parser.json;

import com.lolapau.cobradordelfrac.types.Debt;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @date April 27, 2013
 * @author Lola Priego (md.priego@gmail.com)
 *
 */
public class DebtParser {

    public Debt parse(JSONObject json) throws JSONException {
        
        Debt obj = new Debt();
        if (json.has("debtor_id")) {
            obj.setDebtorId(json.getString("debtor_id"));
        } 
        if (json.has("creditor_id")) {
            obj.setCreditorId(json.getString("creditor_id"));
        } 
        if (json.has("quantity")) {
            obj.setQuantity(json.getString("quantity"));
        } 
        if (json.has("comments")) {
            obj.setComments(json.getString("comments"));
        } 
        
        return obj;
    }
}