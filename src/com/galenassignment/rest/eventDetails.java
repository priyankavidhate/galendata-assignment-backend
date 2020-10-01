package com.galenassignment.rest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import com.galenassignment.methods.getEventDetails;

@Path("/")
public class eventDetails {
	@GET
	@Path("/events")
	public Response getStartingPage(@DefaultValue("10") @QueryParam("limit") String limit,
			@DefaultValue("0") @QueryParam("offset") String offset, @QueryParam("sort") String sort,
			@QueryParam("filter") String filter) {
		System.out.println("sort :" + sort);
		JSONArray arr = getEventDetails.getEventDetailsWithParams(offset, limit, sort, filter);
		return Response.status(200).entity(arr.toString()).build();
	}
	
	@GET
	@Path("/locations")
	public Response getLocations() {
		JSONObject locationObj = getEventDetails.getLocations();
		return Response.status(200).entity(locationObj.toString()).build();
	}

}
