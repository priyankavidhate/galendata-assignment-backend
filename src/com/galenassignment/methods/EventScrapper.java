package com.galenassignment.methods;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EventScrapper {
	/*
	 * name of website eventname event Date Location
	 */

	public ArrayList<Map<String, String>> parseHTMLTable() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		try {
			Document doc = Jsoup.connect(
					"https://www.computerworld.com/article/3313417/tech-event-calendar-2020-upcoming-shows-conferences-and-it-expos.html")
					.get();

			Element table = doc.select("table").get(0); // select the first table.
			Elements rows = table.select("tr");
			Iterator<Element> rowIterator = rows.iterator();
			rowIterator.next();
			boolean wasMatch = false;
			while (rowIterator.hasNext()) {
				Map<String, String> m = new HashMap<String, String>();
				Element row = rowIterator.next();
				Elements cols = row.select("td");
				Elements th = row.select("th");

				m.put("eventWebsite", th.select("a").attr("href"));
				m.put("eventName", th.get(0).text());
				m.put("eventStartDate", cols.get(1).text());
				m.put("eventEndDate", cols.get(2).text());
				m.put("eventLocation", cols.get(3).text());
				list.add(m);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<Map<String, String>> parseDivClass() {
		Document doc;
		Date d = new Date();
		LocalDate currentDate = LocalDate.now();
		int currentYear = currentDate.getYear();
		LocalDate startDate, endDate;
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		try {
			doc = Jsoup.connect("https://www.techmeme.com/events").get();
			Elements rows = doc.select("div.rhov > a");
			for (Element element : rows) {
				Map<String, String> m = new HashMap<String, String>();				
				String eventDates[] = element.select("div").get(0).ownText().split("-");

				if (eventDates.length == 0) {
					continue;
				}
				String startDateStr = eventDates[0] + " " + currentYear;
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
				startDate = LocalDate.parse(startDateStr, formatter);
				endDate = startDate;
				if (eventDates.length == 1) {
					continue;
				}
				if (eventDates[1].split(" ").length > 1) {
					String endDateStr = eventDates[1] + " " + currentYear;
					endDate = LocalDate.parse(endDateStr, formatter);
				} else {
					String endDateStr = startDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + " "
							+ eventDates[1] + " " + currentYear;
					endDate = LocalDate.parse(endDateStr, formatter);
				}
				m.put("eventWebsite", element.attr("href").replace("/gotos/", ""));
				m.put("eventName", element.select("div").get(1).ownText());
				m.put("eventStartDate", startDate.toString());
				m.put("eventEndDate", endDate.toString());
				m.put("eventLocation", element.select("div").get(2).ownText());
				list.add(m);

			}
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return list;
	}

	public static void main(String[] args) {
		ArrayList<Map<String, String>> finalEventList = new ArrayList<Map<String, String>>();

		EventScrapper d = new EventScrapper();
		ArrayList<Thread> threadArr = new ArrayList<Thread>();
		threadArr.add(new Thread(new Runnable() {

			@Override
			public void run() {
				ArrayList<Map<String, String>> list1 = d.parseHTMLTable();
				finalEventList.addAll(list1);
			}

		}));
		threadArr.add(new Thread(new Runnable() {

			@Override
			public void run() {
				ArrayList<Map<String, String>> list2 = d.parseDivClass();
				finalEventList.addAll(list2);

			}
		}));

		for (Thread t : threadArr) {
			t.start();
		}

		try {
			for (Thread t : threadArr) {
				t.join();
			}
			addEventDetails(finalEventList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void addEventDetails(ArrayList<Map<String, String>> eventInfoList) {
		/*
		 * create table eventinfo(id int AUTO_INCREMENT primary key ,
		 * name varchar(25), startdate varchar(12),enddate varchar(12),
		 * location varchar(25), website varchar(200));
		 */
		try {

			Connection con = DatabaseConnection.getConnection();

			String insertvalues = "INSERT INTO eventinfo(name, startdate, enddate,location, website) " + "VALUES (?,?,?,?,?)";

			PreparedStatement insertRecordStmt = con.prepareStatement(insertvalues);

			for (Map<String, String> event : eventInfoList) {
				String eventName = event.get("eventName");
				String eventStartDate = event.get("eventStartDate");
				String eventEndDate = event.get("eventEndDate");
				String eventLocation = event.get("eventLocation");
				String eventWebsite = event.get("eventWebsite");
				insertRecordStmt.setString(1, eventName);
				insertRecordStmt.setString(2, eventStartDate);
				insertRecordStmt.setString(3, eventEndDate);
				insertRecordStmt.setString(4, eventLocation);
				insertRecordStmt.setString(5, eventWebsite);
				insertRecordStmt.addBatch();
			}
			insertRecordStmt.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
