package com.galenassignment.methods;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class getEventDetails {

	public static String getSortQuery(String sortBy) {
		String sortByStr = " order by ";
		String sortByArr[] = sortBy.split("-");
		sortByStr += sortByArr[0] + " " + sortByArr[1];
		return sortByStr;
	}

	public static String getFilterQuery(String filterBy) {
		String[] filterArray = filterBy.split(";");
		String filterByStr = " where location in ( ";
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < filterArray.length; i++) {
			builder.append("?,");
		}
		filterByStr += builder.deleteCharAt(builder.length() - 1).toString() + " )";
		return filterByStr;
	}

	public static JSONArray getEventDetailsWithParams(String offset, String limit, String sortBy, String filterBy) {
		JSONArray array = new JSONArray();
		try {

			String query = "SELECT name, startdate , enddate, location, website from eventinfo ";
			String sortByStr = " order by ";
			String limitStr = " limit " + limit;
			String offsetStr = " offset " + offset;
			String filterByStr = " where location in ( ";

			if (filterBy != null) {
				query += getFilterQuery(filterBy);
			}
			if (sortBy != null) {
				query += getSortQuery(sortBy);
			}
			if (limit != null) {
				query += limitStr;
			}
			if (offset != null) {
				query += offsetStr;
			}

			Connection con = DatabaseConnection.getConnection();
			PreparedStatement ps = con.prepareStatement(query);

			System.out.println(query);
			int j = 1;
			if (filterBy != null) {
				String[] filterArray = filterBy.split(";");
				for (int i = 0; i < filterArray.length; i++) {
					ps.setString(j, filterArray[i]);
					j++;
				}
			}

			ResultSet rs = ps.executeQuery();
			JSONObject eventObj;
			int count = 0;
			while (rs.next()) {
				eventObj = new JSONObject();
				eventObj.put("name", rs.getString("name"));
				eventObj.put("startdate", rs.getString("startdate"));
				eventObj.put("enddate", rs.getString("enddate"));
				eventObj.put("location", rs.getString("location"));
				eventObj.put("website", rs.getString("website"));
				array.put(eventObj);
			}
			System.out.println("Response array :" + array.toString());
		} catch (Exception e) {
			System.out.println(e);
		}

		return array;

	}

	public static JSONObject getLocations() {
		ArrayList<String> locations = new ArrayList<String>();
		JSONObject locationObj = new JSONObject();
		try {
			String query = "SELECT location from eventinfo";
			Connection con = DatabaseConnection.getConnection();
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				locations.add(rs.getString("location"));
			}
			locationObj.put("location", locations);
		} catch (Exception e) {
			System.out.println(e);
		}
		return locationObj;
	}

}
