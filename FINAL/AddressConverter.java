// package com.javaroots.latlong;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 * 
 * @author Abhishek Somani
 * 
 */
public class AddressConverter {
 /*
  * Geocode request URL. Here see we are passing "json" it means we will get
  * the output in JSON format. You can also pass "xml" instead of "json" for
  * XML output. For XML output URL will be
  * "http://maps.googleapis.com/maps/api/geocode/xml";
  */
   public class GoogleResponse {
   
   
       private Result[] results ;
       private String status ;
       public Result[] getResults() {
        return results;
       }
       public void setResults(Result[] results) {
        this.results = results;
       }
       public String getStatus() {
        return status;
       }
       public void setStatus(String status) {
        this.status = status;
       }
       
   }

  public class Geometry {

       private Location location ;
       private String location_type;
       @JsonIgnore
       private Object bounds;
       @JsonIgnore
       private Object viewport;
       public Location getLocation() {
          return location;
       }
       public void setLocation(Location location) {
          this.location = location;
       }
       public String getLocation_type() {
         return location_type;
       }
       public void setLocation_type(String location_type) {
        this.location_type = location_type;
       }
       public Object getBounds() {
        return bounds;
       }
       public void setBounds(Object bounds) {
        this.bounds = bounds;
       }
       public Object getViewport() {
        return viewport;
       }
       public void setViewport(Object viewport) {
        this.viewport = viewport;
       }

  }

public class Location {
 
     private String lat;
     private String lng;
     public String getLat() {
      return lat;
     }
    public void setLat(String lat) {
      this.lat = lat;
     }
    public String getLng() {
      return lng;
     }
    public void setLng(String lng) {
      this.lng = lng;
     }
 
}

public class Result {
 
     private String formatted_address;
     private boolean partial_match;
     private Geometry geometry;
     @JsonIgnore
     private Object address_components;
     @JsonIgnore
     private Object types;
     public String getFormatted_address() {
      return formatted_address;
     }
    public void setFormatted_address(String formatted_address) {
      this.formatted_address = formatted_address;
     }
    public boolean isPartial_match() {
      return partial_match;
     }
    public void setPartial_match(boolean partial_match) {
      this.partial_match = partial_match;
     }
    public Geometry getGeometry() {
      return geometry;
     }
    public void setGeometry(Geometry geometry) {
      this.geometry = geometry;
     }
    public Object getAddress_components() {
      return address_components;
     }
    public void setAddress_components(Object address_components) {
      this.address_components = address_components;
     }
    public Object getTypes() {
      return types;
     }
    public void setTypes(Object types) {
      this.types = types;
     }

}

 private static final String URL = "http://maps.googleapis.com/maps/api/geocode/json";

 /*
  * Here the fullAddress String is in format like
  * "address,city,state,zipcode". Here address means "street number + route"
  * .
  */
 public GoogleResponse convertToLatLong(String fullAddress) throws IOException {

  /*
   * Create an java.net.URL object by passing the request URL in
   * constructor. Here you can see I am converting the fullAddress String
   * in UTF-8 format. You will get Exception if you don't convert your
   * address in UTF-8 format. Perhaps google loves UTF-8 format. :) In
   * parameter we also need to pass "sensor" parameter. sensor (required
   * parameter) — Indicates whether or not the geocoding request comes
   * from a device with a location sensor. This value must be either true
   * or false.
   */
  URL url = new URL(URL + "?address="
    + URLEncoder.encode(fullAddress, "UTF-8") + "&sensor=false&key=AIzaSyC7D7j0OQ1OsfNFqn8rHWeX9r0FfVkJyGI");
  System.out.println(url);
  // Open the Connection
  URLConnection conn = url.openConnection();

  InputStream in = conn.getInputStream() ;
  ObjectMapper mapper = new ObjectMapper();
  GoogleResponse response = (GoogleResponse)mapper.readValue(in,GoogleResponse.class);
  in.close();
  return response;
  

 }
 
 public GoogleResponse convertFromLatLong(String latlongString) throws IOException {

  /*
   * Create an java.net.URL object by passing the request URL in
   * constructor. Here you can see I am converting the fullAddress String
   * in UTF-8 format. You will get Exception if you don't convert your
   * address in UTF-8 format. Perhaps google loves UTF-8 format. :) In
   * parameter we also need to pass "sensor" parameter. sensor (required
   * parameter) — Indicates whether or not the geocoding request comes
   * from a device with a location sensor. This value must be either true
   * or false.
   */
  URL url = new URL(URL + "?latlng="
    + URLEncoder.encode(latlongString, "UTF-8") + "&sensor=false");
  // Open the Connection
  System.out.println(url);
  URLConnection conn = url.openConnection();

  System.out.println("Connection Created !!");
  InputStream in = conn.getInputStream() ;
  ObjectMapper mapper = new ObjectMapper();
  GoogleResponse response = (GoogleResponse)mapper.readValue(in,GoogleResponse.class);
  in.close();
  return response;
  

 }

 
 public static void main (String[] args) throws IOException {
  
  GoogleResponse res = new AddressConverter().convertToLatLong("Mumbai");
  if(res.getStatus().equals("OK"))
  {
   for(Result result : res.getResults())
   {
    System.out.println("Lattitude of address is :"  +result.getGeometry().getLocation().getLat());
    System.out.println("Longitude of address is :" + result.getGeometry().getLocation().getLng());
    System.out.println("Location is " + result.getGeometry().getLocation_type());
   }
  }
  else
  {
   System.out.println(res.getStatus());
  }
  
  System.out.println("\n");
  GoogleResponse res1 = new AddressConverter().convertFromLatLong("18.92038860,72.83013059999999");
  if(res1.getStatus().equals("OK"))
  {
   for(Result result : res1.getResults())
   {
    System.out.println("address is :"  +result.getFormatted_address());
   }
  }
  else
  {
   System.out.println(res1.getStatus());
  }
}
 
}